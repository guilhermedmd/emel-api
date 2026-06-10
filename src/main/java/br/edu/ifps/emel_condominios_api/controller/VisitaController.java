package br.edu.ifps.emel_condominios_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifps.emel_condominios_api.model.Visita;
import br.edu.ifps.emel_condominios_api.service.VisitaService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/visita")
public class VisitaController {
    VisitaService visitaService;

    public VisitaController(VisitaService visitaService) {
        this.visitaService = visitaService;
    }

    @PostMapping
    public ResponseEntity<Visita> cadastrarVisita(@RequestBody Visita novaVisita) {
        Visita visita = visitaService.cadastrarVisita(novaVisita);
        return ResponseEntity.ok(visita);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Visita> mostrarVisitaPorId(@PathVariable long id) {
        Visita visita = visitaService.mostrarVisitaPorId(id);
        return ResponseEntity.ok(visita);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Visita> editarVisita(@PathVariable long id, @RequestBody Visita novaVisita) {
        Visita visita = visitaService.editarVisita(id, novaVisita);
        return ResponseEntity.ok(visita);
    }
    
    @DeleteMapping("/{id}")
    public void deletarVisita(@PathVariable long id){
        visitaService.deletarVisita(id);
    }
    
}
