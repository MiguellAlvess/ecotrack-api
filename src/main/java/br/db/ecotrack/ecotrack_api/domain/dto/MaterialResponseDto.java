package br.db.ecotrack.ecotrack_api.domain.dto;

public record MaterialResponseDto (
        Long materialId,
        String type,
        String description) {
}