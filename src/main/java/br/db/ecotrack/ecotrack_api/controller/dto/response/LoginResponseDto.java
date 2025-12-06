package br.db.ecotrack.ecotrack_api.controller.dto.response;

public record LoginResponseDto(

    String accessToken,
    long expiresIn,
    UserResponseDto user) {
}
