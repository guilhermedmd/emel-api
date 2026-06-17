package br.edu.ifps.emel_condominios_api.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
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
    private SindicoService SindicoService;
    private JwtService jwtService;

    

    public AuthController(br.edu.ifps.emel_condominios_api.service.SindicoService sindicoService,
            JwtService jwtService) {
        SindicoService = sindicoService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody Sindico sindicoLogin) {
        String EmailSindico = SindicoService.login(sindicoLogin);
        // Emitindo o token jwt
        String token = jwtService.gerarTokens(EmailSindico);
        //  retorna o token jwt como resposta do login 
        return new LoginResponse(token);
    }

    @PostMapping("/register")
    public LoginResponse registerSindico(@RequestBody Sindico novoSindico) {
        String emailSindico = SindicoService.register(novoSindico);
        String token = jwtService.gerarTokens(emailSindico);
        return new LoginResponse(token);
    }
    
    
    
}
