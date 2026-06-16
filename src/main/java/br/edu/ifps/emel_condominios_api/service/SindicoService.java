package br.edu.ifps.emel_condominios_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.ifps.emel_condominios_api.model.Sindico;
import br.edu.ifps.emel_condominios_api.repository.SindicoRepository;

@Service
public class SindicoService {
    
    private SindicoRepository sindicoRepository;
    private PasswordEncoder passwordEncoder;

    public SindicoService(SindicoRepository sindicoRepository, PasswordEncoder passwordEncoder) {
        this.sindicoRepository = sindicoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(Sindico sindico){
        Sindico sindicoRepo = sindicoRepository.findByEmail(sindico.getEmail()).orElseThrow(() -> new RuntimeException("Erro: Email não encontrado"));
        if(!passwordEncoder.matches(sindico.getSenha(), sindicoRepo.getSenha())){
            throw new RuntimeException("Erro: Senha incorreta");
        }
        return sindico.getEmail();
    }

    public String register(Sindico novoSindico){
        novoSindico.setSenha(passwordEncoder.encode(novoSindico.getSenha()));
        Sindico sindico = sindicoRepository.save(novoSindico);
        return sindico.getEmail();
    }
    
}
