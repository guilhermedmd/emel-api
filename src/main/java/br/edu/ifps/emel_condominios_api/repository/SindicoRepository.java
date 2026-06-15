package br.edu.ifps.emel_condominios_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifps.emel_condominios_api.model.Sindico;

@Repository
public interface SindicoRepository extends JpaRepository<Sindico, Long>{
    
    Optional<Sindico> findByEmail(String email);
}
