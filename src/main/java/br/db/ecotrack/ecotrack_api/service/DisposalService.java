package br.db.ecotrack.ecotrack_api.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import br.db.ecotrack.ecotrack_api.controller.request.DisposalRequestDto;
import br.db.ecotrack.ecotrack_api.controller.response.DisposalResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.Disposal;
import br.db.ecotrack.ecotrack_api.domain.entity.Material;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.mapper.DisposalMapper;
import br.db.ecotrack.ecotrack_api.repository.DisposalRepository;
import br.db.ecotrack.ecotrack_api.repository.MaterialRepository;
import br.db.ecotrack.ecotrack_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class DisposalService {

  private final DisposalRepository disposalRepository;
  private final DisposalMapper disposalMapper;
  private final MaterialRepository materialRepository;
  private final UserRepository userRepository;

  public DisposalService(DisposalRepository disposalRepository, DisposalMapper disposalMapper,
      MaterialRepository materialRepository, UserRepository userRepository) {
    this.disposalRepository = disposalRepository;
    this.disposalMapper = disposalMapper;
    this.materialRepository = materialRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public DisposalResponseDto createDisposal(DisposalRequestDto disposalRequestDto) {
    Material material = materialRepository.findById(disposalRequestDto.materialId())
        .orElseThrow(() -> new EntityNotFoundException("Material not found"));

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Jwt jwt = (Jwt) auth.getPrincipal();
    String email = jwt.getClaim("email");
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

    Disposal disposal = disposalMapper.toEntity(disposalRequestDto);

    disposal.setMaterial(material);
    disposal.setUser(user);
    Disposal disposalSaved = disposalRepository.save(disposal);

    return disposalMapper.toDto(disposalSaved);
  }

}
