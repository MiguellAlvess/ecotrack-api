package br.db.ecotrack.ecotrack_api.service;

import org.springframework.stereotype.Service;

import br.db.ecotrack.ecotrack_api.controller.dto.disposal.DisposalRequestDto;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.DisposalResponseDto;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.DisposalUpdateDto;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.metrics.DisposalDestinationAmountSummaryDto;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.metrics.DisposalMostDiscardedMaterialDto;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.metrics.DisposalMostFrequentDestinationDto;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.metrics.DisposalRecyclingPercentage;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.metrics.TotalDisposalQuantityDto;
import br.db.ecotrack.ecotrack_api.domain.entity.Disposal;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.domain.enums.DisposalDestination;
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
  public DisposalResponseDto updateDisposal(Long id, DisposalUpdateDto disposalUpdateDto) {
    Disposal disposal = findDisposalByIdAndCurrentUser(id);

    if (disposalUpdateDto.disposalProduct() != null)
      disposal.setDisposalProduct(disposalUpdateDto.disposalProduct());
    if (disposalUpdateDto.quantity() != null)
      disposal.setQuantity(disposalUpdateDto.quantity());
    if (disposalUpdateDto.materialType() != null)
      disposal.setMaterialType(disposalUpdateDto.materialType());
    if (disposalUpdateDto.destination() != null)
      disposal.setDestination(disposalUpdateDto.destination());
    if (disposalUpdateDto.disposalDate() != null)
      disposal.setDisposalDate(disposalUpdateDto.disposalDate());

    return disposalMapper.toDto(disposalRepository.save(disposal));
  }

  @Transactional
  public void deleteDisposalById(Long id) {
    Disposal disposal = findDisposalByIdAndCurrentUser(id);
    disposalRepository.delete(disposal);
  }

  @Transactional(readOnly = true)
  public TotalDisposalQuantityDto getTotalItensDisposal() {
    int totalQuantityCurrentMonth = getTotalQuantityDisposals();
    return new TotalDisposalQuantityDto(totalQuantityCurrentMonth);
  }

  @Transactional(readOnly = true)
  public DisposalMostFrequentDestinationDto getMostUsedDestinationDisposal() {
    return getMostUsedDestination()
        .map(entry -> new DisposalMostFrequentDestinationDto(entry.getKey(), entry.getValue()))
        .orElse(new DisposalMostFrequentDestinationDto("Nenhum destino encontrado", 0));
  }

  @Transactional(readOnly = true)
  public DisposalDestinationAmountSummaryDto getDestinationAmountSummary() {
    Map<String, Integer> destinationAmountSummary = aggregateDisposalByDestination();

    return new DisposalDestinationAmountSummaryDto(destinationAmountSummary);
  }

  public DisposalRecyclingPercentage getRecyclingPercentage() {
    Map<String, Integer> summary = aggregateDisposalByDestination();

    int total = getTotalQuantityDisposals();

    if (total == 0)
      return new DisposalRecyclingPercentage(0.0);

    int recyclable = summary.entrySet().stream()
        .filter(e -> e.getKey().equalsIgnoreCase(
            DisposalDestination.RECYCLING.getDescription()))
        .mapToInt(Map.Entry::getValue)
        .sum();

    double percentage = Math.round(((recyclable * 100.0) / total) * 100.0) / 100.0;

    return new DisposalRecyclingPercentage(percentage);
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

  public int getTotalQuantityDisposals() {
    List<Disposal> lastMonthDisposales = getDisposalsByDateRange();

    int totalQuantity = lastMonthDisposales.stream()
        .mapToInt(Disposal::getQuantity)
        .sum();

    return totalQuantity;
  }

  public DisposalMostDiscardedMaterialDto getMostDiscardedMaterial() {
    List<Disposal> lastMonthDisposals = getDisposalsByDateRange();

    Map<String, Integer> materialQuantity = lastMonthDisposals.stream()
        .collect(groupingBy(d -> d.getMaterialType().getTypeName(), summingInt(Disposal::getQuantity)));

    String mostDiscardedMaterialDto = materialQuantity.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(entry -> entry.getKey())
        .orElse("Sem registros");

    return new DisposalMostDiscardedMaterialDto(mostDiscardedMaterialDto);
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
