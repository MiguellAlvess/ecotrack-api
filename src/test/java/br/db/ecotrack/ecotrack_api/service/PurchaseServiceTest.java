package br.db.ecotrack.ecotrack_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.db.ecotrack.ecotrack_api.controller.request.PurchaseRequestDto;
import br.db.ecotrack.ecotrack_api.controller.response.PurchaseResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.Purchase;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.domain.enums.MaterialType;
import br.db.ecotrack.ecotrack_api.mapper.PurchaseMapper;
import br.db.ecotrack.ecotrack_api.repository.PurchaseRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTest {

    @InjectMocks
    private PurchaseService purchaseService;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private PurchaseMapper purchaseMapper;

    @Mock
    private CurrentUserService currentUserService;

    private User user;
    private PurchaseRequestDto purchaseRequestDto;
    private Purchase purchaseEntity;
    private Purchase purchaseSaved;
    private PurchaseResponseDto purchaseResponseDto;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUserId(1L);

        purchaseRequestDto = new PurchaseRequestDto(
                "Garrafa",
                5,
                MaterialType.GLASS,
                LocalDate.now());

        purchaseEntity = new Purchase();
        purchaseEntity.setPurchaseId(null);
        purchaseEntity.setPurchaseProduct("Garrafa");
        purchaseEntity.setQuantity(5);
        purchaseEntity.setMaterialType(MaterialType.GLASS);
        purchaseEntity.setPurchaseDate(LocalDate.now());

        purchaseSaved = new Purchase();
        purchaseSaved.setPurchaseId(2L);
        purchaseSaved.setPurchaseProduct("Garrafa");
        purchaseSaved.setQuantity(5);
        purchaseSaved.setMaterialType(MaterialType.GLASS);
        purchaseSaved.setPurchaseDate(LocalDate.now());
        purchaseSaved.setUser(user);

        purchaseResponseDto = new PurchaseResponseDto(
                2L,
                "Garrafa",
                5,
                MaterialType.GLASS,
                purchaseSaved.getPurchaseDate());
    }

    @Test
    void create_shouldCreatePurchaseSucessfully() {
        // mockar
        when(currentUserService.getCurrentUserEntity()).thenReturn(user);
        when(purchaseMapper.toEntity(purchaseRequestDto)).thenReturn(purchaseEntity);
        when(purchaseRepository.save(purchaseEntity)).thenReturn(purchaseSaved);
        when(purchaseMapper.toDto(purchaseSaved)).thenReturn(purchaseResponseDto);

        // chamar método do service
        PurchaseResponseDto result = purchaseService.createPurchase(purchaseRequestDto);

        // verifica
        assertNotNull(result);
        assertEquals(2L, result.purchaseId());
        assertEquals("Garrafa", result.purchaseProduct());

        // garante interações,prâmetros na ordem correta de execução
        verify(currentUserService).getCurrentUserEntity();
        verify(purchaseMapper).toEntity(purchaseRequestDto);
        verify(purchaseRepository).save(purchaseEntity);
        verify(purchaseMapper).toDto(purchaseSaved);
    }

    @Test
    void getById_shouldReturnPurchaseDto_WhenPurchaseExists() {
        when(currentUserService.getCurrentUserEntity()).thenReturn(user);
        when(purchaseRepository.findById(2L)).thenReturn(Optional.of(purchaseSaved));
        when(purchaseMapper.toDto(purchaseSaved)).thenReturn(purchaseResponseDto);

        PurchaseResponseDto result = purchaseService.getPurchaseById(2L);

        assertNotNull(result);
        assertEquals(purchaseResponseDto.purchaseId(), result.purchaseId());

        verify(currentUserService).getCurrentUserEntity();
        verify(purchaseRepository).findById(2L);
        verify(purchaseMapper).toDto(purchaseSaved);
    }

    @Test
    void getById_shouldThrowException_WhenPurchaseDoesNotExists() {
        when(currentUserService.getCurrentUserEntity()).thenReturn(user);
        when(purchaseRepository.findById(50L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> purchaseService.getPurchaseById(50L));

        assertEquals("Compra não encontrada com o id: 50", exception.getMessage());

        verify(currentUserService).getCurrentUserEntity();
        verify(purchaseRepository).findById(50L);
        verifyNoMoreInteractions(purchaseMapper);
    }

    @Test
    void getById_shouldThrowException_WhenPurchaseBelongToAnotherUser(){
        User anotherUser = new User();
        anotherUser.setUserId(3L);

        Purchase purchaseFromAnotherUser = new Purchase();
        purchaseFromAnotherUser.setPurchaseId(3L);
        purchaseFromAnotherUser.setUser(anotherUser);
        
        when(currentUserService.getCurrentUserEntity()).thenReturn(user);
        when(purchaseRepository.findById(3L)).thenReturn(Optional.of(purchaseFromAnotherUser));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> purchaseService.getPurchaseById(3L));

        assertEquals("Compra não encontrada", exception.getMessage());

        verify(currentUserService).getCurrentUserEntity();
        verify(purchaseRepository).findById(3L);
        verifyNoMoreInteractions(purchaseMapper);
    }

    @Test
    void getAll_shouldReturnListOfPurchaseResponseDto_WhenUserHasPurchases(){
        List<Purchase> purchases = List.of(purchaseSaved);

        when(currentUserService.getCurrentUserEntity()).thenReturn(user);
        when(purchaseRepository.findByUser(user)).thenReturn(purchases);
        when(purchaseMapper.toDto(purchaseSaved)).thenReturn(purchaseResponseDto);

        List<PurchaseResponseDto> result = purchaseService.getAllPurchasesForCurrentUser();

        assertEquals(1, result.size());
        assertEquals(purchaseResponseDto, result.get(0));

        verify(currentUserService).getCurrentUserEntity();
        verify(purchaseRepository).findByUser(user);
        verify(purchaseMapper).toDto(purchaseSaved);
    }

    @Test
    void getAll_shouldReturnEmptyListOfPurchaseResponseDto_WhenUserHasNotPurchases(){
        when(currentUserService.getCurrentUserEntity()).thenReturn(user);
        when(purchaseRepository.findByUser(user)).thenReturn(List.of());

        List<PurchaseResponseDto> result = purchaseService.getAllPurchasesForCurrentUser();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(currentUserService).getCurrentUserEntity();
        verify(purchaseRepository).findByUser(user);
        verifyNoInteractions(purchaseMapper);
    }
}