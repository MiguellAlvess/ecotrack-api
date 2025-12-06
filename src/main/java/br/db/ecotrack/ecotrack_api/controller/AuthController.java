package br.db.ecotrack.ecotrack_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.db.ecotrack.ecotrack_api.controller.dto.request.LoginRequestDto;
import br.db.ecotrack.ecotrack_api.controller.dto.request.UserRequestDto;
import br.db.ecotrack.ecotrack_api.controller.dto.response.LoginResponseDto;
import br.db.ecotrack.ecotrack_api.service.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/auth")
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

  @PostMapping("/register")
  public ResponseEntity<LoginResponseDto> register(
      @RequestBody @Valid UserRequestDto userRequestDto) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(authService.createUser(userRequestDto));
  }
}
