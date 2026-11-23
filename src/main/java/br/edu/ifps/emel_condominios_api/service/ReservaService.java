package br.edu.ifps.emel_condominios_api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifps.emel_condominios_api.exception.BadRequestException;
import br.edu.ifps.emel_condominios_api.exception.BusinessRuleException;
import br.edu.ifps.emel_condominios_api.exception.ConflictException;
import br.edu.ifps.emel_condominios_api.exception.ForbiddenException;
import br.edu.ifps.emel_condominios_api.exception.ResourceNotFoundException;
import br.edu.ifps.emel_condominios_api.model.AreaLazer;
import br.edu.ifps.emel_condominios_api.model.Morador;
import br.edu.ifps.emel_condominios_api.model.Notificacao;
import br.edu.ifps.emel_condominios_api.model.Reserva;
import br.edu.ifps.emel_condominios_api.repository.AreaLazerRepository;
import br.edu.ifps.emel_condominios_api.repository.MoradorRepository;
import br.edu.ifps.emel_condominios_api.repository.NotificacaoRepository;
import br.edu.ifps.emel_condominios_api.repository.ReservaRepository;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final MoradorRepository moradorRepository;
    private final AreaLazerRepository areaLazerRepository;
    private final NotificacaoRepository notificacaoRepository;

    public ReservaService(ReservaRepository reservaRepository,
                          MoradorRepository moradorRepository,
                          AreaLazerRepository areaLazerRepository,
                          NotificacaoRepository notificacaoRepository) {
        this.reservaRepository = reservaRepository;
        this.moradorRepository = moradorRepository;
        this.areaLazerRepository = areaLazerRepository;
        this.notificacaoRepository = notificacaoRepository;
    }

    /**
     * Busca o Morador pelo e-mail extraído do JWT.
     * Retorna null se o e-mail for nulo (caso de síndico sem cadastro de morador).
     */
    public Morador buscarMoradorPorEmail(String email) {
        if (email == null) return null;
        return moradorRepository.findByEmail(email).orElse(null);
    }

    /**
     * Cria uma reserva garantindo:
     * - data_solicitacao definida pelo servidor (item 4)
     * - ID nulo para impedir sobrescrita (item 4)
     * - Propriedade do morador via JWT quando role = MORADOR (item 1)
     * - Atomicidade reserva + notificação via @Transactional (item 3)
     * - Todas as RNs (RN01 a RN05)
     */
    @Transactional
    public Reserva criarReserva(Reserva reserva, String emailAutenticado, String roleAutenticado) {
        // Impedir sobrescrita de reserva existente
        reserva.setId(null);

        // data_solicitacao sempre definida pelo servidor
        reserva.setDataSolicitacao(LocalDateTime.now());

        if (reserva.getMorador() == null || reserva.getMorador().getId() == null) {
            throw new BadRequestException("Erro: O morador solicitante (morador_id) deve ser informado.");
        }
        if (reserva.getAreaLazer() == null || reserva.getAreaLazer().getId() == null) {
            throw new BadRequestException("Erro: A área de lazer (area_lazer_id) deve ser informada.");
        }

        Morador morador = moradorRepository.findById(reserva.getMorador().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Morador não encontrado"));

        // Verificar propriedade: MORADOR só pode criar reserva para si mesmo
        if ("MORADOR".equalsIgnoreCase(roleAutenticado)) {
            if (!morador.getEmail().equals(emailAutenticado)) {
                throw new ForbiddenException("Erro: Você não tem permissão para criar reservas em nome de outro morador.");
            }
        }

        AreaLazer areaLazer = areaLazerRepository.findById(reserva.getAreaLazer().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Área de lazer não encontrada"));

        // Bloqueio de áreas inativas ou em manutenção
        if (areaLazer.getStatus() != null) {
            String statusArea = areaLazer.getStatus().trim().toUpperCase();
            if ("MANUTENCAO".equals(statusArea) || "MANUTENÇÃO".equals(statusArea) ||
                "INATIVO".equals(statusArea) || "INATIVA".equals(statusArea) ||
                "BLOQUEADA".equals(statusArea) || "BLOQUEADO".equals(statusArea) ||
                "INDISPONIVEL".equals(statusArea) || "INDISPONÍVEL".equals(statusArea)) {
                throw new BusinessRuleException("Erro: A área de lazer '" + areaLazer.getNome() +
                        "' está indisponível para reservas no momento (status: " + areaLazer.getStatus() + ").");
            }
        }

        LocalDateTime dataSolicitacao = reserva.getDataSolicitacao();
        LocalDateTime dataHoraInicio = reserva.getDataHoraInicio();
        LocalDateTime dataHoraFim = reserva.getDataHoraFim();

        // RN01 – Consistência Cronológica e Antecedência
        if (dataHoraInicio == null || dataHoraFim == null) {
            throw new BadRequestException("Erro (RN01): As datas e horários de início e fim são obrigatórios.");
        }

        if (!dataHoraFim.isAfter(dataHoraInicio)) {
            throw new BusinessRuleException("Erro (RN01): A data/hora de fim deve ser obrigatoriamente posterior à data/hora de início.");
        }

        LocalDateTime minInicio = dataSolicitacao.plusHours(24);
        LocalDateTime maxInicio = dataSolicitacao.plusDays(90);

        if (dataHoraInicio.isBefore(minInicio)) {
            throw new BusinessRuleException("Erro (RN01): A reserva deve ser solicitada com antecedência mínima de 24 horas a partir do momento da solicitação.");
        }

        if (dataHoraInicio.isAfter(maxInicio)) {
            throw new BusinessRuleException("Erro (RN01): A data/hora de início não pode ultrapassar o limite máximo de 90 dias a partir do momento da solicitação.");
        }

        // RN02 – Impedimento de Conflito de Horários
        List<Reserva> conflitos = reservaRepository.findConflitos(
                areaLazer.getId(),
                dataHoraInicio,
                dataHoraFim,
                null
        );
        if (!conflitos.isEmpty()) {
            throw new ConflictException("Erro (RN02): Conflito de horários! Já existe uma reserva ativa (PENDENTE ou CONFIRMADA) para esta área de lazer no período informado.");
        }

        // RN03 – Limite de Capacidade de Convidados
        Integer qtdConvidados = reserva.getQtdConvidados();
        if (qtdConvidados == null || qtdConvidados < 1) {
            throw new BadRequestException("Erro (RN03): O número de convidados deve ser maior ou igual a 1.");
        }
        if (areaLazer.getCapacidadeMaxima() != null && qtdConvidados > areaLazer.getCapacidadeMaxima()) {
            throw new BusinessRuleException("Erro (RN03): O número de convidados (" + qtdConvidados +
                    ") ultrapassa a capacidade máxima da área de lazer selecionada (" + areaLazer.getCapacidadeMaxima() + ").");
        }

        // RN04 – Cota de Reservas Ativas por Morador
        long reservasAtivas = reservaRepository.countReservasAtivasPorMorador(morador.getId());
        if (reservasAtivas >= 2) {
            throw new BusinessRuleException("Erro (RN04): O morador já atingiu o limite de 2 reservas ativas (PENDENTE ou CONFIRMADA) simultaneamente no sistema.");
        }

        // RN05 – Status Inicial e Notificação Automática
        reserva.setStatus("PENDENTE");
        reserva.setMorador(morador);
        reserva.setAreaLazer(areaLazer);

        Reserva reservaSalva = reservaRepository.save(reserva);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dataFormatada = reservaSalva.getDataHoraInicio().format(formatter);
        Notificacao notificacao = new Notificacao(
                "Reserva Solicitada",
                "Sua solicitação de reserva para a área '" + areaLazer.getNome() + "' em " + dataFormatada +
                        " foi cadastrada com sucesso e está com status PENDENTE (aguardando confirmação do síndico).",
                "RESERVA",
                LocalDate.now(),
                morador
        );
        notificacaoRepository.save(notificacao);

        return reservaSalva;
    }

    /**
     * RN06 – Cancelamento com antecedência mínima de 24h.
     * Somente reservas PENDENTE ou CONFIRMADA podem ser canceladas.
     * MORADOR só pode cancelar suas próprias reservas.
     * Permite cancelamento exatamente no limite de 24h (>=).
     */
    @Transactional
    public Reserva cancelarReserva(Long id, String emailAutenticado, String roleAutenticado) {
        Reserva reserva = mostrarReservaPorId(id);

        // Verificar propriedade: MORADOR só pode cancelar suas próprias reservas
        if ("MORADOR".equalsIgnoreCase(roleAutenticado)) {
            if (reserva.getMorador() == null || !reserva.getMorador().getEmail().equals(emailAutenticado)) {
                throw new ForbiddenException("Erro: Você não tem permissão para cancelar a reserva de outro morador.");
            }
        }

        // Permitir cancelar somente PENDENTE ou CONFIRMADA
        String statusAtual = reserva.getStatus();
        if ("CANCELADA".equalsIgnoreCase(statusAtual)) {
            throw new BusinessRuleException("Erro: A reserva já se encontra cancelada.");
        }
        if ("RECUSADA".equalsIgnoreCase(statusAtual)) {
            throw new BusinessRuleException("Erro: Não é possível cancelar uma reserva que foi recusada.");
        }
        if (!"PENDENTE".equalsIgnoreCase(statusAtual) && !"CONFIRMADA".equalsIgnoreCase(statusAtual)) {
            throw new BusinessRuleException("Erro: Somente reservas com status PENDENTE ou CONFIRMADA podem ser canceladas.");
        }

        // RN06 – Antecedência mínima de 24h (permite exatamente no limite)
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime limiteMinimo = reserva.getDataHoraInicio().minusHours(24);
        if (agora.isAfter(limiteMinimo)) {
            throw new BusinessRuleException("Erro (RN06): O cancelamento só pode ser realizado com antecedência mínima de 24 horas antes da data/hora de início agendada.");
        }

        reserva.setStatus("CANCELADA");
        Reserva reservaAtualizada = reservaRepository.save(reserva);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dataFormatada = reserva.getDataHoraInicio().format(formatter);
        Notificacao notificacao = new Notificacao(
                "Reserva Cancelada",
                "Sua reserva para a área '" + reserva.getAreaLazer().getNome() + "' agendada para " + dataFormatada +
                        " foi CANCELADA com sucesso.",
                "CANCELAMENTO",
                LocalDate.now(),
                reserva.getMorador()
        );
        notificacaoRepository.save(notificacao);

        return reservaAtualizada;
    }

    @Transactional
    public Reserva confirmarReserva(Long id) {
        Reserva reserva = mostrarReservaPorId(id);

        if (!"PENDENTE".equalsIgnoreCase(reserva.getStatus())) {
            throw new BusinessRuleException("Erro: Apenas reservas com status PENDENTE podem ser confirmadas.");
        }

        reserva.setStatus("CONFIRMADA");
        Reserva reservaAtualizada = reservaRepository.save(reserva);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dataFormatada = reserva.getDataHoraInicio().format(formatter);
        Notificacao notificacao = new Notificacao(
                "Reserva Confirmada",
                "Sua reserva para a área '" + reserva.getAreaLazer().getNome() + "' agendada para " + dataFormatada +
                        " foi CONFIRMADA pelo síndico.",
                "CONFIRMACAO",
                LocalDate.now(),
                reserva.getMorador()
        );
        notificacaoRepository.save(notificacao);

        return reservaAtualizada;
    }

    @Transactional
    public Reserva recusarReserva(Long id) {
        Reserva reserva = mostrarReservaPorId(id);

        if (!"PENDENTE".equalsIgnoreCase(reserva.getStatus())) {
            throw new BusinessRuleException("Erro: Apenas reservas com status PENDENTE podem ser recusadas.");
        }

        reserva.setStatus("RECUSADA");
        Reserva reservaAtualizada = reservaRepository.save(reserva);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dataFormatada = reserva.getDataHoraInicio().format(formatter);
        Notificacao notificacao = new Notificacao(
                "Reserva Recusada",
                "Sua solicitação de reserva para a área '" + reserva.getAreaLazer().getNome() + "' agendada para " + dataFormatada +
                        " foi RECUSADA pelo síndico.",
                "RECUSA",
                LocalDate.now(),
                reserva.getMorador()
        );
        notificacaoRepository.save(notificacao);

        return reservaAtualizada;
    }

    public List<Reserva> listarTodasReservas() {
        return reservaRepository.findAll();
    }

    public Reserva mostrarReservaPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Reserva não encontrada"));
    }

    /**
     * Mostra reserva por ID verificando propriedade para MORADOR.
     */
    public Reserva mostrarReservaPorId(Long id, String emailAutenticado, String roleAutenticado) {
        Reserva reserva = mostrarReservaPorId(id);
        if ("MORADOR".equalsIgnoreCase(roleAutenticado)) {
            if (reserva.getMorador() == null || !reserva.getMorador().getEmail().equals(emailAutenticado)) {
                throw new ForbiddenException("Erro: Você não tem permissão para visualizar a reserva de outro morador.");
            }
        }
        return reserva;
    }

    public List<Reserva> listarReservasPorMorador(Long moradorId) {
        return reservaRepository.findByMoradorId(moradorId);
    }

    /**
     * Lista reservas por morador verificando propriedade para MORADOR.
     */
    public List<Reserva> listarReservasPorMorador(Long moradorId, String emailAutenticado, String roleAutenticado) {
        if ("MORADOR".equalsIgnoreCase(roleAutenticado)) {
            Morador moradorAutenticado = moradorRepository.findByEmail(emailAutenticado)
                    .orElseThrow(() -> new ResourceNotFoundException("Erro: Morador autenticado não encontrado"));
            if (!moradorAutenticado.getId().equals(moradorId)) {
                throw new ForbiddenException("Erro: Você não tem permissão para visualizar as reservas de outro morador.");
            }
        }
        return reservaRepository.findByMoradorId(moradorId);
    }

    public List<Reserva> listarReservasPorAreaLazer(Long areaLazerId) {
        return reservaRepository.findByAreaLazerId(areaLazerId);
    }

    public void deletarReserva(Long id) {
        reservaRepository.deleteById(id);
    }
}
