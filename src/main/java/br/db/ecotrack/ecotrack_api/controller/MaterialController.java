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
import br.db.ecotrack.ecotrack_api.controller.request.MaterialRequestDto;
import br.db.ecotrack.ecotrack_api.controller.response.MaterialResponseDto;
import br.db.ecotrack.ecotrack_api.service.MaterialService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

  private final MaterialService materialService;

  public MaterialController(MaterialService materialService) {
    this.materialService = materialService;
  }

  @GetMapping
  public ResponseEntity<List<MaterialResponseDto>> getAllMaterials() {
    List<MaterialResponseDto> materials = materialService.getAll();
    return ResponseEntity.ok(materials);
  }

  @GetMapping("/{id}")
  public ResponseEntity<MaterialResponseDto> getMaterialById(@PathVariable Long id) {
    try {
      MaterialResponseDto material = materialService.getById(id);
      return ResponseEntity.ok(material);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping
  public ResponseEntity<MaterialResponseDto> createMaterial(@RequestBody @Valid MaterialRequestDto materialRequestDto) {
    try {
      MaterialResponseDto savedMaterial = materialService.createMaterial(materialRequestDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(savedMaterial);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
  }

  @PatchMapping("/{id}")
  public ResponseEntity<MaterialResponseDto> updateMaterial(@PathVariable Long id,
      @RequestBody @Valid MaterialRequestDto materialRequestDto) {
    try {
      MaterialResponseDto updated = materialService.updateMaterial(id, materialRequestDto);
      return ResponseEntity.ok(updated);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }
}
