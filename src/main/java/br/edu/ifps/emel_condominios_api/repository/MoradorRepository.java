package br.edu.ifps.emel_condominios_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifps.emel_condominios_api.model.Morador;

@Repository
public interface MoradorRepository extends JpaRepository<Morador, Long> {
    Optional<Morador> findByEmail(String email);
}
