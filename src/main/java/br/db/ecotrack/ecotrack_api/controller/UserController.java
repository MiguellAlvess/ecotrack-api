package br.db.ecotrack.ecotrack_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.db.ecotrack.ecotrack_api.domain.dto.UserRequestDto;
import br.db.ecotrack.ecotrack_api.domain.dto.UserResponseDto;
import br.db.ecotrack.ecotrack_api.service.CurrentUserService;
import br.db.ecotrack.ecotrack_api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final CurrentUserService currentUserService;

  public UserController(UserService userService, CurrentUserService currentUserService) {
    this.userService = userService;
    this.currentUserService = currentUserService;
  }

  @PostMapping
  public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
    try {
      UserResponseDto createdUserDto = userService.createUser(userRequestDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDto);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
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
  public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody UserRequestDto userRequestDto) {
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
