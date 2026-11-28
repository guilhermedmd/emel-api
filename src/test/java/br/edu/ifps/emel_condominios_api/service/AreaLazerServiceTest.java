package br.edu.ifps.emel_condominios_api.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ifps.emel_condominios_api.exception.ResourceNotFoundException;
import br.edu.ifps.emel_condominios_api.model.AreaLazer;
import br.edu.ifps.emel_condominios_api.repository.AreaLazerRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DRIVER DE TESTE (Test Driver):
 * Esta suíte JUnit 5 atua como test driver acionando diretamente os métodos da
 * classe sob teste (AreaLazerService), aplicando testes estruturais (caixa-branca).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários de AreaLazerService")
class AreaLazerServiceTest {

    /**
     * MOCK:
     * Mock do repositório AreaLazerRepository criado via Mockito para isolar a camada de
     * persistência (banco de dados MySQL/JPA) durante a execução dos testes unitários.
     */
    @Mock
    private AreaLazerRepository areaLazerRepository;

    @InjectMocks
    private AreaLazerService areaLazerService;

    @Test
    @DisplayName("listarAreasLazer deve retornar lista preenchida quando houver áreas cadastradas")
    void deveListarAreasLazerComSucesso() {
        // Arrange — STUB: pré-configura o retorno do mock para retornar duas áreas de lazer
        AreaLazer a1 = new AreaLazer("Churrasqueira", "Área gourmet", 30, "ATIVO");
        a1.setId(1L);
        AreaLazer a2 = new AreaLazer("Salão de Festas", "Salão climatizado", 60, "ATIVO");
        a2.setId(2L);
        when(areaLazerRepository.findAll()).thenReturn(List.of(a1, a2));

        // Act
        List<AreaLazer> resultado = areaLazerService.listarAreasLazer();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("Churrasqueira", resultado.get(0).getNome());
        assertEquals("Salão de Festas", resultado.get(1).getNome());
    }

    @Test
    @DisplayName("listarAreasLazer deve retornar lista vazia quando não houver áreas cadastradas")
    void deveListarAreasLazerVazia() {
        // Arrange — STUB: pré-configura o retorno do mock para lista vazia
        when(areaLazerRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<AreaLazer> resultado = areaLazerService.listarAreasLazer();

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("mostrarAreaLazerPorId deve retornar a área quando ID existir")
    void deveMostrarAreaLazerPorIdComSucesso() {
        // Arrange — STUB: simula área encontrada no repositório para ID 1
        AreaLazer area = new AreaLazer("Piscina", "Piscina adulto", 25, "ATIVO");
        area.setId(1L);
        when(areaLazerRepository.findById(1L)).thenReturn(Optional.of(area));

        // Act
        AreaLazer resultado = areaLazerService.mostrarAreaLazerPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Piscina", resultado.getNome());
    }

    @Test
    @DisplayName("mostrarAreaLazerPorId deve lançar ResourceNotFoundException quando ID não existir")
    void deveLancarExcecaoQuandoAreaLazerNaoEncontrada() {
        // Arrange — STUB: simula retorno vazio (Optional.empty) conduzindo ao fluxo de exceção
        when(areaLazerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> areaLazerService.mostrarAreaLazerPorId(999L));
    }

    @Test
    @DisplayName("cadastrarAreaLazer deve salvar e retornar a área de lazer")
    void deveCadastrarAreaLazer() {
        // Arrange — STUB: pré-configura o retorno do save()
        AreaLazer nova = new AreaLazer("Quadra", "Quadra poliesportiva", 20, "ATIVO");
        AreaLazer salva = new AreaLazer("Quadra", "Quadra poliesportiva", 20, "ATIVO");
        salva.setId(10L);
        when(areaLazerRepository.save(nova)).thenReturn(salva);

        // Act
        AreaLazer resultado = areaLazerService.cadastrarAreaLazer(nova);

        // Assert
        assertEquals(10L, resultado.getId());
        assertEquals("Quadra", resultado.getNome());
    }

    @Test
    @DisplayName("editarAreaLazer deve atualizar dados quando ID existir")
    void deveEditarAreaLazerComSucesso() {
        // Arrange — STUB: simula busca existente e salvamento da área atualizada
        AreaLazer existente = new AreaLazer("Quadra", "Antiga", 20, "ATIVO");
        existente.setId(1L);
        AreaLazer atualizacao = new AreaLazer("Quadra Nova", "Reformada", 25, "MANUTENCAO");
        when(areaLazerRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(areaLazerRepository.save(any(AreaLazer.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        AreaLazer resultado = areaLazerService.editarAreaLazer(1L, atualizacao);

        // Assert
        assertEquals("Quadra Nova", resultado.getNome());
        assertEquals("Reformada", resultado.getDescricao());
        assertEquals(25, resultado.getCapacidadeMaxima());
        assertEquals("MANUTENCAO", resultado.getStatus());
    }

    @Test
    @DisplayName("editarAreaLazer deve lançar ResourceNotFoundException quando ID não existir")
    void deveLancarExcecaoAoEditarAreaInexistente() {
        // Arrange — STUB: simula ID inexistente no repositório
        AreaLazer atualizacao = new AreaLazer("Quadra", "Desc", 20, "ATIVO");
        when(areaLazerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> areaLazerService.editarAreaLazer(999L, atualizacao));
    }

    @Test
    @DisplayName("deletarAreaLazer deve acionar deleteById no repositório")
    void deveDeletarAreaLazer() {
        // Arrange — STUB: método void configurado explicitamente
        doNothing().when(areaLazerRepository).deleteById(1L);

        // Act
        areaLazerService.deletarAreaLazer(1L);

        // Assert
        verify(areaLazerRepository, times(1)).deleteById(1L);
    }
}
