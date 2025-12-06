package br.db.ecotrack.ecotrack_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.db.ecotrack.ecotrack_api.controller.dto.metrics.DisposalResponseDestinationMetricsDto;
import br.db.ecotrack.ecotrack_api.controller.dto.metrics.DisposalResponseMetricsDto;
import br.db.ecotrack.ecotrack_api.controller.dto.request.DisposalRequestDto;
import br.db.ecotrack.ecotrack_api.controller.dto.response.DisposalResponseDto;
import br.db.ecotrack.ecotrack_api.controller.dto.update.DisposalUpdateDto;
import br.db.ecotrack.ecotrack_api.service.DisposalService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

  @GetMapping("/{disposalId}")
  public ResponseEntity<?> getDisposalById(@PathVariable Long disposalId) {
    try {
      DisposalResponseDto disposal = disposalService.getDisposalById(disposalId);
      return ResponseEntity.ok(disposal);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping
  public ResponseEntity<List<DisposalResponseDto>> getAllDisposals() {
    List<DisposalResponseDto> userDisposals = disposalService.getAllDisposalsForCurrentUser();
    return ResponseEntity.ok(userDisposals);
  }

  @GetMapping("/metrics")
  public ResponseEntity<?> getTotalItensDisposal() {
    try {
      DisposalResponseMetricsDto disposalMetricsDto = disposalService.getTotalItensDisposal();
      return ResponseEntity.ok(disposalMetricsDto);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Erro ao processar a requisição: " + e.getMessage());
    }
  }

  @GetMapping("/metrics/destination")
  public ResponseEntity<?> getMostUsedDestination() {
    try {
      DisposalResponseDestinationMetricsDto dto = disposalService.getMostUsedDestinationDisposal();
      return ResponseEntity.ok(dto);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Erro ao processar a requisição: " + e.getMessage());
    }
  }

  @PatchMapping("/{disposalId}")
  public ResponseEntity<DisposalResponseDto> updateDisposal(@PathVariable Long disposalId,
      @RequestBody DisposalUpdateDto disposalUpdateDto) {
    try {
      DisposalResponseDto updateDiposalDto = disposalService.updateDisposal(disposalId, disposalUpdateDto);
      return ResponseEntity.ok(updateDiposalDto);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{disposalId}")
  public ResponseEntity<?> deleteDisposal(@PathVariable Long disposalId) {
    try {
      disposalService.deleteDisposalById(disposalId);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

}
