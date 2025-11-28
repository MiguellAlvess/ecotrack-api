package br.db.ecotrack.ecotrack_api.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import br.db.ecotrack.ecotrack_api.domain.dto.MaterialDto;
import br.db.ecotrack.ecotrack_api.domain.entity.Material;

@Mapper(componentModel = "spring")
public interface MaterialMapper {

    MaterialDto toDto(Material material);

    @Mapping(target = "materialId", ignore = true)
    Material toEntity(MaterialDto materialDto);
}
