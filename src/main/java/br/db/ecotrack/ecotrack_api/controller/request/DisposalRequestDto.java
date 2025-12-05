package br.db.ecotrack.ecotrack_api.controller.request;

import java.time.LocalDate;
import br.db.ecotrack.ecotrack_api.domain.enums.DisposalDestination;
import br.db.ecotrack.ecotrack_api.domain.enums.MaterialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

public record DisposalRequestDto(
    @NotBlank(message = "O produto é obrigatório") String disposalProduct,
    @NotNull(message = "Quantidade é obrigatória") @Positive(message = "Quantidade deve ser positiva") Integer quantity,
    @NotNull(message = "O tipo de material é obrigatório") MaterialType materialType,
    @NotNull(message = "Destino é obrigatório") DisposalDestination destination,
    @NotNull(message = "Data de descarte é obrigatório") @PastOrPresent(message = "A data de descarte não pode ser no futuro") LocalDate disposalDate) {
}
