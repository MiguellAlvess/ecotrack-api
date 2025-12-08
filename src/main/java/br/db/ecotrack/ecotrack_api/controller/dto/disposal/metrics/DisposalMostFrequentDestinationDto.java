package br.db.ecotrack.ecotrack_api.controller.dto.disposal.metrics;

public record DisposalMostFrequentDestinationDto(
    String destination,
    Integer quantity) {
}
