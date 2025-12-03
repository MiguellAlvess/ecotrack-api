package br.db.ecotrack.ecotrack_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.db.ecotrack.ecotrack_api.service.MaterialService;
import br.db.ecotrack.ecotrack_api.controller.request.MaterialRequestDto;
import br.db.ecotrack.ecotrack_api.controller.response.MaterialResponseDto;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
public class MaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MaterialService materialService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAllMaterials_Should_Return200AndListOfMaterials_WhenMaterialsExist() throws Exception {
        MaterialResponseDto dto1 = new MaterialResponseDto(1L, "Plástico", "...");
        MaterialResponseDto dto2 = new MaterialResponseDto(2L, "Vidro", "...");
        List<MaterialResponseDto> materialList = Arrays.asList(dto1, dto2);
        when(materialService.getAll()).thenReturn(materialList);

        mockMvc.perform(get("/api/materials"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type", is("Plástico")));
    }

    @Test
    @WithMockUser
    void getMaterialById_Should_Return200AndMaterial_WhenIdExists() throws Exception {
        Long existingId = 1L;
        MaterialResponseDto responseDto = new MaterialResponseDto(existingId, "Plástico", "Qualquer tipo de plástico");

        when(materialService.getById(existingId)).thenReturn(responseDto);

        mockMvc.perform(get("/api/materials/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.materialId", is(1)))
                .andExpect(jsonPath("$.type", is("Plástico")));
    }

    @Test
    @WithMockUser
    void getMaterialById_Should_Return404_WhenIdDoesNotExist() throws Exception {
        Long nonExistentId = 99L;

        when(materialService.getById(nonExistentId))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Material não encontrado"));

        mockMvc.perform(get("/api/materials/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createMaterial_ShouldReturn201_WhenRequestIsValid() throws Exception {
        MaterialRequestDto requestDto = new MaterialRequestDto("Papel", "Qualquer tipo de papel");
        MaterialResponseDto responseDto = new MaterialResponseDto(3L, "Papel", "Qualquer tipo de papel");

        when(materialService.createMaterial(any(MaterialRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/materials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.materialId", is(3)))
                .andExpect(jsonPath("$.type", is("Papel")));
    }

    @Test
    @WithMockUser
    void createMaterial_ShouldReturn400_WhenRequestIsInvalid() throws Exception {
        MaterialRequestDto invalidRequestDto = new MaterialRequestDto(null, "Descrição sem tipo");

        mockMvc.perform(post("/api/materials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createMaterial_ShouldReturn409_WhenMaterialTypeAlreadyExists() throws Exception {
        MaterialRequestDto requestDto = new MaterialRequestDto("Plástico", "Tentando criar de novo");

        when(materialService.createMaterial(any(MaterialRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Tipo de material já existe"));

        mockMvc.perform(post("/api/materials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void updateMaterial_ShouldReturn200_AndUpdatedMaterial_WhenRequestIsValid() throws Exception {
        Long materialId = 1L;
        MaterialRequestDto requestDto = new MaterialRequestDto("Plástico PET", "Plástico do tipo PET");
        MaterialResponseDto responseDto = new MaterialResponseDto(materialId, "Plástico PET", "Plástico do tipo PET");

        when(materialService.updateMaterial(eq(materialId), any(MaterialRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/api/materials/{id}", materialId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.materialId", is(1)))
                .andExpect(jsonPath("$.type", is("Plástico PET")));
    }

    @Test
    @WithMockUser
    void updateMaterial_ShouldReturn404_WhenIdDoesNotExist() throws Exception {
        Long nonExistentId = 99L;
        MaterialRequestDto requestDto = new MaterialRequestDto("Inexistente", "...");

        when(materialService.updateMaterial(eq(nonExistentId), any(MaterialRequestDto.class)))
                .thenThrow(new jakarta.persistence.EntityNotFoundException());

        mockMvc.perform(patch("/api/materials/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }
}
