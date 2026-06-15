package br.edu.ifps.emel_condominios_api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifps.emel_condominios_api.dto.LoginResponse;
import br.edu.ifps.emel_condominios_api.model.Sindico;
import br.edu.ifps.emel_condominios_api.repository.SindicoRepository;
import br.edu.ifps.emel_condominios_api.service.JwtService;
@RestController
@RequestMapping("/auth")
public class AuthController {
    private SindicoRepository sindicoRepository;
    private JwtService jwtService;

    public AuthController(SindicoRepository sindicoRepository, JwtService jwtService) {
        this.sindicoRepository = sindicoRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody Sindico sindicoLogin) {
            Sindico sindico = sindicoRepository.findByEmail(sindicoLogin.getEmail()).orElseThrow(() -> new RuntimeException("Erro: E-mail não encontrado"));        
            if(!sindicoLogin.getSenha().equals(sindico.getSenha())){
                throw new RuntimeException("Erro: Senha incorreta");
            }
            // Emitindo o token jwt
            String token = jwtService.gerarTokens(sindico.getEmail());
            //  retorna o token jwt como resposta do login 
        return new LoginResponse(token);
    }
    
}
