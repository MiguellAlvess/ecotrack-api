package br.db.ecotrack.ecotrack_api.domain.dto;

public record UserResponseDto(
        Long userId,
        String name,
        String email) {
}
