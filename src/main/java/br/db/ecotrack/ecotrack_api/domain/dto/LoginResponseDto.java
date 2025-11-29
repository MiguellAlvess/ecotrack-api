package br.db.ecotrack.ecotrack_api.domain.dto;

public record LoginResponseDto(

    String acessToken,
    long expiresIn,
    UserResponseDto user
    ) {
}
