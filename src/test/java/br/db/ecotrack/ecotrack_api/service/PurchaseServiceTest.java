package br.db.ecotrack.ecotrack_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
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
    void setup(){
       user = new User();
       user.setUserId(1L);

       purchaseRequestDto = new PurchaseRequestDto(
            "Garrafa",
            5,
            MaterialType.GLASS,
            LocalDate.now()
        );

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
            purchaseSaved.getPurchaseDate()
        );
    }

    @Test
    void shouldCreatePurchaseSucessfully(){
        //mockar
        Mockito.when(currentUserService.getCurrentUserEntity()).thenReturn(user);
        Mockito.when(purchaseMapper.toEntity(purchaseRequestDto)).thenReturn(purchaseEntity);
        Mockito.when(purchaseRepository.save(purchaseEntity)).thenReturn(purchaseSaved);
        Mockito.when(purchaseMapper.toDto(purchaseSaved)).thenReturn(purchaseResponseDto);

        //chamar método do service
        PurchaseResponseDto result = purchaseService.createPurchase(purchaseRequestDto);

        //verifica 
        assertNotNull(result);
        assertEquals(2L, result.purchaseId());
        assertEquals("Garrafa", result.purchaseProduct());

        //garante interações,prâmetros na ordem correta de execução
        Mockito.verify(currentUserService).getCurrentUserEntity();
        Mockito.verify(purchaseMapper).toEntity(purchaseRequestDto);
        Mockito.verify(purchaseRepository).save(purchaseEntity);
        Mockito.verify(purchaseMapper).toDto(purchaseSaved);   
    }

}
