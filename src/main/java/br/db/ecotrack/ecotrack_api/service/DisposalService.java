package br.db.ecotrack.ecotrack_api.service;

import org.springframework.stereotype.Service;
import br.db.ecotrack.ecotrack_api.controller.request.DisposalRequestDto;
import br.db.ecotrack.ecotrack_api.controller.response.DisposalResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.Disposal;
import br.db.ecotrack.ecotrack_api.domain.entity.Material;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.mapper.DisposalMapper;
import br.db.ecotrack.ecotrack_api.repository.DisposalRepository;
import br.db.ecotrack.ecotrack_api.repository.MaterialRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DisposalService {

  private final DisposalRepository disposalRepository;
  private final DisposalMapper disposalMapper;
  private final MaterialRepository materialRepository;
  private final CurrentUserService currentUserService;

  public DisposalService(DisposalRepository disposalRepository, DisposalMapper disposalMapper,
      MaterialRepository materialRepository, CurrentUserService currentUserService) {
    this.disposalRepository = disposalRepository;
    this.disposalMapper = disposalMapper;
    this.materialRepository = materialRepository;
    this.currentUserService = currentUserService;
  }

  @Transactional
  public DisposalResponseDto createDisposal(DisposalRequestDto disposalRequestDto) {
    Material material = materialRepository.findById(disposalRequestDto.materialId())
        .orElseThrow(() -> new EntityNotFoundException("Material not found"));

    User currentUser = currentUserService.getCurrentUserEntity();

    Disposal disposal = disposalMapper.toEntity(disposalRequestDto);

    disposal.setMaterial(material);
    disposal.setUser(currentUser);
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
  public List<DisposalResponseDto> getAllDisposalsForCurrentUser() {
    User currentUser = currentUserService.getCurrentUserEntity();
    List<Disposal> disposals = disposalRepository.findByUser(currentUser);

    return disposals.stream()
        .map(disposal -> disposalMapper.toDto(disposal))
        .toList();
  }

}
