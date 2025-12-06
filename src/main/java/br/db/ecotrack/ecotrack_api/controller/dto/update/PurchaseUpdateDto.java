package br.db.ecotrack.ecotrack_api.controller.dto.update;

import java.time.LocalDate;

import br.db.ecotrack.ecotrack_api.domain.enums.MaterialType;

public record PurchaseUpdateDto(
    String purchaseProduct,
    Integer quantity,
    MaterialType materialType,
    LocalDate purchaseDate) {
}
