package br.db.ecotrack.ecotrack_api.domain.dto;

public record DisposalResponseDto(
    Long disposalId,
    Double quantity,
    String unit,
    String destination,
    String disposalDate,
    String materialType) {
}
