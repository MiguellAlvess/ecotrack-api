package br.db.ecotrack.ecotrack_api.service;

import org.springframework.stereotype.Service;
import br.db.ecotrack.ecotrack_api.domain.dto.MaterialRequestDto;
import br.db.ecotrack.ecotrack_api.domain.dto.MaterialResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.Material;
import br.db.ecotrack.ecotrack_api.repository.MaterialRepository;
import br.db.ecotrack.ecotrack_api.domain.mapper.MaterialMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    public MaterialService(MaterialRepository materialRepository, MaterialMapper materialMapper) {
        this.materialRepository = materialRepository;
        this.materialMapper = materialMapper;
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDto> getAll() {
        List<Material> materials = materialRepository.findAll();
        return materials.stream()
                .map(material -> materialMapper.toDto(material))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MaterialResponseDto getById(Long id) {
        return materialRepository.findById(id)
                .map(material -> materialMapper.toDto(material))
                .orElseThrow(() -> new EntityNotFoundException("Material not found: " + id));
    }

    @Transactional
    public MaterialResponseDto createMaterial(MaterialRequestDto materialRequestDto) {
        Material materialToSave = materialMapper.toEntity(materialRequestDto);
        Material savedMaterial = materialRepository.save(materialToSave);
        return materialMapper.toDto(savedMaterial);
    }

    @Transactional
    public MaterialResponseDto updateMaterial(Long id, MaterialRequestDto materialRequestDto) {
        Material existingMaterial = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Material not found with id: " + id));

        if (materialRequestDto.type() != null) {
            existingMaterial.setType(materialRequestDto.type());
        }
        if (materialRequestDto.description() != null) {
            existingMaterial.setDescription(materialRequestDto.description());
        }

        Material updatedMaterial = materialRepository.save(existingMaterial);
        return materialMapper.toDto(updatedMaterial);
    }
}
