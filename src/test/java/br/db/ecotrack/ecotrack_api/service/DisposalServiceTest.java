package br.db.ecotrack.ecotrack_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.DisposalRequestDto;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.DisposalResponseDto;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.metrics.DisposalDestinationAmountSummaryDto;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.metrics.DisposalMostDiscardedMaterialDto;
import br.db.ecotrack.ecotrack_api.controller.dto.disposal.metrics.DisposalMostFrequentDestinationDto;
import br.db.ecotrack.ecotrack_api.domain.entity.Disposal;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.domain.enums.DisposalDestination;
import br.db.ecotrack.ecotrack_api.domain.enums.MaterialType;
import br.db.ecotrack.ecotrack_api.mapper.DisposalMapper;
import br.db.ecotrack.ecotrack_api.repository.DisposalRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class DisposalServiceTest {

  @InjectMocks
  private DisposalService disposalService;

  @Mock
  private DisposalRepository disposalRepository;

  @Mock
  private DisposalMapper disposalMapper;

  @Mock
  private CurrentUserService currentUserService;

  private User user;
  private Disposal disposalEntity;
  private Disposal disposalSaved;
  private DisposalRequestDto disposalRequestDto;
  private DisposalResponseDto disposalResponseDto;
  @SuppressWarnings("unused")
  private DisposalDestinationAmountSummaryDto destinationAmountSummaryDto;
  @SuppressWarnings("unused")
  private DisposalMostDiscardedMaterialDto disposalMostDiscardedMaterialDto;
  @SuppressWarnings("unused")
  private DisposalMostFrequentDestinationDto disposalMostFrequentDestinationDto;

  @BeforeEach
  void setup() {
    user = new User();
    user.setUserId(1L);

    disposalRequestDto = new DisposalRequestDto("Garrafa", 5, MaterialType.PLASTIC, DisposalDestination.RECYCLING,
        LocalDate.now());

    disposalEntity = new Disposal();
    disposalEntity.setDisposalId(null);
    disposalEntity.setDisposalProduct("Garrafa");
    disposalEntity.setQuantity(5);
    disposalEntity.setMaterialType(MaterialType.PAPER);
    disposalEntity.setDestination(DisposalDestination.RECYCLING);
    disposalEntity.setDisposalDate(LocalDate.now());

    disposalSaved = new Disposal();
    disposalSaved.setDisposalId(2L);
    disposalSaved.setDisposalProduct("Garrafa");
    disposalSaved.setQuantity(5);
    disposalSaved.setMaterialType(MaterialType.PAPER);
    disposalSaved.setDestination(DisposalDestination.RECYCLING);
    disposalSaved.setDisposalDate(LocalDate.now());
    disposalSaved.setUser(user);

    disposalResponseDto = new DisposalResponseDto(
        2L,
        "Garrafa",
        5,
        MaterialType.PLASTIC,
        DisposalDestination.RECYCLING,
        disposalSaved.getDisposalDate());

    destinationAmountSummaryDto = new DisposalDestinationAmountSummaryDto(Map.of("Reciclagem", 10));

    disposalMostDiscardedMaterialDto = new DisposalMostDiscardedMaterialDto("Plástico");

    disposalMostFrequentDestinationDto = new DisposalMostFrequentDestinationDto("Reciclagem", 8);
  }

  @Test
  void create_shouldCreateDisposalSucessfully() {
    when(currentUserService.getCurrentUserEntity()).thenReturn(user);
    when(disposalMapper.toEntity(disposalRequestDto)).thenReturn(disposalEntity);
    when(disposalRepository.save(disposalEntity)).thenReturn(disposalSaved);
    when(disposalMapper.toDto(disposalSaved)).thenReturn(disposalResponseDto);

    DisposalResponseDto result = disposalService.createDisposal(disposalRequestDto);

    assertNotNull(result);
    assertEquals(2L, result.disposalId());
    assertEquals("Garrafa", result.disposalProduct());
    assertEquals(user, disposalEntity.getUser());

    verify(currentUserService).getCurrentUserEntity();
    verify(disposalMapper).toEntity(disposalRequestDto);
    verify(disposalRepository).save(disposalEntity);
    verify(disposalMapper).toDto(disposalSaved);
  }

  @Test
  void getById_shouldReturnDisposalDto_WhenDisposalExists() {
    when(currentUserService.getCurrentUserEntity()).thenReturn(user);
    when(disposalRepository.findById(2L)).thenReturn(Optional.of(disposalSaved));
    when(disposalMapper.toDto(disposalSaved)).thenReturn(disposalResponseDto);

    DisposalResponseDto result = disposalService.getDisposalById(2L);

    assertNotNull(result);
    assertEquals(disposalResponseDto, result);

    verify(currentUserService).getCurrentUserEntity();
    verify(disposalRepository).findById(2L);
    verify(disposalMapper).toDto(disposalSaved);
  }

  @Test
  void getById_shouldThrowException_WhenDisposalDoesNotExists() {
    when(currentUserService.getCurrentUserEntity()).thenReturn(user);
    when(disposalRepository.findById(50L)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
        () -> disposalService.getDisposalById(50L));

    assertEquals("Descarte não encontrado com o id: 50", exception.getMessage());

    verify(currentUserService).getCurrentUserEntity();
    verify(disposalRepository).findById(50L);
    verifyNoInteractions(disposalMapper);
  }

  @Test
  void getById_shouldThrowException_WhenDisposalBelongToAnotherUser() {
    User anotherUser = new User();
    anotherUser.setUserId(3L);

    Disposal disposalFromAnotherUser = new Disposal();
    disposalFromAnotherUser.setDisposalId(3L);
    disposalFromAnotherUser.setUser(anotherUser);

    when(currentUserService.getCurrentUserEntity()).thenReturn(user);
    when(disposalRepository.findById(3L)).thenReturn(Optional.of(disposalFromAnotherUser));

    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
        () -> disposalService.getDisposalById(3L));

    assertEquals("Descarte não encontrado com o id: 3", exception.getMessage());

    verify(currentUserService).getCurrentUserEntity();
    verify(disposalRepository).findById(3L);
    verifyNoInteractions(disposalMapper);
  }

  @Test
  void getAll_shouldReturnListOfDisposalResponseDto_WhenUserHasDisposals() {
    List<Disposal> disposals = List.of(disposalSaved);

    when(currentUserService.getCurrentUserEntity()).thenReturn(user);
    when(disposalRepository.findByUser(user)).thenReturn(disposals);
    when(disposalMapper.toDto(disposalSaved)).thenReturn(disposalResponseDto);

    List<DisposalResponseDto> result = disposalService.getAllDisposalsForCurrentUser();

    assertEquals(1, result.size());
    assertEquals(disposalResponseDto, result.get(0));

    verify(currentUserService).getCurrentUserEntity();
    verify(disposalRepository).findByUser(user);
    verify(disposalMapper).toDto(disposalSaved);
  }

  @Test
  void getAll_shouldReturnEmptyListOfDisposalResponseDto_WhenUserHasNotDisposal() {
    when(currentUserService.getCurrentUserEntity()).thenReturn(user);
    when(disposalRepository.findByUser(user)).thenReturn(List.of());

    List<DisposalResponseDto> result = disposalService.getAllDisposalsForCurrentUser();

    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(currentUserService).getCurrentUserEntity();
    verify(disposalRepository).findByUser(user);
    verifyNoInteractions(disposalMapper);
  }

  @Test
  void deleteById_shouldDeleteDisposal_WhenDisposalExistsAndBelongToUser() {
    when(currentUserService.getCurrentUserEntity()).thenReturn(user);
    when(disposalRepository.findById(2L)).thenReturn(Optional.of(disposalSaved));

    disposalService.deleteDisposalById(2L);

    verify(currentUserService).getCurrentUserEntity();
    verify(disposalRepository).findById(2L);
    verify(disposalRepository).delete(disposalSaved);
    verifyNoInteractions(disposalMapper);
  }

  @Test
  void deleteById_shouldThrowException_WhenDisposalDoesNotExist() {
    when(currentUserService.getCurrentUserEntity()).thenReturn(user);
    when(disposalRepository.findById(50L)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
        () -> disposalService.deleteDisposalById(50L));

    assertEquals("Descarte não encontrado com o id: 50", exception.getMessage());

    verify(currentUserService).getCurrentUserEntity();
    verify(disposalRepository).findById(50L);
    verifyNoMoreInteractions(disposalRepository);
  }

  @Test
  void deleteById_shouldThrowException_WhenDisposalBelongToAnotherUser() {
    User anotherUser = new User();
    anotherUser.setUserId(3L);

    Disposal disposalFromAnotherUser = new Disposal();
    disposalFromAnotherUser.setDisposalId(3L);
    disposalFromAnotherUser.setUser(anotherUser);

    when(currentUserService.getCurrentUserEntity()).thenReturn(user);
    when(disposalRepository.findById(3L)).thenReturn(Optional.of(disposalFromAnotherUser));

    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
        () -> disposalService.deleteDisposalById(3L));

    assertEquals("Descarte não encontrado com o id: 3", exception.getMessage());

    verify(currentUserService).getCurrentUserEntity();
    verify(disposalRepository).findById(3L);
    verifyNoMoreInteractions(disposalRepository);
  }

  @Test
  void getTotalItensDisposal_shouldReturnCorrectQuantity() {
    when(currentUserService.getCurrentUserEntity()).thenReturn(user);
    when(disposalRepository.findByUserAndDisposalDateBetween(eq(user), any(), any()))
        .thenReturn(List.of(disposalSaved));

    var result = disposalService.getTotalItensDisposal();

    assertNotNull(result);
    assertEquals(5, result.totalDisposalsCurrentMonth());

    verify(currentUserService).getCurrentUserEntity();
  }

  @Test
  void getMostDiscardedMaterial_shouldReturnCorrectMaterial() {
    Disposal d1 = new Disposal(1L, "Papel", 3, MaterialType.PAPER, DisposalDestination.RECYCLING, LocalDate.now(),
        user);
    Disposal d2 = new Disposal(2L, "Copo", 7, MaterialType.GLASS, DisposalDestination.DONATION, LocalDate.now(),
        user);

    when(currentUserService.getCurrentUserEntity()).thenReturn(user);
    when(disposalRepository.findByUserAndDisposalDateBetween(eq(user), any(), any()))
        .thenReturn(List.of(d1, d2));

    DisposalMostDiscardedMaterialDto result = disposalService.getMostDiscardedMaterial();

    assertNotNull(result);
    assertEquals("Vidro", result.mostDiscardedMaterial());

    verify(currentUserService).getCurrentUserEntity();
  }

  @Test
  void aggregateDisposalByDestination_shouldReturnCorrectValue() {
    Disposal d1 = new Disposal(1L, "Vidro", 4, MaterialType.GLASS, DisposalDestination.RECYCLING, LocalDate.now(),
        user);
    Disposal d2 = new Disposal(2L, "Plástico", 6, MaterialType.PLASTIC, DisposalDestination.RECYCLING, LocalDate.now(),
        user);

    when(currentUserService.getCurrentUserEntity()).thenReturn(user);
    when(disposalRepository.findByUserAndDisposalDateBetween(eq(user), any(), any()))
        .thenReturn(List.of(d1, d2));

    Map<String, Integer> result = disposalService.aggregateDisposalByDestination();

    assertNotNull(result);
    assertEquals(10, result.get("Reciclagem"));

    verify(currentUserService).getCurrentUserEntity();
  }

  @Test
  void getMostUsedDestinationDisposal_shouldReturnMostFrequentDestination() {
    Disposal d1 = new Disposal(1L, "Vidro", 2, MaterialType.GLASS, DisposalDestination.RECYCLING, LocalDate.now(),
        user);
    Disposal d2 = new Disposal(2L, "Plástico", 8, MaterialType.PLASTIC, DisposalDestination.RECYCLING, LocalDate.now(),
        user);

    when(currentUserService.getCurrentUserEntity()).thenReturn(user);
    when(disposalRepository.findByUserAndDisposalDateBetween(eq(user), any(), any()))
        .thenReturn(List.of(d1, d2));

    DisposalMostFrequentDestinationDto result = disposalService.getMostUsedDestinationDisposal();

    assertNotNull(result);
    assertEquals("Reciclagem", result.destination());
    assertEquals(10, result.quantity());

    verify(currentUserService).getCurrentUserEntity();
  }

  @Test
  void getMostUsedDestinationDisposal_shouldReturnEmptyMessage_WhenNoDisposalExists() {
    when(currentUserService.getCurrentUserEntity()).thenReturn(user);
    when(disposalRepository.findByUserAndDisposalDateBetween(eq(user), any(), any()))
        .thenReturn(List.of());

    DisposalMostFrequentDestinationDto result = disposalService.getMostUsedDestinationDisposal();

    assertNotNull(result);
    assertEquals("Nenhum destino encontrado", result.destination());
    assertEquals(0, result.quantity());

    verify(currentUserService).getCurrentUserEntity();
  }

  @Test
  void getRecyclingPercentage_shouldReturnCorrectPercentage() {
    Disposal rec = new Disposal(1L, "Vidro", 4, MaterialType.GLASS, DisposalDestination.RECYCLING, LocalDate.now(),
        user);
    Disposal rejeito = new Disposal(2L, "Orgânico", 6, MaterialType.ORGANIC, DisposalDestination.WASTE, LocalDate.now(),
        user);

    when(currentUserService.getCurrentUserEntity()).thenReturn(user);
    when(disposalRepository.findByUserAndDisposalDateBetween(eq(user), any(), any()))
        .thenReturn(List.of(rec, rejeito));

    var result = disposalService.getRecyclingPercentage();

    assertNotNull(result);
    assertEquals(40, result.percentageDisposalRecycled());

    verify(currentUserService, times(2)).getCurrentUserEntity();
  }

  @Test
  void getRecyclingPercentage_shouldReturnZero_WhenNoDisposalsFound() {
    when(currentUserService.getCurrentUserEntity()).thenReturn(user);
    when(disposalRepository.findByUserAndDisposalDateBetween(eq(user), any(), any()))
        .thenReturn(List.of());

    var result = disposalService.getRecyclingPercentage();

    assertNotNull(result);
    assertEquals(0.0, result.percentageDisposalRecycled());

    verify(currentUserService, times(2)).getCurrentUserEntity();
  }
}
