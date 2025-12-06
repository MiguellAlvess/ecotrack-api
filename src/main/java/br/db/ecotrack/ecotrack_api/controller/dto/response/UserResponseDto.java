package br.db.ecotrack.ecotrack_api.controller.dto.response;

public record UserResponseDto(
    Long userId,
    String name,
    String email) {
}
