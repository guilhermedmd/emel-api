package br.edu.ifps.emel_condominios_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifps.emel_condominios_api.model.Morador;
import br.edu.ifps.emel_condominios_api.service.MoradorService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/morador")
public class MoradorController {

    MoradorService moradorService;
    
    public MoradorController(MoradorService moradorService) {
        this.moradorService = moradorService;
    }

    @PostMapping
    public ResponseEntity<Morador> cadastroMorador(@RequestBody Morador morador) {
        moradorService.criarMorador(morador);
        return ResponseEntity.ok(morador);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Morador> mostrarMoradorPorId(@PathVariable Long id) {
        Morador morador = moradorService.mostrarMoradorPorId(id);
        return ResponseEntity.ok(morador);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Morador> editarMorador(@PathVariable Long id, @RequestBody Morador novoMorador) {
        Morador morador = moradorService.editarMorador(id, novoMorador);
        return ResponseEntity.ok(morador);
    }

    @DeleteMapping("/{id}")
    public void deletarMorador(@PathVariable Long id){
        moradorService.deletarMorador(id);
    }
    
    
}
