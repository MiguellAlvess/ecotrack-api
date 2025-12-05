package br.db.ecotrack.ecotrack_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.db.ecotrack.ecotrack_api.controller.request.PurchaseRequestDto;
import br.db.ecotrack.ecotrack_api.controller.response.PurchaseResponseDto;
import br.db.ecotrack.ecotrack_api.controller.response.PurchaseResponseMetricsDto;
import br.db.ecotrack.ecotrack_api.domain.entity.Purchase;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.mapper.PurchaseMapper;
import br.db.ecotrack.ecotrack_api.repository.PurchaseRepository;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;

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
    public void deletePurchase(Long purchaseId) {
        Purchase purchase = findPurchaseByIdAndCurrentUser(purchaseId);
        purchaseRepository.delete(purchase);
    }

    @Transactional(readOnly = true)
    public PurchaseResponseMetricsDto getTotalItensPurchased(){
        int totalQuantity =  getTotalQuantityPurchases();
        return new PurchaseResponseMetricsDto(totalQuantity);
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

    private List<Purchase> getPurchasesByDateRange(){
        LocalDate endDate = LocalDate.now();
        LocalDate startDate  = endDate.minusDays(30);

        User currentUser = currentUserService.getCurrentUserEntity();

        return purchaseRepository.findByUserAndPurchaseDateBetween(currentUser, startDate, endDate);
    }

    public int getTotalQuantityPurchases(){
      List <Purchase> purchasesWithinRange = getPurchasesByDateRange();
      
      int totalQuantity = purchasesWithinRange.stream().map(Purchase::getQuantity)
      .reduce(0, Integer::sum); 
        
      return totalQuantity;
    }

}
