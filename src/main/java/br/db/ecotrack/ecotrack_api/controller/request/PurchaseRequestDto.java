package br.db.ecotrack.ecotrack_api.controller.request;

import java.time.LocalDate;
import br.db.ecotrack.ecotrack_api.domain.enums.MeasurementUnit;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

public record PurchaseRequestDto(
    @NotNull(message = "Quantidade é obrigatória") @Positive(message = "Quantidade deve ser positiva") Double quantity,
    @NotNull(message = "A unidade é obrigatória") MeasurementUnit unit,
    @NotNull(message = "Data da compra é obrigatória") @PastOrPresent(message = "A data da compra não pode ser no futuro") LocalDate purchaseDate,
    @NotNull(message = "Id do Material é obrigatório") Long materialId) {
}