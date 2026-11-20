package br.edu.ifps.emel_condominios_api.service;

import org.springframework.stereotype.Service;

import br.edu.ifps.emel_condominios_api.model.Morador;
import br.edu.ifps.emel_condominios_api.repository.MoradorRepository;

@Service
public class MoradorService {

    private final MoradorRepository moradorRepository;

    public MoradorService(MoradorRepository moradorRepository) {
        this.moradorRepository = moradorRepository;
    }

    public Morador criarMorador(Morador novoMorador){
        return moradorRepository.save(novoMorador);
    }

    public Morador mostrarMoradorPorId(Long id){
        return moradorRepository.findById(id).orElseThrow(() -> new RuntimeException("Morador não encontrado"));
    }

    public Morador editarMorador(Long id, Morador novoMorador){
        return moradorRepository.findById(id).map(morador -> {
            morador.setNome(novoMorador.getNome());
            morador.setEmail(novoMorador.getEmail());
            morador.setTelefone(novoMorador.getTelefone());
            morador.setCpf(novoMorador.getCpf());
            return moradorRepository.save(morador);
        }).orElseThrow(() -> new RuntimeException("Morador não encontrado"));
    }

    public void deletarMorador(Long id){
        moradorRepository.deleteById(id);
    }
}
