package br.db.ecotrack.ecotrack_api.service;

import org.springframework.stereotype.Service;
import br.db.ecotrack.ecotrack_api.controller.request.DisposalRequestDto;
import br.db.ecotrack.ecotrack_api.controller.response.DisposalResponseDestinationMetricsDto;
import br.db.ecotrack.ecotrack_api.controller.response.DisposalResponseDto;
import br.db.ecotrack.ecotrack_api.controller.response.DisposalResponseMetricsDto;
import br.db.ecotrack.ecotrack_api.domain.entity.Disposal;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.mapper.DisposalMapper;
import br.db.ecotrack.ecotrack_api.repository.DisposalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

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

  @Transactional(readOnly = true)
  public DisposalResponseMetricsDto getTotalItensDisposal() {
    int totalQuantityCurrentMonth = getTotalQuantityDisposales();
    Map<String, Integer> materialAmountSummary = aggregateDisposalByMaterial();
    return new DisposalResponseMetricsDto(totalQuantityCurrentMonth, materialAmountSummary);
  }

  @Transactional(readOnly = true)
  public DisposalResponseDestinationMetricsDto getMostUsedDestinationDisposal() {
    return getMostUsedDestination()
        .map(entry -> new DisposalResponseDestinationMetricsDto(entry.getKey(), entry.getValue()))
        .orElse(new DisposalResponseDestinationMetricsDto("Nenhum destino encontrado", 0));
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

  private List<Disposal> getDisposalsByDateRange() {
    LocalDate endDate = LocalDate.now();
    LocalDate startDate = endDate.minusDays(30);

    User currentUser = currentUserService.getCurrentUserEntity();

    return disposalRepository.findByUserAndDisposalDateBetween(currentUser, startDate, endDate);
  }

  public int getTotalQuantityDisposales() {
    List<Disposal> lastMonthDisposales = getDisposalsByDateRange();

    int totalQuantity = lastMonthDisposales.stream()
        .mapToInt(Disposal::getQuantity)
        .sum();

    return totalQuantity;
  }

  public Map<String, Integer> aggregateDisposalByMaterial() {
    List<Disposal> lastMonthDisposals = getDisposalsByDateRange();

    Map<String, Integer> materialQuantity = lastMonthDisposals.stream()
        .collect(groupingBy(d -> d.getMaterialType().getTypeName(), summingInt(Disposal::getQuantity)));

    return materialQuantity;
  }

  public Map<String, Integer> aggregateDisposalByDestination() {
    List<Disposal> lastMonthDisposals = getDisposalsByDateRange();

    return lastMonthDisposals.stream()
        .collect(groupingBy(d -> d.getDestination().getDescription(), summingInt(Disposal::getQuantity)));
  }

  public Optional<Map.Entry<String, Integer>> getMostUsedDestination() {
    return aggregateDisposalByDestination().entrySet().stream()
        .max(Map.Entry.comparingByValue());
  }

}
