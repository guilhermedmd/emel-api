package br.edu.ifps.emel_condominios_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }
    @Bean
    // Basicamente é uma cadeia de filtros
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // http é o objeto usado para configurar as regras desse filtro
        // """
        // Desabilita o csrf
        // SessionCreationPolicy.STATELESS -> não cria sessões
        // Toda a url que se inícia com auth é aceita sem autenticação
        // AnyResquest: todo o resto dos endpoints precisa de autenticação
        // AddFilterBefore -> faz com que o spring execute o jwtFilter antes do filtro padrão de segurança
        // Por fim o build() transforma todas essas configurações em um SecurityFilterChain
        // """;
        return http.csrf(csrf -> csrf.disable()).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**").permitAll().anyRequest().authenticated()).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build();
    } 
}
