package br.edu.ifps.emel_condominios_api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifps.emel_condominios_api.dto.LoginResponse;
import br.edu.ifps.emel_condominios_api.model.Sindico;
import br.edu.ifps.emel_condominios_api.service.JwtService;
import br.edu.ifps.emel_condominios_api.service.SindicoService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final SindicoService sindicoService;
    private final JwtService jwtService;

    public AuthController(SindicoService sindicoService, JwtService jwtService) {
        this.sindicoService = sindicoService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody Sindico sindicoLogin) {
        String emailSindico = sindicoService.login(sindicoLogin);
        String token = jwtService.gerarTokens(emailSindico);
        return new LoginResponse(token);
    }

    @PostMapping("/register")
    public LoginResponse registerSindico(@RequestBody Sindico novoSindico) {
        String emailSindico = sindicoService.register(novoSindico);
        String token = jwtService.gerarTokens(emailSindico);
        return new LoginResponse(token);
    }
}
