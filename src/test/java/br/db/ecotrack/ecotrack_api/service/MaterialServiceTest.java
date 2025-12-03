package br.db.ecotrack.ecotrack_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import br.db.ecotrack.ecotrack_api.controller.request.MaterialRequestDto;
import br.db.ecotrack.ecotrack_api.controller.response.MaterialResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.Material;
import br.db.ecotrack.ecotrack_api.mapper.MaterialMapper;
import br.db.ecotrack.ecotrack_api.repository.MaterialRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private MaterialMapper materialMapper;

    @InjectMocks
    private MaterialService materialService;

    @Test
    void getAll_ShouldReturnListOfMaterialResponseDto_WhenMaterialsExist() {
        // Arrange
        Material material1 = new Material(1L, "Plástico", "Material de plástico reciclável");
        Material material2 = new Material(2L, "Vidro", "Material de vidro reciclável");
        List<Material> mockMaterialList = Arrays.asList(material1, material2);

        MaterialResponseDto dto1 = new MaterialResponseDto(1L, "Plástico", "Material de plástico reciclável");
        MaterialResponseDto dto2 = new MaterialResponseDto(2L, "Vidro", "Material de vidro reciclável");

        when(materialRepository.findAll()).thenReturn(mockMaterialList);
        when(materialMapper.toDto(material1)).thenReturn(dto1);
        when(materialMapper.toDto(material2)).thenReturn(dto2);

        // Act
        List<MaterialResponseDto> resultList = materialService.getAll();

        // Assert
        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        assertEquals("Plástico", resultList.get(0).type());
        assertEquals("Vidro", resultList.get(1).type());
    }

    @Test
    void getById_ShouldReturnMaterialResponseDto_WhenMaterialExists() {
        // Arrange
        Long materialId = 1L;
        Material mockMaterial = new Material(materialId, "Plástico", "Material de plástico reciclável");
        MaterialResponseDto mockDto = new MaterialResponseDto(materialId, "Plástico",
                "Material de plástico reciclável");

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(mockMaterial));
        when(materialMapper.toDto(mockMaterial)).thenReturn(mockDto);

        // Act
        MaterialResponseDto resultDto = materialService.getById(materialId);

        // Assert
        assertNotNull(resultDto);
        assertEquals(materialId, resultDto.materialId());
        assertEquals("Plástico", resultDto.type());
    }

    @Test
    void getById_ShouldThrowEntityNotFoundException_WhenMaterialDoesNotExist() {
        // Arrange
        Long materialId = 99L;
        when(materialRepository.findById(materialId)).thenReturn(Optional.empty());

        // Act
        Class<EntityNotFoundException> expectedExceptionClass = EntityNotFoundException.class;
        Executable executable = () -> materialService.getById(materialId);

        // Assert
        assertThrows(expectedExceptionClass, executable);
    }

    @Test
    void createMaterial_ShouldSaveAndReturnMaterialResponseDto_WhenMaterialIsCreated() {
        // Arrange
        Long materialId = 2L;
        MaterialRequestDto requestDto = new MaterialRequestDto("Vidro", "Material de vidro reciclável");
        Material materialToSave = new Material(null, "Vidro", "Material de vidro reciclável");
        Material savedMaterial = new Material(materialId, "Vidro", "Material de vidro reciclável");
        MaterialResponseDto responseDto = new MaterialResponseDto(materialId, "Vidro", "Material de vidro reciclável");

        when(materialMapper.toEntity(requestDto)).thenReturn(materialToSave);
        when(materialRepository.save(materialToSave)).thenReturn(savedMaterial);
        when(materialMapper.toDto(savedMaterial)).thenReturn(responseDto);

        // Act
        MaterialResponseDto resultDto = materialService.createMaterial(requestDto);

        // Assert
        assertNotNull(resultDto);
        assertEquals(2L, resultDto.materialId());
        assertEquals("Vidro", resultDto.type());
    }

    @Test
    void updateMaterial_ShouldUpdateAndReturnMaterialResponseDto_WhenMaterialExists() {
        // Arrange
        Long materialId = 1L;
        MaterialRequestDto requestDto = new MaterialRequestDto("Plástico atualizado", "Descrição atualizada");
        Material existingMaterial = new Material(materialId, "Plástico anterior", "Descrição anterior");
        Material updatedMaterial = new Material(materialId, "Plástico atualizado", "Descrição atualizada");
        MaterialResponseDto responseDto = new MaterialResponseDto(materialId, "Plástico atualizado", "Descrição atualizada");

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(existingMaterial));
        when(materialRepository.save(existingMaterial)).thenReturn(updatedMaterial);
        when(materialMapper.toDto(updatedMaterial)).thenReturn(responseDto);

        // Act
        MaterialResponseDto resultDto = materialService.updateMaterial(materialId, requestDto);

        // Assert
        assertNotNull(resultDto);
        assertEquals(materialId, resultDto.materialId());
        assertEquals("Plástico atualizado", resultDto.type());
        assertEquals("Descrição atualizada", resultDto.description());
    }
}
