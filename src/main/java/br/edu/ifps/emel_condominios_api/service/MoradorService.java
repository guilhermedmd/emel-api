package br.edu.ifps.emel_condominios_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.ifps.emel_condominios_api.model.Morador;
import br.edu.ifps.emel_condominios_api.repository.MoradorRepository;

@Service
public class MoradorService {

    private final MoradorRepository moradorRepository;
    private final PasswordEncoder passwordEncoder;

    public MoradorService(MoradorRepository moradorRepository, PasswordEncoder passwordEncoder) {
        this.moradorRepository = moradorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String email, String senha) {
        Morador morador = moradorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Erro: Email de morador não encontrado"));
        if (morador.getSenha() == null || !passwordEncoder.matches(senha, morador.getSenha())) {
            throw new RuntimeException("Erro: Senha incorreta");
        }
        return morador.getEmail();
    }

    public String register(Morador novoMorador) {
        if (novoMorador.getSenha() != null && !novoMorador.getSenha().isBlank()) {
            novoMorador.setSenha(passwordEncoder.encode(novoMorador.getSenha()));
        }
        Morador moradorSalvo = moradorRepository.save(novoMorador);
        return moradorSalvo.getEmail();
    }

    public Morador criarMorador(Morador novoMorador) {
        if (novoMorador.getSenha() != null && !novoMorador.getSenha().isBlank()) {
            novoMorador.setSenha(passwordEncoder.encode(novoMorador.getSenha()));
        }
        return moradorRepository.save(novoMorador);
    }

    public Morador mostrarMoradorPorId(Long id) {
        return moradorRepository.findById(id).orElseThrow(() -> new RuntimeException("Morador não encontrado"));
    }

    public Morador editarMorador(Long id, Morador novoMorador) {
        return moradorRepository.findById(id).map(morador -> {
            morador.setNome(novoMorador.getNome());
            morador.setEmail(novoMorador.getEmail());
            morador.setTelefone(novoMorador.getTelefone());
            morador.setCpf(novoMorador.getCpf());
            if (novoMorador.getSenha() != null && !novoMorador.getSenha().isBlank()) {
                morador.setSenha(passwordEncoder.encode(novoMorador.getSenha()));
            }
            return moradorRepository.save(morador);
        }).orElseThrow(() -> new RuntimeException("Morador não encontrado"));
    }

    public void deletarMorador(Long id) {
        moradorRepository.deleteById(id);
    }
}
