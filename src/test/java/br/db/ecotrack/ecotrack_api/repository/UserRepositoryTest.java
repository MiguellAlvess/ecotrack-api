package br.db.ecotrack.ecotrack_api.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import br.db.ecotrack.ecotrack_api.common.UserConstants;
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

  @AfterEach
  public void afterEach() {
    User user = UserConstants.validUser();
    user.setUserId(null);
  }

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
    User firstUser = UserConstants.validUser();
    firstUser.setEmail("duplicated@email.com");
    testEntityManager.persistFlushFind(firstUser);
    User secondUser = UserConstants.validUser();
    secondUser.setEmail("duplicated@email.com");

    assertThatThrownBy(() -> {
      userRepository.saveAndFlush(secondUser);
    }).isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  public void getUser_ByExistingId_ReturnsUser() {
    User user = UserConstants.validUser();
    testEntityManager.persistFlushFind(user);

    User sut = userRepository.findById(user.getUserId()).get();

    assertThat(sut).isNotNull();
    assertThat(sut.getName()).isEqualTo(user.getName());
    assertThat(sut.getEmail()).isEqualTo(user.getEmail());
  }

  @Test
  public void getUser_ByNonExistingId_ReturnsNull() {
    User sut = userRepository.findById(1L).orElse(null);

    assertThat(sut).isNull();
  }
}
