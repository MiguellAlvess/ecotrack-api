package br.db.ecotrack.ecotrack_api.controller.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDto(
    @NotBlank(message = "Nome é obrigatório") String name,
    @NotBlank(message = "Email é obrigatório") @Email(message = "Formato de email inválido") String email,
    @NotBlank(message = "Senha é obrigatória") @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres") String password) {
}
