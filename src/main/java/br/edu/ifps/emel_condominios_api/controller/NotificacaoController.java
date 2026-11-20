package br.edu.ifps.emel_condominios_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifps.emel_condominios_api.model.Notificacao;
import br.edu.ifps.emel_condominios_api.service.NotificacaoService;

@RestController
@RequestMapping("/notificacao")
public class NotificacaoController {
    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @PostMapping
    public ResponseEntity<Notificacao> cadastrarNotificacao(@RequestBody Notificacao novaNotificacao) {
        Notificacao notificacao = notificacaoService.cadastrarNotificacao(novaNotificacao);
        return ResponseEntity.ok(notificacao);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacao> mostrarNotificacaoPeloId(@PathVariable Long id) {
        Notificacao notificacao = notificacaoService.mostrarNotificacao(id);
        return ResponseEntity.ok(notificacao);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacao> editarNotificacao(@PathVariable Long id, @RequestBody Notificacao novaNotificacao) {
        Notificacao notificacao = notificacaoService.editarNotificacao(id, novaNotificacao);
        return ResponseEntity.ok(notificacao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarNotificacao(@PathVariable Long id){
        notificacaoService.deletarNotificacao(id);
        return ResponseEntity.ok().build();
    }
}
