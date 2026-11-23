package br.edu.ifps.emel_condominios_api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifps.emel_condominios_api.dto.LoginDto;
import br.edu.ifps.emel_condominios_api.dto.LoginResponse;
import br.edu.ifps.emel_condominios_api.model.Morador;
import br.edu.ifps.emel_condominios_api.model.Sindico;
import br.edu.ifps.emel_condominios_api.service.JwtService;
import br.edu.ifps.emel_condominios_api.service.MoradorService;
import br.edu.ifps.emel_condominios_api.service.SindicoService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final SindicoService sindicoService;
    private final MoradorService moradorService;
    private final JwtService jwtService;

    public AuthController(SindicoService sindicoService, MoradorService moradorService, JwtService jwtService) {
        this.sindicoService = sindicoService;
        this.moradorService = moradorService;
        this.jwtService = jwtService;
    }

    // Endpoint unificado de login (aceita síndico ou morador com email e senha)
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginDto loginDto) {
        // Tenta autenticar primeiro como síndico
        try {
            Sindico sindico = new Sindico(loginDto.email(), loginDto.senha());
            String emailSindico = sindicoService.login(sindico);
            String token = jwtService.gerarTokens(emailSindico, "SINDICO");
            return new LoginResponse(token);
        } catch (Exception eSindico) {
            // Se falhar no síndico, tenta autenticar como morador
            try {
                String emailMorador = moradorService.login(loginDto.email(), loginDto.senha());
                String token = jwtService.gerarTokens(emailMorador, "MORADOR");
                return new LoginResponse(token);
            } catch (Exception eMorador) {
                throw new RuntimeException("Erro: Credenciais inválidas (Email ou senha incorretos)");
            }
        }
    }

    // Registro do Síndico
    @PostMapping("/register")
    public LoginResponse registerSindico(@RequestBody Sindico novoSindico) {
        String emailSindico = sindicoService.register(novoSindico);
        String token = jwtService.gerarTokens(emailSindico, "SINDICO");
        return new LoginResponse(token);
    }

    // Login específico do Morador
    @PostMapping("/morador/login")
    public LoginResponse loginMorador(@RequestBody LoginDto loginDto) {
        String emailMorador = moradorService.login(loginDto.email(), loginDto.senha());
        String token = jwtService.gerarTokens(emailMorador, "MORADOR");
        return new LoginResponse(token);
    }

    // Registro específico do Morador
    @PostMapping("/morador/register")
    public LoginResponse registerMorador(@RequestBody Morador novoMorador) {
        String emailMorador = moradorService.register(novoMorador);
        String token = jwtService.gerarTokens(emailMorador, "MORADOR");
        return new LoginResponse(token);
    }
}
