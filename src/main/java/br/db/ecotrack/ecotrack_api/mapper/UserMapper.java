package br.db.ecotrack.ecotrack_api.mapper;

import org.springframework.stereotype.Component;

import br.db.ecotrack.ecotrack_api.domain.entity.User;
import br.db.ecotrack.ecotrack_api.domain.entity.dto.UserResponseDto;

@Component
public class UserMapper {

    public UserResponseDto toResponseDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail());
    }
}