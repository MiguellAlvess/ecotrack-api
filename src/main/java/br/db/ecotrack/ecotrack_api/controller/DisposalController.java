package br.db.ecotrack.ecotrack_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.db.ecotrack.ecotrack_api.controller.request.DisposalRequestDto;
import br.db.ecotrack.ecotrack_api.controller.response.DisposalResponseDto;
import br.db.ecotrack.ecotrack_api.service.DisposalService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController
@RequestMapping("/api/disposals")
public class DisposalController {

  private final DisposalService disposalService;

  public DisposalController(DisposalService disposalService) {
    this.disposalService = disposalService;
  }

  @PostMapping
  public ResponseEntity<?> createDisposal(@Valid @RequestBody DisposalRequestDto disposalRequestDto) {
    try {
      DisposalResponseDto savedDisposal = disposalService.createDisposal(disposalRequestDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(savedDisposal);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Erro ao processar a requisição: " + e.getMessage());
    }
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
