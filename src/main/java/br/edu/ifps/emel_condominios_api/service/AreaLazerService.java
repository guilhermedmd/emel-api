package br.edu.ifps.emel_condominios_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifps.emel_condominios_api.model.AreaLazer;
import br.edu.ifps.emel_condominios_api.repository.AreaLazerRepository;

@Service
public class AreaLazerService {

    private final AreaLazerRepository areaLazerRepository;

    public AreaLazerService(AreaLazerRepository areaLazerRepository) {
        this.areaLazerRepository = areaLazerRepository;
    }

    public AreaLazer cadastrarAreaLazer(AreaLazer areaLazer) {
        return areaLazerRepository.save(areaLazer);
    }

    public List<AreaLazer> listarAreasLazer() {
        return areaLazerRepository.findAll();
    }

    public AreaLazer mostrarAreaLazerPorId(Long id) {
        return areaLazerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro: Área de lazer não encontrada"));
    }

    public AreaLazer editarAreaLazer(Long id, AreaLazer novaAreaLazer) {
        return areaLazerRepository.findById(id).map(area -> {
            area.setNome(novaAreaLazer.getNome());
            area.setDescricao(novaAreaLazer.getDescricao());
            area.setCapacidadeMaxima(novaAreaLazer.getCapacidadeMaxima());
            area.setStatus(novaAreaLazer.getStatus());
            return areaLazerRepository.save(area);
        }).orElseThrow(() -> new RuntimeException("Erro: Área de lazer não encontrada"));
    }

    public void deletarAreaLazer(Long id) {
        areaLazerRepository.deleteById(id);
    }
}
