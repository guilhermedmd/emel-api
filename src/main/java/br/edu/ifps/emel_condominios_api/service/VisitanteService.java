package br.edu.ifps.emel_condominios_api.service;

import org.springframework.stereotype.Service;

import br.edu.ifps.emel_condominios_api.model.Visitante;
import br.edu.ifps.emel_condominios_api.repository.VisitanteRepository;

@Service
public class VisitanteService {

    VisitanteRepository visitanteRepository;

    public VisitanteService(VisitanteRepository visitanteRepository) {
        this.visitanteRepository = visitanteRepository;
    }

    public Visitante cadastrarVisitante(Visitante novoVisitante){
        return visitanteRepository.save(novoVisitante);
    }
    public Visitante MostrarVisitantePorId(Long id){
        return visitanteRepository.findById(id).orElseThrow(() -> new RuntimeException("Erro: Visitante não encontrado"));
    }
    public Visitante editarVisitante(long id, Visitante novoVisitante){
        return visitanteRepository.findById(id).map(visitante -> {
            visitante.setNome(novoVisitante.getNome());
            visitante.setEmail(novoVisitante.getEmail());
            visitante.setTelefone(novoVisitante.getTelefone());
            visitante.setCpf(novoVisitante.getCpf());
            return visitanteRepository.save(visitante);
        }).orElseThrow(() -> new RuntimeException("Erro: Visitante não encontrado"));
    }
    public void deletarVisitante(long id){
        visitanteRepository.deleteById(id);
    }
    
} 

