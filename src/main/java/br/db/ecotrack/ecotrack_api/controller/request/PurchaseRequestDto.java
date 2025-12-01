package br.db.ecotrack.ecotrack_api.controller.request;

import java.time.LocalDate;

import br.db.ecotrack.ecotrack_api.domain.enums.MeasurementUnit;
import jakarta.validation.constraints.NotNull;

public record PurchaseRequestDto(
    @NotNull(message = "Quantidade é obrigatória")
    Double quantity,

    @NotNull(message = "A unidade é obrigatória")
    MeasurementUnit measurementUnit,

    @NotNull(message = "Data da compra é obrigatória")
    LocalDate purchaseDate,

    @NotNull(message = "Id do Material é obrigatório")
    Long materialId) {
}