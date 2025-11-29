package br.db.ecotrack.ecotrack_api.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import br.db.ecotrack.ecotrack_api.domain.dto.UserResponseDto;
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
        return userRepository.findByEmail(getCurrentUserEmail())
                .map(user -> new UserResponseDto(user.getUserId(), user.getName(), user.getEmail()))
                .orElseThrow(() -> new IllegalStateException("Usuário não encontrado"));
    }

}
