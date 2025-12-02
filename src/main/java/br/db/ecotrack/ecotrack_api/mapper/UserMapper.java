package br.db.ecotrack.ecotrack_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.db.ecotrack.ecotrack_api.controller.request.UserRequestDto;
import br.db.ecotrack.ecotrack_api.controller.response.UserResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserResponseDto toDto(User user);

  @Mapping(target = "purchases", ignore = true)
  @Mapping(target = "disposals", ignore = true)
  @Mapping(target = "userId", ignore = true)
  User toEntity(UserRequestDto userRequestDto);
}
