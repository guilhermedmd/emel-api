package br.edu.ifps.emel_condominios_api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.edu.ifps.emel_condominios_api.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.morador.id = :moradorId AND r.status IN ('PENDENTE', 'CONFIRMADA')")
    long countReservasAtivasPorMorador(@Param("moradorId") Long moradorId);

    @Query("SELECT r FROM Reserva r WHERE r.areaLazer.id = :areaLazerId " +
           "AND r.status IN ('PENDENTE', 'CONFIRMADA') " +
           "AND (:reservaId IS NULL OR r.id != :reservaId) " +
           "AND (r.dataHoraInicio < :fim AND r.dataHoraFim > :inicio)")
    List<Reserva> findConflitos(
        @Param("areaLazerId") Long areaLazerId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim,
        @Param("reservaId") Long reservaId
    );

    List<Reserva> findByMoradorId(Long moradorId);

    List<Reserva> findByAreaLazerId(Long areaLazerId);
}
