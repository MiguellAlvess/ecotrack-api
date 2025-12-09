package br.db.ecotrack.ecotrack_api.repository;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static br.db.ecotrack.ecotrack_api.common.UserConstants.USER;
import br.db.ecotrack.ecotrack_api.domain.entity.User;

@DataJpaTest
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @Test
  public void createUser_WithValidData_ReturnsUser() {
    User user = userRepository.save(USER);

    User sut = testEntityManager.find(User.class, user.getUserId());

    assertThat(sut).isNotNull();
    assertThat(sut.getName()).isEqualTo(USER.getName());
    assertThat(sut.getEmail()).isEqualTo(USER.getEmail());
  }
}
