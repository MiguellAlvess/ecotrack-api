package br.db.ecotrack.ecotrack_api.controller.request;

import java.time.LocalDate;

import br.db.ecotrack.ecotrack_api.domain.enums.DisposalDestination;
import br.db.ecotrack.ecotrack_api.domain.enums.MeasurementUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DisposalRequestDto(
    @NotNull(message = "Quantidade é obrigatória") @Positive(message = "Quantidade deve ser positiva") Double quantity,
    @NotNull(message = "Unidade é obrigatória") MeasurementUnit unit,
    @NotNull(message = "Destino é obrigatório") DisposalDestination destination,
    @NotBlank(message = "Data de descarte é obrigatório") String disposalDate,
    @NotNull(message = "Material ID é obrigatório") Long materialId) {
}
