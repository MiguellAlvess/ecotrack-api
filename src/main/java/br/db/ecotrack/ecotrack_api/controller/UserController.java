package br.db.ecotrack.ecotrack_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.db.ecotrack.ecotrack_api.domain.entity.dto.UserRequestDto;
import br.db.ecotrack.ecotrack_api.domain.entity.dto.UserResponseDto;
import br.db.ecotrack.ecotrack_api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<UserResponseDto> postMethodName(@RequestBody UserRequestDto userRequestDto) {
    UserResponseDto createdUserDto = userService.createUser(userRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDto);
  }

}
