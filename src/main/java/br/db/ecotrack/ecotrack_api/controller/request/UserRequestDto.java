package br.db.ecotrack.ecotrack_api.controller.request;

public record UserRequestDto(
        String name,
        String email,
        String password) {
}
