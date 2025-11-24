package br.db.ecotrack.ecotrack_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.db.ecotrack.ecotrack_api.domain.dto.UserRequestDto;
import br.db.ecotrack.ecotrack_api.domain.dto.UserResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.domain.mapper.UserMapper;
import br.db.ecotrack.ecotrack_api.service.UserService;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UserMapper userMapper;

  public UserController(UserService userService, UserMapper userMapper) {
    this.userService = userService;
    this.userMapper = userMapper;
  }

  @PostMapping
  public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
    UserResponseDto createdUserDto = userService.createUser(userRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDto);
  }

  @GetMapping
  public ResponseEntity<UserResponseDto> findUserById(@RequestParam Long id) {
    Optional<User> user = userService.getUserById(id);
  
    if (user.isPresent()) {
        return ResponseEntity.ok(userMapper.toDto(user.get()));
    }

    return ResponseEntity.notFound().build();
  }
}
