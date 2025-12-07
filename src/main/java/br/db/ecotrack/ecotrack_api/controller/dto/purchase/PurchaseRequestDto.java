package br.db.ecotrack.ecotrack_api.controller.dto.purchase;

import java.time.LocalDate;
import br.db.ecotrack.ecotrack_api.domain.enums.MaterialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

public record PurchaseRequestDto(
    @NotBlank(message = "O produto é obrigatório") String purchaseProduct,
    @NotNull(message = "Quantidade é obrigatória") @Positive(message = "Quantidade deve ser positiva") Integer quantity,
    @NotNull(message = "O tipo de material é obrigatório") MaterialType materialType,
    @NotNull(message = "Data da compra é obrigatória") @PastOrPresent(message = "A data da compra não pode ser no futuro") LocalDate purchaseDate) {
}
