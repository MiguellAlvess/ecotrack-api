package br.db.ecotrack.ecotrack_api.controller.request;

import jakarta.validation.constraints.NotBlank;

public record MaterialRequestDto(
    @NotBlank(message = "Tipo do material é obrigatório") String type,
    String description) {
}
