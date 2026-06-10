package br.edu.ifps.emel_condominios_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifps.emel_condominios_api.model.Visitante;
import br.edu.ifps.emel_condominios_api.service.VisitanteService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/visitante")
public class VisitanteContoller {

    VisitanteService visitanteService;

    public VisitanteContoller(VisitanteService visitanteService) {
        this.visitanteService = visitanteService;
    }

    @PostMapping
    public ResponseEntity<Visitante> cadastrarVisitante(@RequestBody Visitante novoVisitante) {
        Visitante visitante = visitanteService.cadastrarVisitante(novoVisitante);
        return ResponseEntity.ok(visitante);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Visitante> mostrarVisitantePorId(@PathVariable long id) {
        Visitante visitante = visitanteService.MostrarVisitantePorId(id);
        return ResponseEntity.ok(visitante);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Visitante> editarVisitante(@PathVariable long id, @RequestBody Visitante novoVisitante) {
        Visitante visitante = visitanteService.editarVisitante(id, novoVisitante);
        return ResponseEntity.ok(visitante);
    }

    @DeleteMapping("/{id}")
    public void deletarVisitante(@PathVariable long id){
        visitanteService.deletarVisitante(id);
    }
    
    
}
