package br.db.ecotrack.ecotrack_api.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDto(

    @NotNull(message = "Email é obrigatório") @Email String email,

    @NotBlank(message = "Senha é obrigatória") String password) {
}
