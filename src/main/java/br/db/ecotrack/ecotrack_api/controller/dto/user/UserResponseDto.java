package br.db.ecotrack.ecotrack_api.controller.dto.user;

public record UserResponseDto(
    Long userId,
    String name,
    String email) {
}
