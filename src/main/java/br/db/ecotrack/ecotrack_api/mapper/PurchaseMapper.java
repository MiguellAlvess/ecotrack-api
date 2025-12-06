package br.db.ecotrack.ecotrack_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.db.ecotrack.ecotrack_api.controller.dto.request.PurchaseRequestDto;
import br.db.ecotrack.ecotrack_api.controller.dto.response.PurchaseResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.Purchase;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

  PurchaseResponseDto toDto(Purchase purchase);

  @Mapping(target = "user", ignore = true)
  @Mapping(target = "purchaseId", ignore = true)
  Purchase toEntity(PurchaseRequestDto purchaseRequestDto);
}
