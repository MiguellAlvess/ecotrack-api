package br.db.ecotrack.ecotrack_api.domain.dto;

import java.time.LocalDate;

import br.db.ecotrack.ecotrack_api.domain.enums.DisposalDestination;
import br.db.ecotrack.ecotrack_api.domain.enums.MeasurementUnit;

public record DisposalResponseDto(
    Long disposalId,
    Double quantity,
    MeasurementUnit unit,
    DisposalDestination destination,
    LocalDate disposalDate,
    String materialType) {
}
