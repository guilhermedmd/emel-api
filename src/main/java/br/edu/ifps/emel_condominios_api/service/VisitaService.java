package br.edu.ifps.emel_condominios_api.service;

import org.springframework.stereotype.Service;

import br.edu.ifps.emel_condominios_api.model.Visita;
import br.edu.ifps.emel_condominios_api.repository.VisitaRepository;

@Service
public class VisitaService {
    
    VisitaRepository visitaRepository;

    public VisitaService(VisitaRepository visitaRepository) {
        this.visitaRepository = visitaRepository;
    }

    public Visita cadastrarVisita(Visita novaVisita){
        return visitaRepository.save(novaVisita);
    }

    public Visita mostrarVisitaPorId(long id){
        return visitaRepository.findById(id).orElseThrow(() -> new RuntimeException("Erro: Visita não encontrada"));
    }

    public Visita editarVisita(long id, Visita novaVisita){
        return visitaRepository.findById(id).map(visita -> {
            visita.setTipo(visita.getTipo());
            visita.setData(visita.getData());
            visita.setVisitantes(visita.getVisitantes());
            return visitaRepository.save(visita);
        }).orElseThrow(() -> new RuntimeException("Erro: Visita não encontrada"));
    }

    public void deletarVisita(long id){
        visitaRepository.deleteById(id);
    }
}
