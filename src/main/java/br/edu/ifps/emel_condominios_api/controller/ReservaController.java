package br.edu.ifps.emel_condominios_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifps.emel_condominios_api.model.Reserva;
import br.edu.ifps.emel_condominios_api.service.ReservaService;

@RestController
@RequestMapping("/reserva")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    private String extrairEmail(Authentication auth) {
        return auth != null ? auth.getName() : null;
    }

    private String extrairRole(Authentication auth) {
        if (auth == null) return null;
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse(null);
    }

    @PostMapping
    public ResponseEntity<Reserva> criarReserva(@RequestBody Reserva reserva, Authentication auth) {
        String email = extrairEmail(auth);
        String role = extrairRole(auth);
        Reserva novaReserva = reservaService.criarReserva(reserva, email, role);
        return ResponseEntity.ok(novaReserva);
    }

    @GetMapping
    public ResponseEntity<List<Reserva>> listarTodasReservas() {
        List<Reserva> reservas = reservaService.listarTodasReservas();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> mostrarReservaPorId(@PathVariable Long id, Authentication auth) {
        String email = extrairEmail(auth);
        String role = extrairRole(auth);
        Reserva reserva = reservaService.mostrarReservaPorId(id, email, role);
        return ResponseEntity.ok(reserva);
    }

    @GetMapping("/morador/{moradorId}")
    public ResponseEntity<List<Reserva>> listarReservasPorMorador(@PathVariable Long moradorId, Authentication auth) {
        String email = extrairEmail(auth);
        String role = extrairRole(auth);
        List<Reserva> reservas = reservaService.listarReservasPorMorador(moradorId, email, role);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/area-lazer/{areaLazerId}")
    public ResponseEntity<List<Reserva>> listarReservasPorAreaLazer(@PathVariable Long areaLazerId) {
        List<Reserva> reservas = reservaService.listarReservasPorAreaLazer(areaLazerId);
        return ResponseEntity.ok(reservas);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Reserva> cancelarReserva(@PathVariable Long id, Authentication auth) {
        String email = extrairEmail(auth);
        String role = extrairRole(auth);
        Reserva cancelada = reservaService.cancelarReserva(id, email, role);
        return ResponseEntity.ok(cancelada);
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Reserva> confirmarReserva(@PathVariable Long id) {
        Reserva confirmada = reservaService.confirmarReserva(id);
        return ResponseEntity.ok(confirmada);
    }

    @PutMapping("/{id}/recusar")
    public ResponseEntity<Reserva> recusarReserva(@PathVariable Long id) {
        Reserva recusada = reservaService.recusarReserva(id);
        return ResponseEntity.ok(recusada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarReserva(@PathVariable Long id) {
        reservaService.deletarReserva(id);
        return ResponseEntity.ok().build();
    }
}
