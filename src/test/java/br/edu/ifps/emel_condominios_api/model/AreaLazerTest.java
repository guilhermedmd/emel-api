package br.edu.ifps.emel_condominios_api.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da entidade AreaLazer")
class AreaLazerTest {

    @Test
    @DisplayName("Deve criar área de lazer com construtor apenas de ID")
    void deveCriarComConstrutorId() {
        AreaLazer area = new AreaLazer(3L);
        assertEquals(3L, area.getId());
    }

    @Test
    @DisplayName("Deve criar área de lazer com construtor completo preservando atributos")
    void deveCriarComConstrutorCompleto() {
        AreaLazer area = new AreaLazer("Piscina", "Piscina adulto e infantil", 25, "ATIVO");

        assertEquals("Piscina", area.getNome());
        assertEquals("Piscina adulto e infantil", area.getDescricao());
        assertEquals(25, area.getCapacidadeMaxima());
        assertEquals("ATIVO", area.getStatus());
    }
}
