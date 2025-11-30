package br.db.ecotrack.ecotrack_api.controller.response;

public record LoginResponseDto(

    String acessToken,
    long expiresIn,
    UserResponseDto user) {
}
