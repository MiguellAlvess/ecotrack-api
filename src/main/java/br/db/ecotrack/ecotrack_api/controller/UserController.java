package br.db.ecotrack.ecotrack_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.db.ecotrack.ecotrack_api.controller.dto.request.UserRequestDto;
import br.db.ecotrack.ecotrack_api.controller.dto.response.UserResponseDto;
import br.db.ecotrack.ecotrack_api.service.CurrentUserService;
import br.db.ecotrack.ecotrack_api.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final CurrentUserService currentUserService;

  public UserController(UserService userService, CurrentUserService currentUserService) {
    this.userService = userService;
    this.currentUserService = currentUserService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
    try {
      UserResponseDto userDto = userService.getUserById(id);
      return ResponseEntity.ok(userDto);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PatchMapping("/{id}")
  public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id,
      @RequestBody @Valid UserRequestDto userRequestDto) {
    try {
      UserResponseDto updatedUserDto = userService.updateUser(id, userRequestDto);
      return ResponseEntity.ok(updatedUserDto);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    try {
      userService.deleteUser(id);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/me")
  public UserResponseDto me() {
    UserResponseDto userResponseDto = currentUserService.get();
    return userResponseDto;
  }
}
