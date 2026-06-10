package br.edu.ifps.emel_condominios_api.service;

import org.springframework.stereotype.Service;

import br.edu.ifps.emel_condominios_api.model.Morador;
import br.edu.ifps.emel_condominios_api.repository.MoradorRepository;

@Service
public class MoradorService {
    MoradorRepository moradorRepository;

    public MoradorService(MoradorRepository moradorRepository) {
        this.moradorRepository = moradorRepository;
    }

    public Morador criarMorador(Morador novoMorador){
        return moradorRepository.save(novoMorador);
    }

    public Morador mostrarMoradorPorId(Long id){
        return moradorRepository.findById(id).orElseThrow( () -> new RuntimeException("Morador não encontrado"));
    }

    public Morador editarMorador(long id, Morador novMorador){
        return moradorRepository.findById(id).map(morador -> {
            morador.setNome(novMorador.getNome());
            morador.setEmail(novMorador.getEmail());
            morador.setTelefone(novMorador.getTelefone());
            morador.setCpf(novMorador.getCpf());
            return moradorRepository.save(morador);
        }).orElseThrow( () -> new RuntimeException("Morador não encontrado"));
    }

    public void deletarMorador(long id){
        moradorRepository.deleteById(id);
    }

}
