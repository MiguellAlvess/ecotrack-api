package br.db.ecotrack.ecotrack_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.db.ecotrack.ecotrack_api.controller.dto.purchase.PurchaseRequestDto;
import br.db.ecotrack.ecotrack_api.controller.dto.purchase.PurchaseResponseDto;
import br.db.ecotrack.ecotrack_api.controller.dto.purchase.PurchaseUpdateDto;
import br.db.ecotrack.ecotrack_api.controller.dto.purchase.metrics.PurchaseMaterialAmountSummaryDto;
import br.db.ecotrack.ecotrack_api.controller.dto.purchase.metrics.TotalPurchaseQuantityDto;
import br.db.ecotrack.ecotrack_api.domain.enums.MaterialType;
import br.db.ecotrack.ecotrack_api.service.PurchaseService;
import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PurchaseService purchaseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAllPurchases_Should_Return200AndListOfPurchases_WhenExists() throws Exception {
        LocalDate today = LocalDate.now();

        PurchaseResponseDto dto1 = new PurchaseResponseDto(1L, "Garrafa", 5, MaterialType.ORGANIC, today);
        PurchaseResponseDto dto2 = new PurchaseResponseDto(1L, "Taça", 5, MaterialType.GLASS, today.minusDays(5));
        List<PurchaseResponseDto> purchaseList = List.of(dto1, dto2);
        when(purchaseService.getAllPurchasesForCurrentUser()).thenReturn(purchaseList);

        mockMvc.perform(get("/api/purchases"))
                .andExpect(status().isOk())
                .andExpect(content().contentType((MediaType.APPLICATION_JSON_VALUE)))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].purchaseProduct").value(dto1.purchaseProduct()))
                .andExpect(jsonPath("$[0].quantity").value(dto1.quantity()))
                .andExpect(jsonPath("$[0].materialType").value(dto1.materialType().toString()))
                .andExpect(jsonPath("$[1].purchaseProduct").value(dto2.purchaseProduct()))
                .andExpect(jsonPath("$[1].quantity").value(dto2.quantity()))
                .andExpect(jsonPath("$[1].materialType").value(dto2.materialType().toString()));

        verify(purchaseService, times(1)).getAllPurchasesForCurrentUser();
    }

    @Test
    @WithMockUser
    void getAllPurchases_Should_Return200AndEmptyList_WhenUserHasNoPurchases() throws Exception {
        List<PurchaseResponseDto> purchaseEmptyList = List.of();
        when(purchaseService.getAllPurchasesForCurrentUser()).thenReturn(purchaseEmptyList);

        mockMvc.perform(get("/api/purchases"))
                .andExpect(status().isOk())
                .andExpect(content().contentType((MediaType.APPLICATION_JSON_VALUE)))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(purchaseService, times(1)).getAllPurchasesForCurrentUser();
    }

    @Test
    @WithMockUser
    void createPurchase_Should_Return201AndPurchaseResponseDto_WhenPurchaseIsCreated() throws Exception {
        LocalDate today = LocalDate.now();
        PurchaseRequestDto requestDto = new PurchaseRequestDto("Taça", 5, MaterialType.GLASS, today);
        PurchaseResponseDto responseDto = new PurchaseResponseDto(1L, "Taça", 5, MaterialType.GLASS, today);

        when(purchaseService.createPurchase(any(PurchaseRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/purchases").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType((MediaType.APPLICATION_JSON_VALUE)))
                .andExpect(jsonPath("$.purchaseId").value(responseDto.purchaseId()))
                .andExpect(jsonPath("$.purchaseProduct").value(responseDto.purchaseProduct()))
                .andExpect(jsonPath("$.quantity").value(responseDto.quantity()))
                .andExpect(jsonPath("$.materialType").value(responseDto.materialType().toString()));

        verify(purchaseService, times(1)).createPurchase(any(PurchaseRequestDto.class));
    }

    @Test
    @WithMockUser
    void createPurchase_Should_Return400_WhenPurchaseRequestIsInvalid() throws Exception {
        PurchaseRequestDto invalidRequestDto = new PurchaseRequestDto("", -5, null, null);

        mockMvc.perform(post("/api/purchases").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest());

        verify(purchaseService, never()).createPurchase(any());
    }

    @Test
    @WithMockUser
    void getPurchaseById_Should_Return200AndPurchaseResponseDto_WhenPurchaseExists() throws Exception {
        Long purchaseId = 1L;
        LocalDate today = LocalDate.now();
        PurchaseRequestDto requestDto = new PurchaseRequestDto("Taça", 5, MaterialType.GLASS, today);
        PurchaseResponseDto responseDto = new PurchaseResponseDto(1L, "Taça", 5, MaterialType.GLASS, today);

        when(purchaseService.getPurchaseById(purchaseId)).thenReturn(responseDto);

        mockMvc.perform(get("/api/purchases/{purchaseId}", purchaseId))
                .andExpect(status().isOk())
                .andExpect(content().contentType((MediaType.APPLICATION_JSON_VALUE)))
                .andExpect(jsonPath("$.purchaseId").value(responseDto.purchaseId()))
                .andExpect(jsonPath("$.purchaseProduct").value(responseDto.purchaseProduct()))
                .andExpect(jsonPath("$.quantity").value(responseDto.quantity()))
                .andExpect(jsonPath("$.materialType").value(responseDto.materialType().toString()));
    }

    @Test
    @WithMockUser
    void getPurchaseById_Should_Return404_WhenPurchaseDoesNotExist() throws Exception {
        Long purchaseId = 999L;

        when(purchaseService.getPurchaseById(purchaseId))
                .thenThrow(new EntityNotFoundException("Compra não encontrada com o id: " + purchaseId));

        mockMvc.perform(get("/api/purchases/{purchaseId}", purchaseId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Compra não encontrada com o id: " + purchaseId));

        verify(purchaseService, times(1)).getPurchaseById(purchaseId);
    }

    @Test
    @WithMockUser
    void updatePurchase_Should_Return200AndUpdatedPurchase_WhenPurchaseExists() throws Exception {
        Long purchaseId = 1L;
        LocalDate today = LocalDate.now();
        PurchaseUpdateDto updateDto = new PurchaseUpdateDto("Xícara", 5, MaterialType.GLASS, today.minusDays(5));
        PurchaseResponseDto responseDto = new PurchaseResponseDto(purchaseId, "Xícara", 5, MaterialType.GLASS, today);

        when(purchaseService.updatePurchase(eq(purchaseId), any(PurchaseUpdateDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/api/purchases/{purchaseId}", purchaseId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType((MediaType.APPLICATION_JSON_VALUE)))
                .andExpect(jsonPath("$.purchaseId").value(responseDto.purchaseId()))
                .andExpect(jsonPath("$.purchaseProduct").value(responseDto.purchaseProduct()))
                .andExpect(jsonPath("$.quantity").value(responseDto.quantity()))
                .andExpect(jsonPath("$.materialType").value(responseDto.materialType().toString()))
                .andExpect(jsonPath("$.purchaseDate").value(responseDto.purchaseDate().toString()));

        verify(purchaseService, times(1)).updatePurchase(eq(purchaseId), any(PurchaseUpdateDto.class));
    }

    @Test
    @WithMockUser
    void updatePurchase_Should_Return404_WhenPurchaseDoesNotExist() throws Exception {
        Long purchaseId = 999L;
        LocalDate today = LocalDate.now();
        PurchaseUpdateDto updateDto = new PurchaseUpdateDto("Xícara", 5, MaterialType.GLASS, today.minusDays(5));

        when(purchaseService.updatePurchase(eq(purchaseId), any(PurchaseUpdateDto.class)))
                .thenThrow(new EntityNotFoundException("Compra não encontrada com o id: " + purchaseId));

        mockMvc.perform(patch("/api/purchases/{purchaseId}", purchaseId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Compra não encontrada com o id: " + purchaseId));

        verify(purchaseService, times(1)).updatePurchase(eq(purchaseId), any(PurchaseUpdateDto.class));
    }

    @Test
    @WithMockUser
    void deletePurchase_Should_Return204_WhenPurchaseIsDeleted() throws Exception{
        Long purchaseId= 1L;

        doNothing().when(purchaseService).deletePurchase(eq(purchaseId));

        mockMvc.perform(delete("/api/purchases/{purchaseId}", purchaseId))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));

        verify(purchaseService, times(1)).deletePurchase(eq(purchaseId));
    }

    @Test
    @WithMockUser
    void deletePurchase_ShouldReturn404_WhenPurchaseDoesNotExists() throws Exception{
        Long purchaseId = 999L;

        doThrow(new EntityNotFoundException("Compra não encontrada com o id: " + purchaseId)).when(purchaseService).deletePurchase(eq(purchaseId));

        mockMvc.perform(delete("/api/purchases/{purchaseId}", purchaseId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Compra não encontrada com o id: " + purchaseId));

        verify(purchaseService, times(1)).deletePurchase(eq(purchaseId));
    }

    @Test
    @WithMockUser
    void getTotalItensPurchased_Should_Return200AndTotalQuantity_WhenSucessful() throws Exception{
        TotalPurchaseQuantityDto totalQuantityDto = new TotalPurchaseQuantityDto(50);

        when(purchaseService.getTotalItensPurchased()).thenReturn(totalQuantityDto);

        mockMvc.perform(get("/api/purchases/total-itens-purchased-30-days"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalPurchasesCurrentMonth").value(totalQuantityDto.totalPurchasesCurrentMonth()));

        verify(purchaseService, times(1)).getTotalItensPurchased();
    }

    @Test
    @WithMockUser
    void getPurchasesMaterialSummary_Should_Return200AndPurchasesMaterialSummary_WhenSucessful() throws Exception{
        PurchaseMaterialAmountSummaryDto materialAmountSummaryDto = new PurchaseMaterialAmountSummaryDto(Map.of("Plástico", 20));

        when(purchaseService.getMaterialAmountSummaryDto()).thenReturn(materialAmountSummaryDto);

        mockMvc.perform(get("/api/purchases/purchases-material-summary-30-days"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.materialAmountSummary.Plástico").value(20));

        verify(purchaseService, times(1)).getMaterialAmountSummaryDto();
    }
}