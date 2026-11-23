package br.edu.ifps.emel_condominios_api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para ReservaService.
 * Usa implementações fake de repositórios para evitar problemas com
 * Mockito inline mock maker no Java 25.
 */
class ReservaServiceTest {

    private FakeReservaRepository reservaRepository;
    private FakeMoradorRepository moradorRepository;
    private FakeAreaLazerRepository areaLazerRepository;
    private FakeNotificacaoRepository notificacaoRepository;
    private ReservaService reservaService;

    private Morador morador1;
    private Morador morador2;
    private AreaLazer areaLazer;

    @BeforeEach
    void setUp() {
        reservaRepository = new FakeReservaRepository();
        moradorRepository = new FakeMoradorRepository();
        areaLazerRepository = new FakeAreaLazerRepository();
        notificacaoRepository = new FakeNotificacaoRepository();

        reservaService = new ReservaService(
                reservaRepository, moradorRepository, areaLazerRepository, notificacaoRepository
        );

        // Dados de teste
        morador1 = new Morador("João", "joao@email.com", "11999999999", "11111111111", "hash1");
        morador1.setId(1L);
        moradorRepository.save(morador1);

        morador2 = new Morador("Maria", "maria@email.com", "11888888888", "22222222222", "hash2");
        morador2.setId(2L);
        moradorRepository.save(morador2);

        areaLazer = new AreaLazer("Churrasqueira", "Área de churrasco", 30, "ATIVO");
        areaLazer.setId(1L);
        areaLazerRepository.save(areaLazer);
    }

    private Reserva criarReservaValida(Morador morador) {
        Reserva r = new Reserva();
        r.setMorador(new Morador(morador.getId()));
        r.setAreaLazer(new AreaLazer(areaLazer.getId()));
        r.setDataHoraInicio(LocalDateTime.now().plusHours(25));
        r.setDataHoraFim(LocalDateTime.now().plusHours(29));
        r.setTipoEvento("Churrasco");
        r.setQtdConvidados(10);
        r.setObservacoes("Teste");
        return r;
    }

    // ========== TESTES DO FLUXO PRINCIPAL DE CRIAÇÃO ==========

    @Nested
    @DisplayName("Fluxo principal de criação")
    class CriacaoFluxoPrincipal {

        @Test
        @DisplayName("Deve criar reserva com sucesso no fluxo principal")
        void deveCriarReservaComSucesso() {
            Reserva reserva = criarReservaValida(morador1);

            Reserva resultado = reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");

            assertNotNull(resultado.getId());
            assertEquals("PENDENTE", resultado.getStatus());
            assertNotNull(resultado.getDataSolicitacao());
            assertEquals(morador1.getId(), resultado.getMorador().getId());
            assertEquals(areaLazer.getId(), resultado.getAreaLazer().getId());
        }

        @Test
        @DisplayName("Deve gerar notificação automática (RN05)")
        void deveGerarNotificacaoAutomatica() {
            Reserva reserva = criarReservaValida(morador1);

            reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");

            assertEquals(1, notificacaoRepository.count());
            Notificacao notif = notificacaoRepository.findAll().getFirst();
            assertEquals("Reserva Solicitada", notif.getTitulo());
            assertEquals("RESERVA", notif.getTipo());
            assertEquals(morador1.getId(), notif.getMorador().getId());
        }

        @Test
        @DisplayName("Deve definir data_solicitacao pelo servidor")
        void deveDefinirDataSolicitacaoPeloServidor() {
            Reserva reserva = criarReservaValida(morador1);
            reserva.setDataSolicitacao(LocalDateTime.of(2020, 1, 1, 0, 0)); // valor do cliente

            LocalDateTime antes = LocalDateTime.now();
            Reserva resultado = reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");
            LocalDateTime depois = LocalDateTime.now();

            assertTrue(!resultado.getDataSolicitacao().isBefore(antes));
            assertTrue(!resultado.getDataSolicitacao().isAfter(depois));
        }

        @Test
        @DisplayName("Deve impedir sobrescrita de reserva existente (ID ignorado)")
        void deveImpedirSobrescritaDeReserva() {
            Reserva reserva1 = criarReservaValida(morador1);
            Reserva salva = reservaService.criarReserva(reserva1, "joao@email.com", "MORADOR");
            Long idOriginal = salva.getId();

            // Tenta criar nova reserva com ID existente
            Reserva reserva2 = criarReservaValida(morador1);
            reserva2.setId(idOriginal);
            reserva2.setDataHoraInicio(LocalDateTime.now().plusHours(50));
            reserva2.setDataHoraFim(LocalDateTime.now().plusHours(54));

            Reserva resultado = reservaService.criarReserva(reserva2, "joao@email.com", "MORADOR");

            assertNotEquals(idOriginal, resultado.getId());
            assertEquals(2, reservaRepository.count());
        }

        @Test
        @DisplayName("Síndico pode criar reserva em nome de qualquer morador")
        void sindicoPodeCriarParaQualquerMorador() {
            Reserva reserva = criarReservaValida(morador1);

            Reserva resultado = reservaService.criarReserva(reserva, "sindico@email.com", "SINDICO");

            assertNotNull(resultado.getId());
            assertEquals("PENDENTE", resultado.getStatus());
        }

        @Test
        @DisplayName("Deve manter campo observações opcional")
        void deveMaterObservcoesOpcional() {
            Reserva reserva = criarReservaValida(morador1);
            reserva.setObservacoes(null);

            Reserva resultado = reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");

            assertNotNull(resultado.getId());
            assertNull(resultado.getObservacoes());
        }
    }

    // ========== TESTES DE SEGURANÇA E PROPRIEDADE ==========

    @Nested
    @DisplayName("Segurança e propriedade das reservas")
    class SegurancaPropriedade {

        @Test
        @DisplayName("Morador NÃO pode criar reserva em nome de outro morador")
        void moradorNaoPodeCriarParaOutro() {
            Reserva reserva = criarReservaValida(morador2); // morador2

            ForbiddenException ex = assertThrows(ForbiddenException.class,
                    () -> reservaService.criarReserva(reserva, "joao@email.com", "MORADOR")); // autenticado como morador1

            assertTrue(ex.getMessage().contains("permissão"));
        }

        @Test
        @DisplayName("Morador NÃO pode visualizar reserva de outro morador")
        void moradorNaoPodeVisualizarReservaDeOutro() {
            Reserva reserva = criarReservaValida(morador1);
            Reserva salva = reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");

            ForbiddenException ex = assertThrows(ForbiddenException.class,
                    () -> reservaService.mostrarReservaPorId(salva.getId(), "maria@email.com", "MORADOR"));

            assertTrue(ex.getMessage().contains("permissão"));
        }

        @Test
        @DisplayName("Morador NÃO pode cancelar reserva de outro morador")
        void moradorNaoPodeCancelarReservaDeOutro() {
            Reserva reserva = criarReservaValida(morador1);
            Reserva salva = reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");

            ForbiddenException ex = assertThrows(ForbiddenException.class,
                    () -> reservaService.cancelarReserva(salva.getId(), "maria@email.com", "MORADOR"));

            assertTrue(ex.getMessage().contains("permissão"));
        }

        @Test
        @DisplayName("Morador NÃO pode listar reservas de outro morador")
        void moradorNaoPodeListarReservasDeOutro() {
            ForbiddenException ex = assertThrows(ForbiddenException.class,
                    () -> reservaService.listarReservasPorMorador(2L, "joao@email.com", "MORADOR"));

            assertTrue(ex.getMessage().contains("permissão"));
        }

        @Test
        @DisplayName("Síndico PODE visualizar reserva de qualquer morador")
        void sindicoPodeVisualizarQualquerReserva() {
            Reserva reserva = criarReservaValida(morador1);
            Reserva salva = reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");

            Reserva resultado = reservaService.mostrarReservaPorId(salva.getId(), "sindico@email.com", "SINDICO");

            assertNotNull(resultado);
        }

        @Test
        @DisplayName("Síndico PODE cancelar reserva de qualquer morador")
        void sindicoPodeCancelarQualquerReserva() {
            Reserva reserva = criarReservaValida(morador1);
            Reserva salva = reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");

            Reserva cancelada = reservaService.cancelarReserva(salva.getId(), "sindico@email.com", "SINDICO");

            assertEquals("CANCELADA", cancelada.getStatus());
        }
    }

    // ========== TESTES RN01 – Consistência Cronológica ==========

    @Nested
    @DisplayName("RN01 – Consistência Cronológica e Antecedência")
    class RN01 {

        @Test
        @DisplayName("Deve rejeitar quando data_hora_fim é anterior a data_hora_inicio")
        void deveRejeitarFimAnteriorInicio() {
            Reserva reserva = criarReservaValida(morador1);
            reserva.setDataHoraFim(reserva.getDataHoraInicio().minusHours(1));

            assertThrows(BusinessRuleException.class,
                    () -> reservaService.criarReserva(reserva, "joao@email.com", "MORADOR"));
        }

        @Test
        @DisplayName("Deve rejeitar quando data_hora_fim é igual a data_hora_inicio")
        void deveRejeitarFimIgualInicio() {
            Reserva reserva = criarReservaValida(morador1);
            reserva.setDataHoraFim(reserva.getDataHoraInicio());

            assertThrows(BusinessRuleException.class,
                    () -> reservaService.criarReserva(reserva, "joao@email.com", "MORADOR"));
        }

        @Test
        @DisplayName("Deve rejeitar antecedência menor que 24 horas")
        void deveRejeitarAntecedenciaMenor24h() {
            Reserva reserva = criarReservaValida(morador1);
            reserva.setDataHoraInicio(LocalDateTime.now().plusHours(23));
            reserva.setDataHoraFim(LocalDateTime.now().plusHours(27));

            assertThrows(BusinessRuleException.class,
                    () -> reservaService.criarReserva(reserva, "joao@email.com", "MORADOR"));
        }

        @Test
        @DisplayName("Deve rejeitar antecedência maior que 90 dias")
        void deveRejeitarAntecedenciaMaior90Dias() {
            Reserva reserva = criarReservaValida(morador1);
            reserva.setDataHoraInicio(LocalDateTime.now().plusDays(91));
            reserva.setDataHoraFim(LocalDateTime.now().plusDays(91).plusHours(4));

            assertThrows(BusinessRuleException.class,
                    () -> reservaService.criarReserva(reserva, "joao@email.com", "MORADOR"));
        }

        @Test
        @DisplayName("Valor-limite: aceitar exatamente 24h de antecedência")
        void deveAceitarExatamente24h() {
            Reserva reserva = criarReservaValida(morador1);
            // Definir início para exatamente 24h + 1 segundo (para compensar tempo de execução)
            reserva.setDataHoraInicio(LocalDateTime.now().plusHours(24).plusSeconds(5));
            reserva.setDataHoraFim(LocalDateTime.now().plusHours(28));

            Reserva resultado = reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");

            assertNotNull(resultado.getId());
        }

        @Test
        @DisplayName("Deve rejeitar quando datas são nulas")
        void deveRejeitarDatasNulas() {
            Reserva reserva = criarReservaValida(morador1);
            reserva.setDataHoraInicio(null);
            reserva.setDataHoraFim(null);

            assertThrows(BadRequestException.class,
                    () -> reservaService.criarReserva(reserva, "joao@email.com", "MORADOR"));
        }
    }

    // ========== TESTES RN02 – Conflito de Horários ==========

    @Nested
    @DisplayName("RN02 – Impedimento de Conflito de Horários")
    class RN02 {

        @Test
        @DisplayName("Deve rejeitar reserva com conflito de horários")
        void deveRejeitarConflitoHorarios() {
            Reserva reserva1 = criarReservaValida(morador1);
            reservaService.criarReserva(reserva1, "joao@email.com", "MORADOR");

            // Mesma área, mesmo período
            Reserva reserva2 = criarReservaValida(morador2);
            reserva2.setDataHoraInicio(reserva1.getDataHoraInicio());
            reserva2.setDataHoraFim(reserva1.getDataHoraFim());

            assertThrows(ConflictException.class,
                    () -> reservaService.criarReserva(reserva2, "maria@email.com", "MORADOR"));
        }
    }

    // ========== TESTES RN03 – Capacidade de Convidados ==========

    @Nested
    @DisplayName("RN03 – Limite de Capacidade de Convidados")
    class RN03 {

        @Test
        @DisplayName("Deve rejeitar qtd_convidados = 0")
        void deveRejeitarConvidadosZero() {
            Reserva reserva = criarReservaValida(morador1);
            reserva.setQtdConvidados(0);

            assertThrows(BadRequestException.class,
                    () -> reservaService.criarReserva(reserva, "joao@email.com", "MORADOR"));
        }

        @Test
        @DisplayName("Deve rejeitar qtd_convidados negativa")
        void deveRejeitarConvidadosNegativo() {
            Reserva reserva = criarReservaValida(morador1);
            reserva.setQtdConvidados(-1);

            assertThrows(BadRequestException.class,
                    () -> reservaService.criarReserva(reserva, "joao@email.com", "MORADOR"));
        }

        @Test
        @DisplayName("Deve rejeitar qtd_convidados acima da capacidade máxima")
        void deveRejeitarAcimaCapacidade() {
            Reserva reserva = criarReservaValida(morador1);
            reserva.setQtdConvidados(31); // capacidade = 30

            assertThrows(BusinessRuleException.class,
                    () -> reservaService.criarReserva(reserva, "joao@email.com", "MORADOR"));
        }

        @Test
        @DisplayName("Valor-limite: aceitar qtd_convidados = 1")
        void deveAceitarConvidadosUm() {
            Reserva reserva = criarReservaValida(morador1);
            reserva.setQtdConvidados(1);

            Reserva resultado = reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");

            assertNotNull(resultado.getId());
        }

        @Test
        @DisplayName("Valor-limite: aceitar qtd_convidados = capacidade_maxima")
        void deveAceitarConvidadosIgualCapacidade() {
            Reserva reserva = criarReservaValida(morador1);
            reserva.setQtdConvidados(30); // = capacidade

            Reserva resultado = reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");

            assertNotNull(resultado.getId());
        }
    }

    // ========== TESTES RN04 – Cota de Reservas Ativas ==========

    @Nested
    @DisplayName("RN04 – Cota de Reservas Ativas por Morador")
    class RN04 {

        @Test
        @DisplayName("Deve rejeitar terceira reserva ativa")
        void deveRejeitarTerceiraReserva() {
            Reserva r1 = criarReservaValida(morador1);
            r1.setDataHoraInicio(LocalDateTime.now().plusHours(25));
            r1.setDataHoraFim(LocalDateTime.now().plusHours(29));
            reservaService.criarReserva(r1, "joao@email.com", "MORADOR");

            Reserva r2 = criarReservaValida(morador1);
            r2.setDataHoraInicio(LocalDateTime.now().plusHours(50));
            r2.setDataHoraFim(LocalDateTime.now().plusHours(54));
            reservaService.criarReserva(r2, "joao@email.com", "MORADOR");

            Reserva r3 = criarReservaValida(morador1);
            r3.setDataHoraInicio(LocalDateTime.now().plusHours(75));
            r3.setDataHoraFim(LocalDateTime.now().plusHours(79));

            assertThrows(BusinessRuleException.class,
                    () -> reservaService.criarReserva(r3, "joao@email.com", "MORADOR"));
        }

        @Test
        @DisplayName("Valor-limite: aceitar segunda reserva ativa")
        void deveAceitarSegundaReserva() {
            Reserva r1 = criarReservaValida(morador1);
            r1.setDataHoraInicio(LocalDateTime.now().plusHours(25));
            r1.setDataHoraFim(LocalDateTime.now().plusHours(29));
            reservaService.criarReserva(r1, "joao@email.com", "MORADOR");

            Reserva r2 = criarReservaValida(morador1);
            r2.setDataHoraInicio(LocalDateTime.now().plusHours(50));
            r2.setDataHoraFim(LocalDateTime.now().plusHours(54));

            Reserva resultado = reservaService.criarReserva(r2, "joao@email.com", "MORADOR");

            assertNotNull(resultado.getId());
        }
    }

    // ========== TESTES RN06 – Cancelamento ==========

    @Nested
    @DisplayName("RN06 – Cancelamento de Reservas")
    class RN06 {

        @Test
        @DisplayName("Deve cancelar reserva PENDENTE com antecedência suficiente")
        void deveCancelarPendente() {
            Reserva reserva = criarReservaValida(morador1);
            Reserva salva = reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");

            Reserva cancelada = reservaService.cancelarReserva(salva.getId(), "joao@email.com", "MORADOR");

            assertEquals("CANCELADA", cancelada.getStatus());
        }

        @Test
        @DisplayName("Deve gerar notificação de cancelamento")
        void deveGerarNotificacaoCancelamento() {
            Reserva reserva = criarReservaValida(morador1);
            Reserva salva = reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");

            reservaService.cancelarReserva(salva.getId(), "joao@email.com", "MORADOR");

            // 1 notificação de criação + 1 de cancelamento
            assertEquals(2, notificacaoRepository.count());
            List<Notificacao> notificacoes = notificacaoRepository.findAll();
            boolean temCancelamento = notificacoes.stream()
                    .anyMatch(n -> "Reserva Cancelada".equals(n.getTitulo()));
            assertTrue(temCancelamento);
        }

        @Test
        @DisplayName("Deve rejeitar cancelamento de reserva já CANCELADA")
        void deveRejeitarCancelamentoDeJaCancelada() {
            Reserva reserva = criarReservaValida(morador1);
            Reserva salva = reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");
            reservaService.cancelarReserva(salva.getId(), "joao@email.com", "MORADOR");

            assertThrows(BusinessRuleException.class,
                    () -> reservaService.cancelarReserva(salva.getId(), "joao@email.com", "MORADOR"));
        }

        @Test
        @DisplayName("Deve rejeitar cancelamento de reserva RECUSADA")
        void deveRejeitarCancelamentoDeRecusada() {
            Reserva reserva = criarReservaValida(morador1);
            Reserva salva = reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");
            reservaService.recusarReserva(salva.getId());

            assertThrows(BusinessRuleException.class,
                    () -> reservaService.cancelarReserva(salva.getId(), "joao@email.com", "MORADOR"));
        }

        @Test
        @DisplayName("Deve rejeitar cancelamento com menos de 24h de antecedência")
        void deveRejeitarCancelamentoSemAntecedencia() {
            Reserva reserva = criarReservaValida(morador1);
            Reserva salva = reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");

            // Forçar data_hora_inicio para daqui a 23h (menos que 24h de agora)
            salva.setDataHoraInicio(LocalDateTime.now().plusHours(23));
            reservaRepository.save(salva);

            assertThrows(BusinessRuleException.class,
                    () -> reservaService.cancelarReserva(salva.getId(), "joao@email.com", "MORADOR"));
        }

        @Test
        @DisplayName("Deve preservar status original quando cancelamento é rejeitado")
        void devePreservarStatusQuandoRejeitado() {
            Reserva reserva = criarReservaValida(morador1);
            Reserva salva = reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");
            salva.setDataHoraInicio(LocalDateTime.now().plusHours(23));
            reservaRepository.save(salva);

            try {
                reservaService.cancelarReserva(salva.getId(), "joao@email.com", "MORADOR");
            } catch (BusinessRuleException ignored) {}

            Reserva verificada = reservaRepository.findById(salva.getId()).orElseThrow();
            assertEquals("PENDENTE", verificada.getStatus());
        }

        @Test
        @DisplayName("Deve cancelar reserva CONFIRMADA")
        void deveCancelarConfirmada() {
            Reserva reserva = criarReservaValida(morador1);
            Reserva salva = reservaService.criarReserva(reserva, "joao@email.com", "MORADOR");
            reservaService.confirmarReserva(salva.getId());

            Reserva cancelada = reservaService.cancelarReserva(salva.getId(), "joao@email.com", "MORADOR");

            assertEquals("CANCELADA", cancelada.getStatus());
        }
    }

    // ========== TESTE DE ÁREA INDISPONÍVEL ==========

    @Nested
    @DisplayName("Bloqueio de áreas indisponíveis")
    class AreaIndisponivel {

        @Test
        @DisplayName("Deve rejeitar reserva em área em MANUTENCAO")
        void deveRejeitarAreaEmManutencao() {
            areaLazer.setStatus("MANUTENCAO");
            areaLazerRepository.save(areaLazer);

            Reserva reserva = criarReservaValida(morador1);

            assertThrows(BusinessRuleException.class,
                    () -> reservaService.criarReserva(reserva, "joao@email.com", "MORADOR"));
        }

        @Test
        @DisplayName("Deve rejeitar reserva em área INATIVA")
        void deveRejeitarAreaInativa() {
            areaLazer.setStatus("INATIVO");
            areaLazerRepository.save(areaLazer);

            Reserva reserva = criarReservaValida(morador1);

            assertThrows(BusinessRuleException.class,
                    () -> reservaService.criarReserva(reserva, "joao@email.com", "MORADOR"));
        }
    }

    // ========== TESTE DE RECURSO NÃO ENCONTRADO ==========

    @Nested
    @DisplayName("Recursos inexistentes")
    class RecursoInexistente {

        @Test
        @DisplayName("Deve retornar 404 para morador inexistente")
        void deveRetornar404MoradorInexistente() {
            Reserva reserva = criarReservaValida(morador1);
            reserva.setMorador(new Morador(999L));

            assertThrows(ResourceNotFoundException.class,
                    () -> reservaService.criarReserva(reserva, "joao@email.com", "SINDICO"));
        }

        @Test
        @DisplayName("Deve retornar 404 para área de lazer inexistente")
        void deveRetornar404AreaInexistente() {
            Reserva reserva = criarReservaValida(morador1);
            reserva.setAreaLazer(new AreaLazer(999L));

            assertThrows(ResourceNotFoundException.class,
                    () -> reservaService.criarReserva(reserva, "joao@email.com", "MORADOR"));
        }

        @Test
        @DisplayName("Deve retornar 404 para reserva inexistente no cancelamento")
        void deveRetornar404ReservaInexistente() {
            assertThrows(ResourceNotFoundException.class,
                    () -> reservaService.cancelarReserva(999L, "joao@email.com", "MORADOR"));
        }
    }

    // ========== FAKE REPOSITORIES ==========

    /**
     * Implementação fake do ReservaRepository para testes unitários.
     */
    static class FakeReservaRepository implements ReservaRepository {
        private final Map<Long, Reserva> store = new HashMap<>();
        private final AtomicLong idGen = new AtomicLong(1);

        @Override
        public long countReservasAtivasPorMorador(Long moradorId) {
            return store.values().stream()
                    .filter(r -> r.getMorador() != null && moradorId.equals(r.getMorador().getId()))
                    .filter(r -> "PENDENTE".equalsIgnoreCase(r.getStatus()) || "CONFIRMADA".equalsIgnoreCase(r.getStatus()))
                    .count();
        }

        @Override
        public List<Reserva> findConflitos(Long areaLazerId, LocalDateTime inicio, LocalDateTime fim, Long reservaId) {
            return store.values().stream()
                    .filter(r -> r.getAreaLazer() != null && areaLazerId.equals(r.getAreaLazer().getId()))
                    .filter(r -> "PENDENTE".equalsIgnoreCase(r.getStatus()) || "CONFIRMADA".equalsIgnoreCase(r.getStatus()))
                    .filter(r -> reservaId == null || !reservaId.equals(r.getId()))
                    .filter(r -> r.getDataHoraInicio().isBefore(fim) && r.getDataHoraFim().isAfter(inicio))
                    .collect(Collectors.toList());
        }

        @Override
        public List<Reserva> findByMoradorId(Long moradorId) {
            return store.values().stream()
                    .filter(r -> r.getMorador() != null && moradorId.equals(r.getMorador().getId()))
                    .collect(Collectors.toList());
        }

        @Override
        public List<Reserva> findByAreaLazerId(Long areaLazerId) {
            return store.values().stream()
                    .filter(r -> r.getAreaLazer() != null && areaLazerId.equals(r.getAreaLazer().getId()))
                    .collect(Collectors.toList());
        }

        @Override
        public <S extends Reserva> S save(S entity) {
            if (entity.getId() == null) {
                entity.setId(idGen.getAndIncrement());
            }
            store.put(entity.getId(), entity);
            @SuppressWarnings("unchecked")
            S result = (S) entity;
            return result;
        }

        @Override public Optional<Reserva> findById(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public List<Reserva> findAll() { return new ArrayList<>(store.values()); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.remove(id); }
        @Override public void delete(Reserva entity) { store.remove(entity.getId()); }
        @Override public boolean existsById(Long id) { return store.containsKey(id); }

        // Unused JpaRepository methods - minimal stubs
        @Override public <S extends Reserva> List<S> saveAll(Iterable<S> entities) { throw new UnsupportedOperationException(); }
        @Override public void flush() {}
        @Override public <S extends Reserva> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Reserva> List<S> saveAllAndFlush(Iterable<S> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllInBatch(Iterable<Reserva> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllByIdInBatch(Iterable<Long> longs) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllInBatch() { throw new UnsupportedOperationException(); }
        @Override public Reserva getReferenceById(Long id) { return store.get(id); }
        @Override public <S extends Reserva> Optional<S> findOne(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends Reserva> List<S> findAll(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends Reserva> List<S> findAll(Example<S> example, Sort sort) { throw new UnsupportedOperationException(); }
        @Override public <S extends Reserva> Page<S> findAll(Example<S> example, Pageable pageable) { throw new UnsupportedOperationException(); }
        @Override public <S extends Reserva> long count(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends Reserva> boolean exists(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends Reserva, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { throw new UnsupportedOperationException(); }
        @Override public List<Reserva> findAll(Sort sort) { throw new UnsupportedOperationException(); }
        @Override public Page<Reserva> findAll(Pageable pageable) { throw new UnsupportedOperationException(); }
        @Override public List<Reserva> findAllById(Iterable<Long> ids) { throw new UnsupportedOperationException(); }
        @Override public void deleteAll(Iterable<? extends Reserva> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAll() { store.clear(); }
    }

    static class FakeMoradorRepository implements MoradorRepository {
        private final Map<Long, Morador> store = new HashMap<>();
        private final AtomicLong idGen = new AtomicLong(1);

        @Override
        public Optional<Morador> findByEmail(String email) {
            return store.values().stream().filter(m -> email.equals(m.getEmail())).findFirst();
        }

        @Override
        public <S extends Morador> S save(S entity) {
            if (entity.getId() == null) entity.setId(idGen.getAndIncrement());
            store.put(entity.getId(), entity);
            @SuppressWarnings("unchecked") S result = (S) entity;
            return result;
        }

        @Override public Optional<Morador> findById(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public List<Morador> findAll() { return new ArrayList<>(store.values()); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.remove(id); }
        @Override public void delete(Morador entity) { store.remove(entity.getId()); }
        @Override public boolean existsById(Long id) { return store.containsKey(id); }
        @Override public <S extends Morador> List<S> saveAll(Iterable<S> entities) { throw new UnsupportedOperationException(); }
        @Override public void flush() {}
        @Override public <S extends Morador> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Morador> List<S> saveAllAndFlush(Iterable<S> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllInBatch(Iterable<Morador> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllByIdInBatch(Iterable<Long> longs) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllInBatch() { throw new UnsupportedOperationException(); }
        @Override public Morador getReferenceById(Long id) { return store.get(id); }
        @Override public <S extends Morador> Optional<S> findOne(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends Morador> List<S> findAll(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends Morador> List<S> findAll(Example<S> example, Sort sort) { throw new UnsupportedOperationException(); }
        @Override public <S extends Morador> Page<S> findAll(Example<S> example, Pageable pageable) { throw new UnsupportedOperationException(); }
        @Override public <S extends Morador> long count(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends Morador> boolean exists(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends Morador, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { throw new UnsupportedOperationException(); }
        @Override public List<Morador> findAll(Sort sort) { throw new UnsupportedOperationException(); }
        @Override public Page<Morador> findAll(Pageable pageable) { throw new UnsupportedOperationException(); }
        @Override public List<Morador> findAllById(Iterable<Long> ids) { throw new UnsupportedOperationException(); }
        @Override public void deleteAll(Iterable<? extends Morador> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAll() { store.clear(); }
    }

    static class FakeAreaLazerRepository implements AreaLazerRepository {
        private final Map<Long, AreaLazer> store = new HashMap<>();
        private final AtomicLong idGen = new AtomicLong(1);

        @Override
        public <S extends AreaLazer> S save(S entity) {
            if (entity.getId() == null) entity.setId(idGen.getAndIncrement());
            store.put(entity.getId(), entity);
            @SuppressWarnings("unchecked") S result = (S) entity;
            return result;
        }

        @Override public Optional<AreaLazer> findById(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public List<AreaLazer> findAll() { return new ArrayList<>(store.values()); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.remove(id); }
        @Override public void delete(AreaLazer entity) { store.remove(entity.getId()); }
        @Override public boolean existsById(Long id) { return store.containsKey(id); }
        @Override public <S extends AreaLazer> List<S> saveAll(Iterable<S> entities) { throw new UnsupportedOperationException(); }
        @Override public void flush() {}
        @Override public <S extends AreaLazer> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends AreaLazer> List<S> saveAllAndFlush(Iterable<S> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllInBatch(Iterable<AreaLazer> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllByIdInBatch(Iterable<Long> longs) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllInBatch() { throw new UnsupportedOperationException(); }
        @Override public AreaLazer getReferenceById(Long id) { return store.get(id); }
        @Override public <S extends AreaLazer> Optional<S> findOne(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends AreaLazer> List<S> findAll(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends AreaLazer> List<S> findAll(Example<S> example, Sort sort) { throw new UnsupportedOperationException(); }
        @Override public <S extends AreaLazer> Page<S> findAll(Example<S> example, Pageable pageable) { throw new UnsupportedOperationException(); }
        @Override public <S extends AreaLazer> long count(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends AreaLazer> boolean exists(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends AreaLazer, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { throw new UnsupportedOperationException(); }
        @Override public List<AreaLazer> findAll(Sort sort) { throw new UnsupportedOperationException(); }
        @Override public Page<AreaLazer> findAll(Pageable pageable) { throw new UnsupportedOperationException(); }
        @Override public List<AreaLazer> findAllById(Iterable<Long> ids) { throw new UnsupportedOperationException(); }
        @Override public void deleteAll(Iterable<? extends AreaLazer> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAll() { store.clear(); }
    }

    static class FakeNotificacaoRepository implements NotificacaoRepository {
        private final Map<Long, Notificacao> store = new HashMap<>();
        private final AtomicLong idGen = new AtomicLong(1);
        boolean failOnSave = false;

        @Override
        public <S extends Notificacao> S save(S entity) {
            if (failOnSave) throw new RuntimeException("Falha simulada ao salvar notificação");
            if (entity.getId() == null) entity.setId(idGen.getAndIncrement());
            store.put(entity.getId(), entity);
            @SuppressWarnings("unchecked") S result = (S) entity;
            return result;
        }

        @Override public Optional<Notificacao> findById(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public List<Notificacao> findAll() { return new ArrayList<>(store.values()); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.remove(id); }
        @Override public void delete(Notificacao entity) { store.remove(entity.getId()); }
        @Override public boolean existsById(Long id) { return store.containsKey(id); }
        @Override public <S extends Notificacao> List<S> saveAll(Iterable<S> entities) { throw new UnsupportedOperationException(); }
        @Override public void flush() {}
        @Override public <S extends Notificacao> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Notificacao> List<S> saveAllAndFlush(Iterable<S> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllInBatch(Iterable<Notificacao> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllByIdInBatch(Iterable<Long> longs) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllInBatch() { throw new UnsupportedOperationException(); }
        @Override public Notificacao getReferenceById(Long id) { return store.get(id); }
        @Override public <S extends Notificacao> Optional<S> findOne(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends Notificacao> List<S> findAll(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends Notificacao> List<S> findAll(Example<S> example, Sort sort) { throw new UnsupportedOperationException(); }
        @Override public <S extends Notificacao> Page<S> findAll(Example<S> example, Pageable pageable) { throw new UnsupportedOperationException(); }
        @Override public <S extends Notificacao> long count(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends Notificacao> boolean exists(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends Notificacao, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { throw new UnsupportedOperationException(); }
        @Override public List<Notificacao> findAll(Sort sort) { throw new UnsupportedOperationException(); }
        @Override public Page<Notificacao> findAll(Pageable pageable) { throw new UnsupportedOperationException(); }
        @Override public List<Notificacao> findAllById(Iterable<Long> ids) { throw new UnsupportedOperationException(); }
        @Override public void deleteAll(Iterable<? extends Notificacao> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAll() { store.clear(); }
    }
}
