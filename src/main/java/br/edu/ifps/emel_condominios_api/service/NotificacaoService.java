package br.edu.ifps.emel_condominios_api.service;

import org.springframework.stereotype.Service;

import br.edu.ifps.emel_condominios_api.exception.ResourceNotFoundException;
import br.edu.ifps.emel_condominios_api.model.Notificacao;
import br.edu.ifps.emel_condominios_api.repository.NotificacaoRepository;

@Service
public class NotificacaoService {
    
    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    public Notificacao cadastrarNotificacao(Notificacao novaNotificacao){
        return notificacaoRepository.save(novaNotificacao);
    }

    public Notificacao mostrarNotificacao(Long id){
        return notificacaoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Erro: notificação não encontrada"));
    }

    public Notificacao editarNotificacao(Long id, Notificacao novaNotificacao){
        return notificacaoRepository.findById(id).map(notificacao -> {
            notificacao.setTitulo(novaNotificacao.getTitulo());
            notificacao.setDescricao(novaNotificacao.getDescricao());
            notificacao.setTipo(novaNotificacao.getTipo());
            return notificacaoRepository.save(notificacao);
        }).orElseThrow(() -> new ResourceNotFoundException("Erro: notificação não encontrada"));
    }

    public void deletarNotificacao(Long id){
        notificacaoRepository.deleteById(id);
    }
}
