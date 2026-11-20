package br.edu.ifps.emel_condominios_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifps.emel_condominios_api.model.AreaLazer;
import br.edu.ifps.emel_condominios_api.service.AreaLazerService;

@RestController
@RequestMapping("/area-lazer")
public class AreaLazerController {

    private final AreaLazerService areaLazerService;

    public AreaLazerController(AreaLazerService areaLazerService) {
        this.areaLazerService = areaLazerService;
    }

    @PostMapping
    public ResponseEntity<AreaLazer> cadastrarAreaLazer(@RequestBody AreaLazer areaLazer) {
        AreaLazer novaArea = areaLazerService.cadastrarAreaLazer(areaLazer);
        return ResponseEntity.ok(novaArea);
    }

    @GetMapping
    public ResponseEntity<List<AreaLazer>> listarAreasLazer() {
        List<AreaLazer> areas = areaLazerService.listarAreasLazer();
        return ResponseEntity.ok(areas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaLazer> mostrarAreaLazerPorId(@PathVariable Long id) {
        AreaLazer area = areaLazerService.mostrarAreaLazerPorId(id);
        return ResponseEntity.ok(area);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AreaLazer> editarAreaLazer(@PathVariable Long id, @RequestBody AreaLazer novaAreaLazer) {
        AreaLazer areaAtualizada = areaLazerService.editarAreaLazer(id, novaAreaLazer);
        return ResponseEntity.ok(areaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAreaLazer(@PathVariable Long id) {
        areaLazerService.deletarAreaLazer(id);
        return ResponseEntity.ok().build();
    }
}
