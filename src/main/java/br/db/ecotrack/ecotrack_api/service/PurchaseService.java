package br.db.ecotrack.ecotrack_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.db.ecotrack.ecotrack_api.controller.request.PurchaseRequestDto;
import br.db.ecotrack.ecotrack_api.controller.response.PurchaseResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.Material;
import br.db.ecotrack.ecotrack_api.domain.entity.Purchase;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.mapper.PurchaseMapper;
import br.db.ecotrack.ecotrack_api.repository.MaterialRepository;
import br.db.ecotrack.ecotrack_api.repository.PurchaseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Service
public class PurchaseService {

    private final CurrentUserService currentUserService;
    private final PurchaseRepository purchaseRepository;
    private final MaterialRepository materialRepository;
    private final PurchaseMapper purchaseMapper;

    public PurchaseService(PurchaseRepository purchaseRepository, PurchaseMapper purchaseMapper,
            CurrentUserService currentUserService, MaterialRepository materialRepository) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseMapper = purchaseMapper;
        this.currentUserService = currentUserService;
        this.materialRepository = materialRepository;
    }

    @Transactional
    public PurchaseResponseDto createPurchase(@Valid PurchaseRequestDto purchaseRequestDto) {
        User user = currentUserService.getUserEntity();

        Material material = materialRepository.findById(purchaseRequestDto.materialId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Material not found: " + purchaseRequestDto.materialId()));

        Purchase purchase = purchaseMapper.toEntity(purchaseRequestDto);
        purchase.setUser(user);
        purchase.setMaterial(material);

        Purchase purchaseSaved = purchaseRepository.save(purchase);

        return purchaseMapper.toDto(purchaseSaved);
    }

    @Transactional(readOnly = true)
    public PurchaseResponseDto getPurchaseById(Long purchaseId) {
        return purchaseRepository.findById(purchaseId)
                .map(purchase -> purchaseMapper.toDto(purchase))
                .orElseThrow(() -> new EntityNotFoundException("Purchase not found: " + purchaseId));
    }

    @Transactional(readOnly = true)
    public List<PurchaseResponseDto> getAllPurchasesByUser(Long userId) {
        Long currentUserId = currentUserService.getCurrentUserId();

        if (!currentUserId.equals(userId)) {
            throw new SecurityException("Access denied for user: " + currentUserId);
        }

        return purchaseRepository.findAllByUser_UserId(userId)
                .stream()
                .map(purchase -> purchaseMapper.toDto(purchase))
                .toList();
    }

    @Transactional
    public void deletePurchase(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new EntityNotFoundException("Purchase not found: " + purchaseId));
        purchaseRepository.deleteById(purchaseId);
    }
}