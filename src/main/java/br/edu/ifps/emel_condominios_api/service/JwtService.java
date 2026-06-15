package br.edu.ifps.emel_condominios_api.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final String SECRETKEY = "pneumocitosegraficaazulestrela123456";
    
    public String gerarTokens(String email){
        return Jwts.builder().subject(email).issuedAt(new Date()).expiration(new Date(
            System.currentTimeMillis() + 86400000
        )
    ).signWith(
        Keys.hmacShaKeyFor(
            SECRETKEY.getBytes()
        )
    ).compact();
    }

    public String extrairEmail(String token){
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(SECRETKEY.getBytes())).build().parseSignedClaims(token).getPayload().getSubject();
    }
}
