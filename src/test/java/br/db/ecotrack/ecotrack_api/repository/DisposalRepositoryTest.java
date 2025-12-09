package br.db.ecotrack.ecotrack_api.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import static br.db.ecotrack.ecotrack_api.common.DisposalConstants.invalidDisposal;
import static br.db.ecotrack.ecotrack_api.common.DisposalConstants.validDisposal;
import static br.db.ecotrack.ecotrack_api.common.UserConstants.validUser;
import br.db.ecotrack.ecotrack_api.domain.entity.Disposal;
import br.db.ecotrack.ecotrack_api.domain.entity.User;
import jakarta.validation.ConstraintViolationException;

@DataJpaTest
public class DisposalRepositoryTest {

  @Autowired
  private DisposalRepository disposalRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  private User persistValidUser() {
    return testEntityManager.persistFlushFind(validUser());
  }

  @Test
  public void createDisposal_WithValidData_ReturnsDisposal() {
    User user = persistValidUser();
    Disposal disposalToSave = validDisposal();
    disposalToSave.setUser(user);

    Disposal savedDisposal = disposalRepository.save(disposalToSave);

    Disposal persistedDisposal = testEntityManager.find(Disposal.class, savedDisposal.getDisposalId());

    assertThat(persistedDisposal).isNotNull();
    assertThat(persistedDisposal.getDisposalProduct()).isEqualTo(disposalToSave.getDisposalProduct());
    assertThat(persistedDisposal.getQuantity()).isEqualTo(disposalToSave.getQuantity());
    assertThat(persistedDisposal.getMaterialType()).isEqualTo(disposalToSave.getMaterialType());
    assertThat(persistedDisposal.getDestination()).isEqualTo(disposalToSave.getDestination());
    assertThat(persistedDisposal.getDisposalDate()).isEqualTo(disposalToSave.getDisposalDate());
    assertThat(persistedDisposal.getUser().getUserId()).isEqualTo(user.getUserId());
  }

  @Test
  public void createDisposal_WithInvalidData_ThrowsException() {
    User user = persistValidUser();
    Disposal invalid = invalidDisposal();
    invalid.setUser(user);

    assertThatThrownBy(() -> {
      disposalRepository.save(invalid);
      disposalRepository.flush();
    }).isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class);
  }

  @Test
  public void getDisposal_ByExistingId_ReturnsDisposal() {
    User user = persistValidUser();
    Disposal disposal = validDisposal();
    disposal.setUser(user);

    Disposal persistedDisposal = testEntityManager.persistFlushFind(disposal);

    Disposal foundDisposal = disposalRepository.findById(persistedDisposal.getDisposalId()).orElse(null);

    assertThat(foundDisposal).isNotNull();
    assertThat(foundDisposal.getDisposalProduct()).isEqualTo(persistedDisposal.getDisposalProduct());
    assertThat(foundDisposal.getQuantity()).isEqualTo(persistedDisposal.getQuantity());
    assertThat(foundDisposal.getMaterialType()).isEqualTo(persistedDisposal.getMaterialType());
    assertThat(foundDisposal.getDestination()).isEqualTo(persistedDisposal.getDestination());
    assertThat(foundDisposal.getDisposalDate()).isEqualTo(persistedDisposal.getDisposalDate());
    assertThat(foundDisposal.getUser().getUserId()).isEqualTo(user.getUserId());
  }

  @Test
  public void getDisposal_ByNonExistingId_ReturnsNull() {
    Disposal disposal = disposalRepository.findById(1L).orElse(null);

    assertThat(disposal).isNull();
  }

  @Test
  public void removeDisposal_WithExistingDisposal_RemovesDisposal() {
    User user = persistValidUser();
    Disposal disposal = validDisposal();
    disposal.setUser(user);

    Disposal persistedDisposal = testEntityManager.persistFlushFind(disposal);

    disposalRepository.deleteById(persistedDisposal.getDisposalId());
    disposalRepository.flush();

    Disposal removedDisposal = testEntityManager.find(Disposal.class, persistedDisposal.getDisposalId());

    assertThat(removedDisposal).isNull();
  }
}
