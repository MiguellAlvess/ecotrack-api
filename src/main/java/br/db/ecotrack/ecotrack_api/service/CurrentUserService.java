package br.db.ecotrack.ecotrack_api.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import br.db.ecotrack.ecotrack_api.controller.dto.response.UserResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.repository.UserRepository;

@Service
public class CurrentUserService {

  private final UserRepository userRepository;

  public CurrentUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Jwt jwt = (Jwt) authentication.getPrincipal();
    return jwt.getClaimAsString("email");
  }

  public UserResponseDto get() {
    User currentUser = findCurrentUserEntity();
    return new UserResponseDto(currentUser.getUserId(), currentUser.getName(), currentUser.getEmail());
  }

  public User getCurrentUserEntity() {
    return findCurrentUserEntity();
  }

  private User findCurrentUserEntity() {
    String email = getCurrentUserEmail();
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException(
            "Usuário autenticado com o email '" + email + "' não foi encontrado no banco de dados."));
  }

}
