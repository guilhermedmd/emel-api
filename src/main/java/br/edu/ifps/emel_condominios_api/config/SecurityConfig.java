package br.edu.ifps.emel_condominios_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll()
                        .requestMatchers("/auth/**", "/error").permitAll()
                        // Rotas de Reserva permitidas para Morador e Síndico:
                        .requestMatchers(HttpMethod.POST, "/reserva").hasAnyRole("MORADOR", "SINDICO")
                        .requestMatchers(HttpMethod.GET, "/reserva/morador/{moradorId}").hasAnyRole("MORADOR", "SINDICO")
                        .requestMatchers(HttpMethod.GET, "/reserva/area-lazer/{areaLazerId}").hasAnyRole("MORADOR", "SINDICO")
                        .requestMatchers(HttpMethod.PUT, "/reserva/{id}/cancelar").hasAnyRole("MORADOR", "SINDICO")
                        .requestMatchers(HttpMethod.GET, "/reserva/{id}").hasAnyRole("MORADOR", "SINDICO")
                        // Demais rotas de Reserva exclusivas para Síndico:
                        .requestMatchers("/reserva/**").hasRole("SINDICO")
                        // Rotas de Notificação permitidas para Morador e Síndico:
                        .requestMatchers(HttpMethod.POST, "/notificacao").hasAnyRole("MORADOR", "SINDICO")
                        .requestMatchers(HttpMethod.GET, "/notificacao/{id}").hasAnyRole("MORADOR", "SINDICO")
                        .requestMatchers(HttpMethod.PUT, "/notificacao/{id}").hasAnyRole("MORADOR", "SINDICO")
                        // Demais rotas de Notificação exclusivas para Síndico:
                        .requestMatchers("/notificacao/**").hasRole("SINDICO")
                        // Rotas de Área de Lazer permitidas para Morador e Síndico:
                        .requestMatchers(HttpMethod.GET, "/area-lazer").hasAnyRole("MORADOR", "SINDICO")
                        .requestMatchers(HttpMethod.GET, "/area-lazer/{id}").hasAnyRole("MORADOR", "SINDICO")
                        // Demais rotas de Área de Lazer exclusivas para Síndico:
                        .requestMatchers("/area-lazer/**").hasRole("SINDICO")
                        // Todas as demais rotas são exclusivas do Síndico:
                        .requestMatchers("/morador/**").hasRole("SINDICO")
                        .requestMatchers("/visitante/**").hasRole("SINDICO")
                        .requestMatchers("/visita/**").hasRole("SINDICO")
                        .anyRequest().hasRole("SINDICO")
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
