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
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.DisposalRequestDto;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.DisposalResponseDto;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.DisposalUpdateDto;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.metrics.DisposalDestinationAmountSummaryDto;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.metrics.DisposalMostDiscardedMaterialDto;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.metrics.DisposalMostFrequentDestinationDto;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.metrics.DisposalRecyclingPercentage;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.metrics.TotalDisposalQuantityDto;
import br.db.ecotrack.ecotrack_api.domain.enums.DisposalDestination;
import br.db.ecotrack.ecotrack_api.domain.enums.MaterialType;
import br.db.ecotrack.ecotrack_api.service.DisposalService;
import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@AutoConfigureMockMvc
public class DisposalControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private DisposalService disposalService;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ChatClient chatClient;

  @Test
  @WithMockUser
  void getAllDisposals_Should_Return200AndListOfDisposals_WhenExists() throws Exception {
    LocalDate today = LocalDate.now();

    DisposalResponseDto dto1 = new DisposalResponseDto(1L, "Garrafa", 5, MaterialType.PLASTIC,
        DisposalDestination.RECYCLING, today);
    DisposalResponseDto dto2 = new DisposalResponseDto(2L, "Papelão", 3, MaterialType.PAPER,
        DisposalDestination.RECYCLING, today.minusDays(3));

    when(disposalService.getAllDisposalsForCurrentUser()).thenReturn(List.of(dto1, dto2));

    mockMvc.perform(get("/api/disposals"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].disposalId").value(dto1.disposalId()))
        .andExpect(jsonPath("$[0].disposalProduct").value(dto1.disposalProduct()))
        .andExpect(jsonPath("$[0].quantity").value(dto1.quantity()))
        .andExpect(jsonPath("$[0].materialType").value(dto1.materialType().toString()))
        .andExpect(jsonPath("$[0].destination").value(dto1.destination().toString()));

    verify(disposalService, times(1)).getAllDisposalsForCurrentUser();
  }

  @Test
  @WithMockUser
  void getAllDisposals_Should_Return200AndEmptyList_WhenUserHasNoDisposals() throws Exception {
    when(disposalService.getAllDisposalsForCurrentUser()).thenReturn(List.of());

    mockMvc.perform(get("/api/disposals"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(0)));

    verify(disposalService, times(1)).getAllDisposalsForCurrentUser();
  }

  @Test
  @WithMockUser
  void createDisposal_Should_Return201AndDisposalResponseDto_WhenDisposalIsCreated() throws Exception {
    LocalDate today = LocalDate.now();
    DisposalRequestDto requestDto = new DisposalRequestDto("Vidro", 4, MaterialType.GLASS,
        DisposalDestination.RECYCLING, today);
    DisposalResponseDto responseDto = new DisposalResponseDto(1L, "Vidro", 4, MaterialType.GLASS,
        DisposalDestination.RECYCLING, today);

    when(disposalService.createDisposal(any(DisposalRequestDto.class))).thenReturn(responseDto);

    mockMvc.perform(post("/api/disposals")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.disposalId").value(responseDto.disposalId()))
        .andExpect(jsonPath("$.disposalProduct").value(responseDto.disposalProduct()))
        .andExpect(jsonPath("$.quantity").value(responseDto.quantity()))
        .andExpect(jsonPath("$.materialType").value(responseDto.materialType().toString()));

    verify(disposalService, times(1)).createDisposal(any(DisposalRequestDto.class));
  }

  @Test
  @WithMockUser
  void createDisposal_Should_Return400_WhenRequestIsInvalid() throws Exception {
    DisposalRequestDto invalidDto = new DisposalRequestDto("", -1, null, null, null);

    mockMvc.perform(post("/api/disposals")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());

    verify(disposalService, never()).createDisposal(any());
  }

  @Test
  @WithMockUser
  void getDisposalById_Should_Return200AndDisposalResponseDto_WhenExists() throws Exception {
    Long disposalId = 1L;
    LocalDate today = LocalDate.now();
    DisposalResponseDto responseDto = new DisposalResponseDto(1L, "Plástico", 2, MaterialType.PLASTIC,
        DisposalDestination.RECYCLING, today);

    when(disposalService.getDisposalById(disposalId)).thenReturn(responseDto);

    mockMvc.perform(get("/api/disposals/{disposalId}", disposalId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.disposalId").value(responseDto.disposalId()))
        .andExpect(jsonPath("$.disposalProduct").value(responseDto.disposalProduct()))
        .andExpect(jsonPath("$.quantity").value(responseDto.quantity()));

    verify(disposalService, times(1)).getDisposalById(disposalId);
  }

  @Test
  @WithMockUser
  void getDisposalById_Should_Return404_WhenNotFound() throws Exception {
    Long disposalId = 999L;

    when(disposalService.getDisposalById(disposalId))
        .thenThrow(new EntityNotFoundException("Descarte não encontrado com o id: " + disposalId));

    mockMvc.perform(get("/api/disposals/{disposalId}", disposalId))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Descarte não encontrado com o id: " + disposalId));

    verify(disposalService, times(1)).getDisposalById(disposalId);
  }

  @Test
  @WithMockUser
  void updateDisposal_Should_Return200_WhenUpdated() throws Exception {
    Long disposalId = 1L;
    LocalDate today = LocalDate.now();

    DisposalUpdateDto updateDto = new DisposalUpdateDto("Metal", 8, MaterialType.METAL,
        DisposalDestination.RECYCLING, today.minusDays(2));
    DisposalResponseDto responseDto = new DisposalResponseDto(disposalId, "Metal", 8, MaterialType.METAL,
        DisposalDestination.RECYCLING, today);

    when(disposalService.updateDisposal(eq(disposalId), any(DisposalUpdateDto.class))).thenReturn(responseDto);

    mockMvc.perform(patch("/api/disposals/{disposalId}", disposalId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.disposalId").value(responseDto.disposalId()))
        .andExpect(jsonPath("$.disposalProduct").value(responseDto.disposalProduct()))
        .andExpect(jsonPath("$.quantity").value(responseDto.quantity()))
        .andExpect(jsonPath("$.disposalDate").value(responseDto.disposalDate().toString()));

    verify(disposalService, times(1)).updateDisposal(eq(disposalId), any(DisposalUpdateDto.class));
  }

  @Test
  @WithMockUser
  void updateDisposal_Should_Return404_WhenNotFound() throws Exception {
    Long disposalId = 999L;
    LocalDate today = LocalDate.now();
    DisposalUpdateDto updateDto = new DisposalUpdateDto("Vidro", 3, MaterialType.GLASS,
        DisposalDestination.RECYCLING, today.minusDays(2));

    when(disposalService.updateDisposal(eq(disposalId), any(DisposalUpdateDto.class)))
        .thenThrow(new EntityNotFoundException("Descarte não encontrado com o id: " + disposalId));

    mockMvc.perform(patch("/api/disposals/{disposalId}", disposalId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Descarte não encontrado com o id: " + disposalId));

    verify(disposalService, times(1)).updateDisposal(eq(disposalId), any(DisposalUpdateDto.class));
  }

  @Test
  @WithMockUser
  void deleteDisposal_Should_Return204_WhenDeleted() throws Exception {
    Long disposalId = 1L;

    doNothing().when(disposalService).deleteDisposalById(disposalId);

    mockMvc.perform(delete("/api/disposals/{disposalId}", disposalId))
        .andExpect(status().isNoContent());

    verify(disposalService, times(1)).deleteDisposalById(disposalId);
  }

  @Test
  @WithMockUser
  void deleteDisposal_Should_Return404_WhenNotFound() throws Exception {
    Long disposalId = 999L;

    doThrow(new EntityNotFoundException("Descarte não encontrado com o id: " + disposalId))
        .when(disposalService).deleteDisposalById(disposalId);

    mockMvc.perform(delete("/api/disposals/{disposalId}", disposalId))
        .andExpect(status().isNotFound());

    verify(disposalService, times(1)).deleteDisposalById(disposalId);
  }

  @Test
  @WithMockUser
  void getTotalItensDisposal_Should_Return200_WhenSuccessful() throws Exception {
    TotalDisposalQuantityDto dto = new TotalDisposalQuantityDto(12);

    when(disposalService.getTotalItensDisposal()).thenReturn(dto);

    mockMvc.perform(get("/api/disposals/total-itens-disposed-30-days"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalDisposalsCurrentMonth").value(dto.totalDisposalsCurrentMonth()));

    verify(disposalService, times(1)).getTotalItensDisposal();
  }

  @Test
  @WithMockUser
  void getMostDiscardedMaterial_Should_Return200_WhenSuccessful() throws Exception {
    DisposalMostDiscardedMaterialDto dto = new DisposalMostDiscardedMaterialDto("Plástico");

    when(disposalService.getMostDiscardedMaterial()).thenReturn(dto);

    mockMvc.perform(get("/api/disposals/disposals-most-discarded-material"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.mostDiscardedMaterial").value("Plástico"));

    verify(disposalService, times(1)).getMostDiscardedMaterial();
  }

  @Test
  @WithMockUser
  void getDestinationSummary_Should_Return200_WhenSuccessful() throws Exception {
    DisposalDestinationAmountSummaryDto dto = new DisposalDestinationAmountSummaryDto(
        Map.of("Reciclável", 15));

    when(disposalService.getDestinationAmountSummary()).thenReturn(dto);

    mockMvc.perform(get("/api/disposals/disposals-destination-summary-30-days"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.destinationAmountSummary.Reciclável").value(15));

    verify(disposalService, times(1)).getDestinationAmountSummary();
  }

  @Test
  @WithMockUser
  void getMostUsedDestination_Should_Return200_WhenSuccessful() throws Exception {
    DisposalMostFrequentDestinationDto dto = new DisposalMostFrequentDestinationDto("Reciclável", 10);

    when(disposalService.getMostUsedDestinationDisposal()).thenReturn(dto);

    mockMvc.perform(get("/api/disposals/disposal-most-frequent-destination"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.destination").value(dto.destination()))
        .andExpect(jsonPath("$.quantity").value(dto.quantity()));

    verify(disposalService, times(1)).getMostUsedDestinationDisposal();
  }

  @Test
  @WithMockUser
  void getRecyclingPercentage_Should_Return200_WhenSuccessful() throws Exception {
    DisposalRecyclingPercentage dto = new DisposalRecyclingPercentage(75.0);

    when(disposalService.getRecyclingPercentage()).thenReturn(dto);

    mockMvc.perform(get("/api/disposals/percentage-disposals-items-30-days"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.percentageDisposalRecycled").value(dto.percentageDisposalRecycled()));

    verify(disposalService, times(1)).getRecyclingPercentage();
  }
}
