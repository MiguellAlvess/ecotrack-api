package br.db.ecotrack.ecotrack_api.service;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import br.db.ecotrack.ecotrack_api.controller.request.LoginRequestDto;
import br.db.ecotrack.ecotrack_api.controller.response.LoginResponseDto;
import br.db.ecotrack.ecotrack_api.controller.response.UserResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.mapper.UserMapper;
import br.db.ecotrack.ecotrack_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtEncoder jwtEncoder;
  private final UserMapper userMapper;
  private final long expiresIn = 86400L;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder,
      UserMapper userMapper) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtEncoder = jwtEncoder;
    this.userMapper = userMapper;
  }

  public LoginResponseDto login(LoginRequestDto loginRequestDto) {
    User user = userRepository.findByEmail(loginRequestDto.email())
        .orElseThrow(() -> new EntityNotFoundException("Email não encontrado:" + loginRequestDto.email()));

    boolean passwordMatches = checkPassword(loginRequestDto.password(), user.getPassword());
    if (!passwordMatches) {
      throw new IllegalArgumentException("Senha incorreta");
    }
    JwtClaimsSet jwt = JwtClaimsSet.builder()
        .issuer("ecotrack-api")
        .subject(user.getName())
        .expiresAt(Instant.now().plusSeconds(expiresIn))
        .issuedAt(Instant.now())
        .claim("email", user.getEmail())
        .build();

    String token = jwtEncoder.encode(JwtEncoderParameters.from(jwt)).getTokenValue();
    UserResponseDto userResponseDto = userMapper.toDto(user);

    return new LoginResponseDto(token, expiresIn, userResponseDto);
  }

  private boolean checkPassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

}
