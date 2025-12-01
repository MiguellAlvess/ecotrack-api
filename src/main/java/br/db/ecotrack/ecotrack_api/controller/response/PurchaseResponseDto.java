package br.db.ecotrack.ecotrack_api.controller.response;

import java.time.LocalDate;

import br.db.ecotrack.ecotrack_api.domain.enums.MeasurementUnit;

public record PurchaseResponseDto(
    Long purchaseId,
    Double quantity,
    MeasurementUnit measurementUnit,
    LocalDate purchaseDate,
    String materialType) { 
}
