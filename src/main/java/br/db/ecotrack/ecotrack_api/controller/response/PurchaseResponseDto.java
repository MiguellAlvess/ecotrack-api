package br.db.ecotrack.ecotrack_api.controller.response;

import java.time.LocalDate;

public record PurchaseResponseDto(
    Long purchaseId,
    Double quantity,
    LocalDate purchaseDate,
    String materialType) {
}
