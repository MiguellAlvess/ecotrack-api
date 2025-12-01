package br.db.ecotrack.ecotrack_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
public class DisposalService {

  private final DisposalRepository disposalRepository;
  private final DisposalMapper disposalMapper;
  private final MaterialRepository materialRepository;
  private final UserRepository userRepository;
  private final CurrentUserService currentUserService;
  public Object getDisposalById;

  public DisposalService(DisposalRepository disposalRepository, DisposalMapper disposalMapper,
      MaterialRepository materialRepository, UserRepository userRepository, CurrentUserService currentUserService) {
    this.disposalRepository = disposalRepository;
    this.disposalMapper = disposalMapper;
    this.materialRepository = materialRepository;
    this.userRepository = userRepository;
    this.currentUserService = currentUserService;
  }

  @Transactional
  public DisposalResponseDto createDisposal(DisposalRequestDto disposalRequestDto) {
    Material material = materialRepository.findById(disposalRequestDto.materialId())
        .orElseThrow(() -> new EntityNotFoundException("Material not found"));

    String email = currentUserService.getCurrentUserEmail();
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

    Disposal disposal = disposalMapper.toEntity(disposalRequestDto);

    disposal.setMaterial(material);
    disposal.setUser(user);
    Disposal disposalSaved = disposalRepository.save(disposal);

    return disposalMapper.toDto(disposalSaved);
  }

  @Transactional(readOnly = true)
  public DisposalResponseDto getDisposalById(Long id) {
    return disposalRepository.findById(id)
        .map(disposal -> disposalMapper.toDto(disposal))
        .orElseThrow(() -> new EntityNotFoundException("Disposal not found: " + id));
  }

  @Transactional(readOnly = true)
  public List<DisposalResponseDto> getAllDisposal() {
    List<Disposal> disposals = disposalRepository.findAll();
    return disposals.stream()
        .map(disposal -> disposalMapper.toDto(disposal))
        .toList();
  }

  @Transactional(readOnly = true)
  public Disposal getDisposalEntityById(Long id) {
    return disposalRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Disposal not found: " + id));
  }

  @Transactional
  public void deleteDisposalById(Long id) {
    Disposal disposal = getDisposalEntityById(id);
    disposalRepository.delete(disposal);
  }

}
