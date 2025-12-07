package br.db.ecotrack.ecotrack_api.controller.dto.disposal.metrics;

public record DisposalResponseDestinationMetricsDto(
    String destination,
    Integer quantity) {
}
