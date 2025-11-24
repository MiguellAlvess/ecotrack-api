package br.db.ecotrack.ecotrack_api.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.db.ecotrack.ecotrack_api.domain.dto.UserRequestDto;
import br.db.ecotrack.ecotrack_api.domain.dto.UserResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.domain.mapper.UserMapper;
import br.db.ecotrack.ecotrack_api.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;


  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
   
  }

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
    return new UserResponseDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
  }

  public Optional<User> getUserById(Long id){
    return userRepository.findById(id);
  }

}