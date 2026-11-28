package br.edu.ifps.emel_condominios_api.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da entidade Morador")
class MoradorTest {

    @Test
    @DisplayName("Deve criar morador com construtor apenas de ID")
    void deveCriarComConstrutorId() {
        Morador morador = new Morador(1L);
        assertEquals(1L, morador.getId());
    }

    @Test
    @DisplayName("Deve criar morador com construtor completo incluindo senha")
    void deveCriarComConstrutorCompletoComSenha() {
        Morador morador = new Morador("João Silva", "joao@email.com", "11999999999", "12345678900", "senhaHash");

        assertEquals("João Silva", morador.getNome());
        assertEquals("joao@email.com", morador.getEmail());
        assertEquals("11999999999", morador.getTelefone());
        assertEquals("12345678900", morador.getCpf());
        assertEquals("senhaHash", morador.getSenha());
    }

    @Test
    @DisplayName("Deve criar morador com construtor sem senha preservando atributos")
    void deveCriarComConstrutorSemSenha() {
        Morador morador = new Morador("Maria Souza", "maria@email.com", "11888888888", "00987654321");

        assertEquals("Maria Souza", morador.getNome());
        assertEquals("maria@email.com", morador.getEmail());
        assertEquals("11888888888", morador.getTelefone());
        assertEquals("00987654321", morador.getCpf());
        assertNull(morador.getSenha());
    }
}
