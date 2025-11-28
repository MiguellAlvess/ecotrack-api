package br.db.ecotrack.ecotrack_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.db.ecotrack.ecotrack_api.domain.dto.MaterialDto;
import br.db.ecotrack.ecotrack_api.service.MaterialService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    public ResponseEntity<List<MaterialDto>> getAllMaterials() {
        List<MaterialDto> materials = materialService.getAll();
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialDto> getMaterialById(@PathVariable Long id) {
        try {
            MaterialDto material = materialService.getById(id);
            return ResponseEntity.ok(material);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<MaterialDto> createMaterial(@RequestBody MaterialDto material) {
        try {
            MaterialDto savedMaterial = materialService.createMaterial(material);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMaterial);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MaterialDto> updateMaterial(@PathVariable Long id, @RequestBody MaterialDto materialDto) {
        try {
            MaterialDto updated = materialService.updateMaterial(id, materialDto);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
