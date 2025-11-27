package br.db.ecotrack.ecotrack_api.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.db.ecotrack.ecotrack_api.domain.dto.UserRequestDto;
import br.db.ecotrack.ecotrack_api.domain.dto.UserResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.domain.mapper.UserMapper;
import br.db.ecotrack.ecotrack_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.userMapper = userMapper;
  }

  @Transactional
  public UserResponseDto createUser(UserRequestDto userRequestDto) {
    User user = new User();

    user.setName(userRequestDto.name());
    user.setEmail(userRequestDto.email());
    String plainPassword = userRequestDto.password();

    Optional<User> existEmail = userRepository.findByEmail(user.getEmail());
    if (existEmail.isPresent()) {
      throw new IllegalArgumentException("Email already in use");
    }

    String senhaHasheada = passwordEncoder.encode(plainPassword);
    user.setPassword(senhaHasheada);

    User savedUser = userRepository.save(user);

    return userMapper.toDto(savedUser);
  }

  @Transactional(readOnly = true)
  public UserResponseDto getUserById(Long id) {
    return userRepository.findById(id)
        .map(user -> userMapper.toDto(user))
        .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
  }

  @Transactional
  public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
    User existingUser = getUserEntityById(id);

    if (userRequestDto.name() != null) {
      existingUser.setName(userRequestDto.name());
    }
    if (userRequestDto.email() != null) {
      validateEmailForUpdate(userRequestDto.email(), id);
      existingUser.setEmail(userRequestDto.email());
    }
    if (userRequestDto.password() != null) {
      existingUser.setPassword(passwordEncoder.encode(userRequestDto.password()));
    }

    User updatedUser = userRepository.save(existingUser);
    return userMapper.toDto(updatedUser);
  }

  @Transactional(readOnly = true)
  public void validateEmailForUpdate(String email, Long userId) {
    userRepository.findByEmail(email)
        .ifPresent(user -> {
          if (!user.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Email already in use: " + email);
          }
        });
  }

  @Transactional(readOnly = true)
  public User getUserEntityById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
  }

  @Transactional
  public void deleteUser(Long id) {
    User user = getUserEntityById(id);
    userRepository.delete(user);
  }

}