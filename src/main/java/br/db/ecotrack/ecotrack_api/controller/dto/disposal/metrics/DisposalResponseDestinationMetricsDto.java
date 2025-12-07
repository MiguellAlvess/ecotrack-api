package br.db.ecotrack.ecotrack_api.controller.dto.metrics;

public record DisposalResponseDestinationMetricsDto(
    String destination,
    Integer quantity) {
}
