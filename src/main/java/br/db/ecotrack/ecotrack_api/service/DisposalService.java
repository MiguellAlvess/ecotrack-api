package br.db.ecotrack.ecotrack_api.service;

import org.springframework.stereotype.Service;
import br.db.ecotrack.ecotrack_api.controller.request.DisposalRequestDto;
import br.db.ecotrack.ecotrack_api.controller.response.DisposalResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.Disposal;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.mapper.DisposalMapper;
import br.db.ecotrack.ecotrack_api.repository.DisposalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DisposalService {

  private final DisposalRepository disposalRepository;
  private final DisposalMapper disposalMapper;
  private final CurrentUserService currentUserService;

  public DisposalService(DisposalRepository disposalRepository, DisposalMapper disposalMapper,
      CurrentUserService currentUserService) {
    this.disposalRepository = disposalRepository;
    this.disposalMapper = disposalMapper;
    this.currentUserService = currentUserService;
  }

  @Transactional
  public DisposalResponseDto createDisposal(DisposalRequestDto disposalRequestDto) {
    User currentUser = currentUserService.getCurrentUserEntity();

    Disposal disposal = disposalMapper.toEntity(disposalRequestDto);

    disposal.setUser(currentUser);
    Disposal disposalSaved = disposalRepository.save(disposal);

    return disposalMapper.toDto(disposalSaved);
  }

  @Transactional(readOnly = true)
  public DisposalResponseDto getDisposalById(Long id) {
    Disposal disposal = findDisposalByIdAndCurrentUser(id);
    return disposalMapper.toDto(disposal);
  }

  @Transactional(readOnly = true)
  public List<DisposalResponseDto> getAllDisposalsForCurrentUser() {
    User currentUser = currentUserService.getCurrentUserEntity();
    List<Disposal> disposals = disposalRepository.findByUser(currentUser);

    return disposals.stream()
        .map(disposal -> disposalMapper.toDto(disposal))
        .toList();
  }

  @Transactional
  public void deleteDisposalById(Long id) {
    Disposal disposal = findDisposalByIdAndCurrentUser(id);
    disposalRepository.delete(disposal);
  }

  private Disposal findDisposalByIdAndCurrentUser(Long id) {
    User currentUser = currentUserService.getCurrentUserEntity();
    Disposal disposal = disposalRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Descarte não encontrado com o id: " + id));

    if (!disposal.getUser().getUserId().equals(currentUser.getUserId())) {
      throw new EntityNotFoundException("Descarte não encontrado com o id: " + id);
    }
    return disposal;
  }
}
