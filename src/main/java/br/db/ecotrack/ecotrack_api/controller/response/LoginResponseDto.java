package br.db.ecotrack.ecotrack_api.controller.response;

public record LoginResponseDto(

    String accessToken,
    long expiresIn,
    UserResponseDto user) {
}
