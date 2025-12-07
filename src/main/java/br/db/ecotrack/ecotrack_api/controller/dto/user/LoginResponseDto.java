package br.db.ecotrack.ecotrack_api.controller.dto.user;

public record LoginResponseDto(

    String accessToken,
    long expiresIn,
    UserResponseDto user) {
}
