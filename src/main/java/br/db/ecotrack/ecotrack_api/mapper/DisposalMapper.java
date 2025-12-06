package br.db.ecotrack.ecotrack_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.db.ecotrack.ecotrack_api.controller.dto.request.DisposalRequestDto;
import br.db.ecotrack.ecotrack_api.controller.dto.response.DisposalResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.Disposal;

@Mapper(componentModel = "spring")
public interface DisposalMapper {

  DisposalResponseDto toDto(Disposal disposal);

  @Mapping(target = "user", ignore = true)
  @Mapping(target = "disposalId", ignore = true)
  Disposal toEntity(DisposalRequestDto disposalRequestDto);
}
