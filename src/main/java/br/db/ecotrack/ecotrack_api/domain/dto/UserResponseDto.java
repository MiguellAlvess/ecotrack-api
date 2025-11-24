package br.db.ecotrack.ecotrack_api.domain.dto;

public record UserResponseDto(
        Long id,
        String name,
        String email) {
}
