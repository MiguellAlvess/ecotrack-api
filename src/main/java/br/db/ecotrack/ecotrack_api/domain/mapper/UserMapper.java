package br.db.ecotrack.ecotrack_api.domain.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.db.ecotrack.ecotrack_api.domain.dto.UserRequestDto;
import br.db.ecotrack.ecotrack_api.domain.dto.UserResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toDto(User user);

    @Mapping(target = "userId", ignore = true)
    User toEntity(UserRequestDto userRequestDto);
}

