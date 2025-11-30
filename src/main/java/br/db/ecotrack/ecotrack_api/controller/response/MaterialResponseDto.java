package br.db.ecotrack.ecotrack_api.controller.response;

public record MaterialResponseDto(
    Long materialId,
    String type,
    String description) {
}
