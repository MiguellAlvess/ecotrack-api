package br.db.ecotrack.ecotrack_api.controller.dto.update;

import java.time.LocalDate;

import br.db.ecotrack.ecotrack_api.domain.enums.DisposalDestination;
import br.db.ecotrack.ecotrack_api.domain.enums.MaterialType;

public record DisposalUpdateDto(
    String disposalProduct,
    Integer quantity,
    MaterialType materialType,
    DisposalDestination destination,
    LocalDate disposalDate) {
}
