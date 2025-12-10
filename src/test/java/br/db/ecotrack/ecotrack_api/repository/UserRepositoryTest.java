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
    User userToSave = validUser();

    User savedUser = userRepository.save(userToSave);

    User persistedUser = testEntityManager.find(User.class, savedUser.getUserId());

    assertThat(persistedUser).isNotNull();
    assertThat(persistedUser.getName()).isEqualTo(userToSave.getName());
    assertThat(persistedUser.getEmail()).isEqualTo(userToSave.getEmail());
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
    User firstUser = validUser();
    firstUser.setEmail("duplicated@email.com");
    testEntityManager.persistFlushFind(firstUser);

    User secondUser = validUser();
    secondUser.setEmail("duplicated@email.com");

    assertThatThrownBy(() -> userRepository.saveAndFlush(secondUser))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  public void getUser_ByExistingId_ReturnsUser() {
    User persistedUser = testEntityManager.persistFlushFind(validUser());

    User foundUser = userRepository.findById(persistedUser.getUserId()).orElse(null);

    assertThat(foundUser).isNotNull();
    assertThat(foundUser.getName()).isEqualTo(persistedUser.getName());
    assertThat(foundUser.getEmail()).isEqualTo(persistedUser.getEmail());
  }

  @Test
  public void getUser_ByNonExistingId_ReturnsNull() {
    User user = userRepository.findById(1L).orElse(null);

    assertThat(user).isNull();
  }

  @Test
  public void removeUser_WithExistingUser_RemovesUser() {
    User user = testEntityManager.persistFlushFind(validUser());

    userRepository.deleteById(user.getUserId());
    userRepository.flush();

    User removedUser = testEntityManager.find(User.class, user.getUserId());

    assertThat(removedUser).isNull();
  }
}
