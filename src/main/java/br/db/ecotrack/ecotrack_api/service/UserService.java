package br.db.ecotrack.ecotrack_api.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.domain.entity.dto.UserRequestDto;
import br.db.ecotrack.ecotrack_api.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User createUser(UserRequestDto userRequestDto) {
    User user = new User();

    user.setName(userRequestDto.name());
    user.setEmail(userRequestDto.email());
    user.setPassword(userRequestDto.password());

    Optional<User> existEmail = userRepository.findByEmail(user.getEmail());
    if (existEmail.isPresent()) {
      throw new IllegalArgumentException("Email already in use");
    }

    return userRepository.save(user);
  }
}
