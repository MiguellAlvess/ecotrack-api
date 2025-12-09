package br.db.ecotrack.ecotrack_api.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import static br.db.ecotrack.ecotrack_api.common.UserConstants.invalidUser;
import static br.db.ecotrack.ecotrack_api.common.UserConstants.validUser;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import jakarta.validation.ConstraintViolationException;

@DataJpaTest
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @Test
  public void createUser_WithValidData_ReturnsUser() {
    User request = validUser();

    User user = userRepository.save(request);

    User sut = testEntityManager.find(User.class, user.getUserId());
    assertThat(sut).isNotNull();
    assertThat(sut.getName()).isEqualTo(request.getName());
    assertThat(sut.getEmail()).isEqualTo(request.getEmail());
  }

  @Test
  public void createUser_WithInvalidData_ThrowsException() {
    User invalid = invalidUser();

    assertThatThrownBy(() -> {
      userRepository.save(invalid);
      userRepository.flush();
    }).isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  public void createUser_WithExistingEmail_ThrowsException() {
    User firstUser = new User();
    firstUser.setName("First User");
    firstUser.setEmail("duplicated@email.com");
    firstUser.setPassword("password123");

    userRepository.saveAndFlush(firstUser);

    User secondUser = new User();
    secondUser.setName("Second User");
    secondUser.setEmail("duplicated@email.com");
    secondUser.setPassword("otherPass123");

    assertThatThrownBy(() -> {
      userRepository.save(secondUser);
      userRepository.flush();
    }).isInstanceOf(DataIntegrityViolationException.class);
  }
}
