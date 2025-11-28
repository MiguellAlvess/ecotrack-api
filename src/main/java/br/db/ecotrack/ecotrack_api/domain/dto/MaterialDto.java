package br.db.ecotrack.ecotrack_api.domain.dto;

public record MaterialDto(
        Long materialId,
        String type,
        String description) {
}
