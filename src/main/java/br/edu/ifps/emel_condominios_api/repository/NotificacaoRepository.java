package br.edu.ifps.emel_condominios_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifps.emel_condominios_api.model.Notificacao;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long>{
    
}
