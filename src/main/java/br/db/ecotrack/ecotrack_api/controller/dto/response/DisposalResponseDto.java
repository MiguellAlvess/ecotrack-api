package br.db.ecotrack.ecotrack_api.controller.dto.response;

import java.time.LocalDate;
import br.db.ecotrack.ecotrack_api.domain.enums.DisposalDestination;
import br.db.ecotrack.ecotrack_api.domain.enums.MaterialType;

public record DisposalResponseDto(
    Long disposalId,
    String disposalProduct,
    Integer quantity,
    MaterialType materialType,
    DisposalDestination destination,
    LocalDate disposalDate) {
}
