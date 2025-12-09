package br.db.ecotrack.ecotrack_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import br.db.ecotrack.ecotrack_api.controller.dto.user.UserRequestDto;
import br.db.ecotrack.ecotrack_api.controller.dto.user.UserResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.mapper.UserMapper;
import br.db.ecotrack.ecotrack_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  private User existingUser;
  private User updatedUser;
  private UserRequestDto userRequestDto;
  private UserResponseDto userResponseDto;

  @BeforeEach
  void setup() {
    existingUser = new User();
    existingUser.setUserId(1L);
    existingUser.setName("Old Name");
    existingUser.setEmail("old@email.com");
    existingUser.setPassword("oldPassword");

    updatedUser = new User();
    updatedUser.setUserId(1L);
    updatedUser.setName("New Name");
    updatedUser.setEmail("new@email.com");
    updatedUser.setPassword("encodedPassword");

    userRequestDto = new UserRequestDto(
        "New Name",
        "new@email.com",
        "newPassword");

    userResponseDto = new UserResponseDto(
        1L,
        "New Name",
        "new@email.com");
  }

  @Test
  void getUserById_shouldReturnDto_whenUserExists() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userMapper.toDto(existingUser)).thenReturn(userResponseDto);

    UserResponseDto result = userService.getUserById(1L);

    assertNotNull(result);
    assertEquals(userResponseDto, result);

    verify(userRepository).findById(1L);
    verify(userMapper).toDto(existingUser);
  }

  @Test
  void getUserById_shouldThrow_whenUserNotFound() {
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
        () -> userService.getUserById(99L));

    assertEquals("User not found: 99", exception.getMessage());

    verify(userRepository).findById(99L);
    verifyNoInteractions(userMapper);
  }

  @Test
  void updateUser_shouldUpdateSuccessfully() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userRepository.findByEmail(userRequestDto.email())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(userRequestDto.password())).thenReturn("encodedPassword");
    when(userRepository.save(existingUser)).thenReturn(updatedUser);
    when(userMapper.toDto(updatedUser)).thenReturn(userResponseDto);

    UserResponseDto result = userService.updateUser(1L, userRequestDto);

    assertNotNull(result);
    assertEquals(userResponseDto, result);

    assertEquals("New Name", existingUser.getName());
    assertEquals("new@email.com", existingUser.getEmail());
    assertEquals("encodedPassword", existingUser.getPassword());
  }

  @Test
  void updateUser_shouldThrow_whenEmailAlreadyUsedByDifferentUser() {
    User otherUser = new User();
    otherUser.setUserId(2L);

    when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userRepository.findByEmail(userRequestDto.email())).thenReturn(Optional.of(otherUser));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> userService.updateUser(1L, userRequestDto));

    assertEquals("Email already in use: " + userRequestDto.email(), exception.getMessage());
  }

  @Test
  void deleteUser_shouldDeleteSuccessfully() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

    userService.deleteUser(1L);

    verify(userRepository).findById(1L);
    verify(userRepository).delete(existingUser);
  }

  @Test
  void deleteUser_shouldThrow_whenUserNotFound() {
    when(userRepository.findById(50L)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
        () -> userService.deleteUser(50L));

    assertEquals("User not found: 50", exception.getMessage());
  }

  @Test
  void findByEmail_shouldReturnUser_whenEmailExists() {
    when(userRepository.findByEmail("old@email.com"))
        .thenReturn(Optional.of(existingUser));

    User result = userService.findByEmail("old@email.com");

    assertNotNull(result);
    assertEquals(existingUser, result);
  }

  @Test
  void findByEmail_shouldThrow_whenEmailNotFound() {
    when(userRepository.findByEmail("missing@email.com"))
        .thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> userService.findByEmail("missing@email.com"));

    assertEquals("Email não encontrado: missing@email.com", exception.getMessage());
  }
}
