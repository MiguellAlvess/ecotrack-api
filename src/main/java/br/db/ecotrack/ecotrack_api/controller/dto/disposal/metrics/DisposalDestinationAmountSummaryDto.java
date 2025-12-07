package br.db.ecotrack.ecotrack_api.controller.dto.disposal.metrics;

import java.util.Map;

public record DisposalDestinationAmountSummaryDto(
    Map<String, Integer> destinationAmountSummary) {
}
