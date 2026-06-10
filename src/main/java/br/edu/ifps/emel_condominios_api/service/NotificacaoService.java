package br.edu.ifps.emel_condominios_api.service;

import org.springframework.stereotype.Service;

import br.edu.ifps.emel_condominios_api.model.Notificacao;
import br.edu.ifps.emel_condominios_api.repository.NotificacaoRepository;

@Service
public class NotificacaoService {
    
    NotificacaoRepository notificacaoRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    public Notificacao cadastrarNotificacao(Notificacao novaNotificacao){
        return notificacaoRepository.save(novaNotificacao);
    }

    public Notificacao mostrarNotificacao(long id){
        return notificacaoRepository.findById(id).orElseThrow(() -> new RuntimeException("Erro: notificação não encontrada"));
    }

    public Notificacao editarNotificacao(long id, Notificacao novaNotificacao){
        return notificacaoRepository.findById(id).map(notificacao -> {
            notificacao.setTitulo(novaNotificacao.getTitulo());
            notificacao.setDescricao(novaNotificacao.getDescricao());
            notificacao.setTipo(novaNotificacao.getTipo());
            return notificacaoRepository.save(notificacao);
        }).orElseThrow(() -> new RuntimeException("Erro: notificação não encontrada"));
    }

    public void deletarNotificacao(long id){
        notificacaoRepository.deleteById(id);
    }
}
