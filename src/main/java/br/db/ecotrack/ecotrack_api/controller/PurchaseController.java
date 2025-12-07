package br.db.ecotrack.ecotrack_api.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.db.ecotrack.ecotrack_api.controller.dto.purchase.PurchaseRequestDto;
import br.db.ecotrack.ecotrack_api.controller.dto.purchase.PurchaseResponseDto;
import br.db.ecotrack.ecotrack_api.controller.dto.purchase.PurchaseUpdateDto;
import br.db.ecotrack.ecotrack_api.controller.dto.purchase.metrics.MaterialAmountSummaryDto;
import br.db.ecotrack.ecotrack_api.controller.dto.purchase.metrics.TotalQuantityCurrentMonthDto;
import br.db.ecotrack.ecotrack_api.service.PurchaseService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

  private final PurchaseService purchaseService;

  public PurchaseController(PurchaseService purchaseService) {
    this.purchaseService = purchaseService;
  }

  @PostMapping
  public ResponseEntity<?> createPurchase(@RequestBody @Valid PurchaseRequestDto purchaseRequestDto) {
    try {
      PurchaseResponseDto purchaseDTO = purchaseService.createPurchase(purchaseRequestDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(purchaseDTO);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Erro ao processar a requisição");
    }
  }

  @GetMapping("/{purchaseId}")
  public ResponseEntity<?> getPurchaseById(@PathVariable Long purchaseId) {
    try {
      PurchaseResponseDto purchaseDTO = purchaseService.getPurchaseById(purchaseId);
      return ResponseEntity.ok(purchaseDTO);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping
  public ResponseEntity<List<PurchaseResponseDto>> getAllPurchases() {
    List<PurchaseResponseDto> purchases = purchaseService.getAllPurchasesForCurrentUser();
    return ResponseEntity.ok(purchases);
  }

  @GetMapping("/total-itens-purchased-30-days")
  public ResponseEntity<?> getTotalItensPurchased() {
    try {
      TotalQuantityCurrentMonthDto totalQuantity = purchaseService.getTotalItensPurchased();
      return ResponseEntity.ok(totalQuantity);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Erro ao processar a requisição: " + e.getMessage());
    }
  }

  @GetMapping("/purchases-material-summary-30-days")
  public ResponseEntity<?> getPurchasesMaterialSummary() {
    try {
      MaterialAmountSummaryDto materialSummary = purchaseService.getMaterialAmountSummaryDto();
      return ResponseEntity.ok(materialSummary);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Erro ao processar a requisição: " + e.getMessage());
    }
  }

  @PatchMapping("/{purchaseId}")
  public ResponseEntity<PurchaseResponseDto> updatePurchase(@PathVariable Long purchaseId,
      @RequestBody PurchaseUpdateDto purchaseUpdateDto) {
    try {
      PurchaseResponseDto updatePurchaseDto = purchaseService.updatePurchase(purchaseId, purchaseUpdateDto);
      return ResponseEntity.ok(updatePurchaseDto);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{purchaseId}")
  public ResponseEntity<?> deletePurchase(@PathVariable Long purchaseId) {
    try {
      purchaseService.deletePurchase(purchaseId);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }
}
