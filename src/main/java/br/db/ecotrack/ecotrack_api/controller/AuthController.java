package br.db.ecotrack.ecotrack_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.db.ecotrack.ecotrack_api.controller.request.LoginRequestDto;
import br.db.ecotrack.ecotrack_api.controller.response.LoginResponseDto;
import br.db.ecotrack.ecotrack_api.service.AuthService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/login")
@AllArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping()
  public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto LoginRequest) {
    try {
      LoginResponseDto loginResponseDto = authService.login(LoginRequest);
      return ResponseEntity.ok(loginResponseDto);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }
}
