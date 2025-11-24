package br.db.ecotrack.ecotrack_api.domain.dto;

public record UserRequestDto(
    String name,
    String email,
    String password) {
}
