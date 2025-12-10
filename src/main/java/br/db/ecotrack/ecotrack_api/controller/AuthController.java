package br.db.ecotrack.ecotrack_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.db.ecotrack.ecotrack_api.controller.dto.user.LoginRequestDto;
import br.db.ecotrack.ecotrack_api.controller.dto.user.LoginResponseDto;
import br.db.ecotrack.ecotrack_api.controller.dto.user.UserRequestDto;
import br.db.ecotrack.ecotrack_api.service.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(
      @RequestBody @Valid LoginRequestDto loginRequestDto) {
    return ResponseEntity.ok(authService.login(loginRequestDto));
  }

  @PostMapping
  public ResponseEntity<LoginResponseDto> register(
      @RequestBody @Valid UserRequestDto userRequestDto) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(authService.createUser(userRequestDto));
  }
}
