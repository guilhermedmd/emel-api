package br.edu.ifps.emel_condominios_api.config;

import java.io.IOException;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.edu.ifps.emel_condominios_api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter{
    JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

    String auth = request.getHeader("Authorization");
    if(auth != null && auth.startsWith("Bearer ")){

        String token = auth.substring(7);
        try {
            String email = jwtService.extrairEmail(token);

            System.out.println("Email: " + email);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of()
                    );
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    filterChain.doFilter(request, response);
}
}
