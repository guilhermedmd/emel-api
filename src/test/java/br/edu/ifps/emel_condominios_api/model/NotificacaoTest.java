package br.edu.ifps.emel_condominios_api.model;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da entidade Notificacao")
class NotificacaoTest {

    @Test
    @DisplayName("Deve criar notificação com construtor apenas de ID")
    void deveCriarComConstrutorId() {
        Notificacao notificacao = new Notificacao(5L);
        assertEquals(5L, notificacao.getId());
    }

    @Test
    @DisplayName("Deve criar notificação com construtor completo preservando atributos e relacionamento com Morador")
    void deveCriarComConstrutorCompleto() {
        Morador morador = new Morador(1L);
        LocalDate data = LocalDate.of(2026, 11, 27);

        Notificacao notificacao = new Notificacao(
                "Reserva Solicitada",
                "Sua reserva foi registrada com status PENDENTE.",
                "RESERVA",
                data,
                morador
        );

        assertEquals("Reserva Solicitada", notificacao.getTitulo());
        assertEquals("Sua reserva foi registrada com status PENDENTE.", notificacao.getDescricao());
        assertEquals("RESERVA", notificacao.getTipo());
        assertEquals(data, notificacao.getData());
        assertSame(morador, notificacao.getMorador());
    }
}
