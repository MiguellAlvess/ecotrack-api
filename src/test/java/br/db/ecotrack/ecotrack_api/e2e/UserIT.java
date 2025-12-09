package br.db.ecotrack.ecotrack_api.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import br.db.ecotrack.ecotrack_api.controller.dto.user.LoginResponseDto;
import br.db.ecotrack.ecotrack_api.controller.dto.user.UserRequestDto;

@ActiveProfiles("it")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void createUser_ReturnsCreated() {
    UserRequestDto request = new UserRequestDto(
        "UserTest",
        "teste123@gmail.com",
        "1223456");

    ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity(
        "/api/users/auth/register",
        request,
        LoginResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().user().email()).isEqualTo(request.email());
    assertThat(response.getBody().user().name()).isEqualTo(request.name());
  }
}
