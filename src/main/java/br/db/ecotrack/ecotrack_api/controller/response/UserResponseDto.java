package br.db.ecotrack.ecotrack_api.controller.response;

public record UserResponseDto(
    Long userId,
    String name,
    String email) {
}
