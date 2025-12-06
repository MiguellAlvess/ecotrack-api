package br.db.ecotrack.ecotrack_api.controller.dto.response;

import java.time.LocalDate;
import br.db.ecotrack.ecotrack_api.domain.enums.MaterialType;

public record PurchaseResponseDto(
    Long purchaseId,
    String purchaseProduct,
    Integer quantity,
    MaterialType materialType,
    LocalDate purchaseDate) {
}
