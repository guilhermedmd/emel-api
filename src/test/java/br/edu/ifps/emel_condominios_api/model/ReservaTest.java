package br.edu.ifps.emel_condominios_api.model;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da entidade Reserva")
class ReservaTest {

    @Nested
    @DisplayName("Construtores manuais")
    class Construtores {

        @Test
        @DisplayName("Deve criar reserva com construtor apenas de ID")
        void deveCriarComConstrutorId() {
            // Arrange & Act
            Reserva reserva = new Reserva(10L);

            // Assert
            assertEquals(10L, reserva.getId());
        }

        @Test
        @DisplayName("Deve criar reserva com construtor completo sem ID")
        void deveCriarComConstrutorCompleto() {
            // Arrange
            Morador morador = new Morador(1L);
            AreaLazer area = new AreaLazer(2L);
            LocalDateTime inicio = LocalDateTime.of(2026, 11, 28, 10, 0);
            LocalDateTime fim = LocalDateTime.of(2026, 11, 28, 14, 0);
            LocalDateTime solicitacao = LocalDateTime.of(2026, 11, 27, 10, 0);

            // Act
            Reserva reserva = new Reserva(
                    morador, area, inicio, fim,
                    "Aniversário", 20, "PENDENTE", "Sem som alto", solicitacao
            );

            // Assert
            assertEquals(morador, reserva.getMorador());
            assertEquals(area, reserva.getAreaLazer());
            assertEquals(inicio, reserva.getDataHoraInicio());
            assertEquals(fim, reserva.getDataHoraFim());
            assertEquals("Aniversário", reserva.getTipoEvento());
            assertEquals(20, reserva.getQtdConvidados());
            assertEquals("PENDENTE", reserva.getStatus());
            assertEquals("Sem som alto", reserva.getObservacoes());
            assertEquals(solicitacao, reserva.getDataSolicitacao());
        }

        @Test
        @DisplayName("Deve criar reserva com construtor reduzido sem status e dataSolicitacao")
        void deveCriarComConstrutorReduzido() {
            // Arrange
            Morador morador = new Morador(1L);
            AreaLazer area = new AreaLazer(2L);
            LocalDateTime inicio = LocalDateTime.of(2026, 11, 28, 10, 0);
            LocalDateTime fim = LocalDateTime.of(2026, 11, 28, 14, 0);

            // Act
            Reserva reserva = new Reserva(
                    morador, area, inicio, fim,
                    "Reunião", 15, "Trazer projetor"
            );

            // Assert
            assertEquals(morador, reserva.getMorador());
            assertEquals(area, reserva.getAreaLazer());
            assertEquals(inicio, reserva.getDataHoraInicio());
            assertEquals(fim, reserva.getDataHoraFim());
            assertEquals("Reunião", reserva.getTipoEvento());
            assertEquals(15, reserva.getQtdConvidados());
            assertEquals("Trazer projetor", reserva.getObservacoes());
            assertNull(reserva.getStatus());
            assertNull(reserva.getDataSolicitacao());
        }
    }

    @Nested
    @DisplayName("Métodos getMoradorId e setMoradorId")
    class MoradorIdHelpers {

        @Test
        @DisplayName("getMoradorId deve retornar null quando morador é nulo")
        void getMoradorIdQuandoMoradorNulo() {
            Reserva reserva = new Reserva();
            assertNull(reserva.getMoradorId());
        }

        @Test
        @DisplayName("getMoradorId deve retornar ID quando morador está preenchido")
        void getMoradorIdQuandoMoradorPreenchido() {
            Reserva reserva = new Reserva();
            reserva.setMorador(new Morador(5L));

            assertEquals(5L, reserva.getMoradorId());
        }

        @Test
        @DisplayName("setMoradorId deve criar objeto Morador quando associação é nula")
        void setMoradorIdQuandoAssociacaoNula() {
            Reserva reserva = new Reserva();

            reserva.setMoradorId(7L);

            assertNotNull(reserva.getMorador());
            assertEquals(7L, reserva.getMorador().getId());
        }

        @Test
        @DisplayName("setMoradorId deve atualizar ID quando associação já existe")
        void setMoradorIdQuandoAssociacaoExistente() {
            Reserva reserva = new Reserva();
            Morador morador = new Morador("João", "joao@email.com", "11999999999", "11111111111");
            morador.setId(1L);
            reserva.setMorador(morador);

            reserva.setMoradorId(9L);

            assertSame(morador, reserva.getMorador());
            assertEquals(9L, reserva.getMorador().getId());
        }

        @Test
        @DisplayName("setMoradorId não deve alterar associação quando valor informado é nulo")
        void setMoradorIdQuandoValorNulo() {
            Reserva reserva = new Reserva();
            reserva.setMoradorId(null);
            assertNull(reserva.getMorador());

            Morador morador = new Morador(3L);
            reserva.setMorador(morador);
            reserva.setMoradorId(null);
            assertEquals(3L, reserva.getMoradorId());
        }
    }

    @Nested
    @DisplayName("Métodos getAreaLazerId e setAreaLazerId")
    class AreaLazerIdHelpers {

        @Test
        @DisplayName("getAreaLazerId deve retornar null quando areaLazer é nula")
        void getAreaLazerIdQuandoAreaNula() {
            Reserva reserva = new Reserva();
            assertNull(reserva.getAreaLazerId());
        }

        @Test
        @DisplayName("getAreaLazerId deve retornar ID quando areaLazer está preenchida")
        void getAreaLazerIdQuandoAreaPreenchida() {
            Reserva reserva = new Reserva();
            reserva.setAreaLazer(new AreaLazer(4L));

            assertEquals(4L, reserva.getAreaLazerId());
        }

        @Test
        @DisplayName("setAreaLazerId deve criar objeto AreaLazer quando associação é nula")
        void setAreaLazerIdQuandoAssociacaoNula() {
            Reserva reserva = new Reserva();

            reserva.setAreaLazerId(8L);

            assertNotNull(reserva.getAreaLazer());
            assertEquals(8L, reserva.getAreaLazer().getId());
        }

        @Test
        @DisplayName("setAreaLazerId deve atualizar ID quando associação já existe")
        void setAreaLazerIdQuandoAssociacaoExistente() {
            Reserva reserva = new Reserva();
            AreaLazer area = new AreaLazer("Salão", "Salão de festas", 40, "ATIVO");
            area.setId(2L);
            reserva.setAreaLazer(area);

            reserva.setAreaLazerId(12L);

            assertSame(area, reserva.getAreaLazer());
            assertEquals(12L, reserva.getAreaLazer().getId());
        }

        @Test
        @DisplayName("setAreaLazerId não deve alterar associação quando valor informado é nulo")
        void setAreaLazerIdQuandoValorNulo() {
            Reserva reserva = new Reserva();
            reserva.setAreaLazerId(null);
            assertNull(reserva.getAreaLazer());

            AreaLazer area = new AreaLazer(6L);
            reserva.setAreaLazer(area);
            reserva.setAreaLazerId(null);
            assertEquals(6L, reserva.getAreaLazerId());
        }
    }
}
