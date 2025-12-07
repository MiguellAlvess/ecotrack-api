package br.db.ecotrack.ecotrack_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.db.ecotrack.ecotrack_api.controller.dto.purchase.PurchaseRequestDto;
import br.db.ecotrack.ecotrack_api.controller.dto.purchase.PurchaseResponseDto;
import br.db.ecotrack.ecotrack_api.controller.dto.purchase.PurchaseUpdateDto;
import br.db.ecotrack.ecotrack_api.controller.dto.purchase.metrics.PurchaseResponseMetricsDto;
import br.db.ecotrack.ecotrack_api.domain.entity.Purchase;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.mapper.PurchaseMapper;
import br.db.ecotrack.ecotrack_api.repository.PurchaseRepository;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

@Service
public class PurchaseService {

  private final CurrentUserService currentUserService;
  private final PurchaseRepository purchaseRepository;
  private final PurchaseMapper purchaseMapper;

  public PurchaseService(PurchaseRepository purchaseRepository, PurchaseMapper purchaseMapper,
      CurrentUserService currentUserService) {
    this.purchaseRepository = purchaseRepository;
    this.purchaseMapper = purchaseMapper;
    this.currentUserService = currentUserService;
  }

  @Transactional
  public PurchaseResponseDto createPurchase(PurchaseRequestDto purchaseRequestDto) {
    User user = currentUserService.getCurrentUserEntity();

    Purchase purchase = purchaseMapper.toEntity(purchaseRequestDto);

    purchase.setUser(user);
    Purchase purchaseSaved = purchaseRepository.save(purchase);

    return purchaseMapper.toDto(purchaseSaved);
  }

  @Transactional(readOnly = true)
  public PurchaseResponseDto getPurchaseById(Long purchaseId) {
    Purchase purchase = findPurchaseByIdAndCurrentUser(purchaseId);
    return purchaseMapper.toDto(purchase);
  }

  @Transactional(readOnly = true)
  public List<PurchaseResponseDto> getAllPurchasesForCurrentUser() {
    User currentUser = currentUserService.getCurrentUserEntity();
    List<Purchase> purchases = purchaseRepository.findByUser(currentUser);

    return purchases.stream()
        .map(purchase -> purchaseMapper.toDto(purchase))
        .toList();
  }

  @Transactional
  public PurchaseResponseDto updatePurchase(Long id, PurchaseUpdateDto purchaseUpdateDto) {
    Purchase purchase = findPurchaseByIdAndCurrentUser(id);

    if (purchaseUpdateDto.purchaseProduct() != null)
      purchase.setPurchaseProduct(purchaseUpdateDto.purchaseProduct());
    if (purchaseUpdateDto.quantity() != null)
      purchase.setQuantity(purchaseUpdateDto.quantity());
    if (purchaseUpdateDto.materialType() != null)
      purchase.setMaterialType(purchaseUpdateDto.materialType());
    if (purchaseUpdateDto.purchaseDate() != null)
      purchase.setPurchaseDate(purchaseUpdateDto.purchaseDate());

    return purchaseMapper.toDto(purchaseRepository.save(purchase));
  }

  @Transactional
  public void deletePurchase(Long purchaseId) {
    Purchase purchase = findPurchaseByIdAndCurrentUser(purchaseId);
    purchaseRepository.delete(purchase);
  }

  @Transactional(readOnly = true)
  public PurchaseResponseMetricsDto getTotalItensPurchased() {
    int totalQuantityCurrentMonth = getTotalQuantityPurchases();
    Map<String, Integer> materialAmountSummary = aggregatePurchaseByMaterial();
    return new PurchaseResponseMetricsDto(totalQuantityCurrentMonth, materialAmountSummary);
  }

  private Purchase findPurchaseByIdAndCurrentUser(Long id) {
    User currentUser = currentUserService.getCurrentUserEntity();
    Purchase purchase = purchaseRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Compra não encontrada com o id: " + id));

    if (!purchase.getUser().getUserId().equals(currentUser.getUserId())) {
      throw new EntityNotFoundException("Compra não encontrada com o id: " + id);
    }
    return purchase;
  }

  private List<Purchase> getPurchasesByDateRange() {
    LocalDate endDate = LocalDate.now();
    LocalDate startDate = endDate.minusDays(30);

    User currentUser = currentUserService.getCurrentUserEntity();

    return purchaseRepository.findByUserAndPurchaseDateBetween(currentUser, startDate, endDate);
  }

  public int getTotalQuantityPurchases() {
    List<Purchase> lastMonthPurchases = getPurchasesByDateRange();

    int totalQuantity = lastMonthPurchases.stream()
        .mapToInt(Purchase::getQuantity)
        .sum();

    return totalQuantity;
  }

  public Map<String, Integer> aggregatePurchaseByMaterial() {
    List<Purchase> lastMonthPurchases = getPurchasesByDateRange();

    Map<String, Integer> materialQuantity = lastMonthPurchases.stream()
        .collect(groupingBy(p -> p.getMaterialType().getTypeName(), summingInt(Purchase::getQuantity)));

    return materialQuantity;
  }
}
