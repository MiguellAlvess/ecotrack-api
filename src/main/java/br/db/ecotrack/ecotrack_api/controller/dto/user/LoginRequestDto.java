package br.db.ecotrack.ecotrack_api.controller.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
    @NotBlank(message = "Email é obrigatório") @Email String email,
    @NotBlank(message = "Senha é obrigatória") String password) {
}
