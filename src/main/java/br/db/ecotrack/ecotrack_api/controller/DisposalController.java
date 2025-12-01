package br.db.ecotrack.ecotrack_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.db.ecotrack.ecotrack_api.controller.request.DisposalRequestDto;
import br.db.ecotrack.ecotrack_api.controller.response.DisposalResponseDto;
import br.db.ecotrack.ecotrack_api.service.DisposalService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/disposals")
public class DisposalController {

  private final DisposalService disposalService;

  public DisposalController(DisposalService disposalService) {
    this.disposalService = disposalService;
  }

  @PostMapping
  public ResponseEntity<DisposalResponseDto> createDisposal(@Valid @RequestBody DisposalRequestDto disposalRequestDto) {
    DisposalResponseDto savedDisposal = disposalService.createDisposal(disposalRequestDto);
    return ResponseEntity.status(201).body(savedDisposal);
  }

  @GetMapping("/{id}")
  public ResponseEntity<DisposalResponseDto> getDisposalById(@PathVariable Long id) {
    return ResponseEntity.ok(disposalService.getDisposalById(id));
  }

  @GetMapping
  public ResponseEntity<List<DisposalResponseDto>> getAllDisposal() {
    return ResponseEntity.ok(disposalService.getAllDisposal());
  }

}
