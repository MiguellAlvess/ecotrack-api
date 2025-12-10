package br.db.ecotrack.ecotrack_api.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import static br.db.ecotrack.ecotrack_api.common.PurchaseConstants.invalidPurchase;
import static br.db.ecotrack.ecotrack_api.common.PurchaseConstants.validPurchase;
import static br.db.ecotrack.ecotrack_api.common.UserConstants.validUser;
import br.db.ecotrack.ecotrack_api.domain.entity.Purchase;
import br.db.ecotrack.ecotrack_api.domain.entity.User;

@DataJpaTest
public class PurchaseRepositoryTest {

  @Autowired
  private PurchaseRepository purchaseRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  private User persistValidUser() {
    return testEntityManager.persistFlushFind(validUser());
  }

  @Test
  public void createPurchase_WithValidData_ReturnsPurchase() {
    User user = persistValidUser();
    Purchase purchaseToSave = validPurchase();
    purchaseToSave.setUser(user);

    Purchase savedPurchase = purchaseRepository.save(purchaseToSave);

    Purchase persistedPurchase = testEntityManager.find(Purchase.class, savedPurchase.getPurchaseId());

    assertThat(persistedPurchase).isNotNull();
    assertThat(persistedPurchase.getPurchaseProduct()).isEqualTo(purchaseToSave.getPurchaseProduct());
    assertThat(persistedPurchase.getQuantity()).isEqualTo(purchaseToSave.getQuantity());
    assertThat(persistedPurchase.getMaterialType()).isEqualTo(purchaseToSave.getMaterialType());
    assertThat(persistedPurchase.getPurchaseDate()).isEqualTo(purchaseToSave.getPurchaseDate());
    assertThat(persistedPurchase.getUser().getUserId()).isEqualTo(user.getUserId());
  }

  @Test
  public void createPurchase_WithInvalidData_ThrowsException() {
    Purchase invalid = invalidPurchase();

    assertThatThrownBy(() -> purchaseRepository.saveAndFlush(invalid))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  public void getPurchase_ByExistingId_ReturnsPurchase() {
    User user = persistValidUser();
    Purchase purchase = validPurchase();
    purchase.setUser(user);

    Purchase persistedPurchase = testEntityManager.persistFlushFind(purchase);

    Purchase foundPurchase = purchaseRepository.findById(persistedPurchase.getPurchaseId()).orElse(null);

    assertThat(foundPurchase).isNotNull();
    assertThat(foundPurchase.getPurchaseProduct()).isEqualTo(persistedPurchase.getPurchaseProduct());
    assertThat(foundPurchase.getQuantity()).isEqualTo(persistedPurchase.getQuantity());
    assertThat(foundPurchase.getMaterialType()).isEqualTo(persistedPurchase.getMaterialType());
    assertThat(foundPurchase.getPurchaseDate()).isEqualTo(persistedPurchase.getPurchaseDate());
    assertThat(foundPurchase.getUser().getUserId()).isEqualTo(user.getUserId());
  }

  @Test
  public void getPurchase_ByNonExistingId_ReturnsNull() {
    Purchase purchase = purchaseRepository.findById(1L).orElse(null);

    assertThat(purchase).isNull();
  }

  @Test
  public void removePurchase_WithExistingPurchase_RemovesPurchase() {
    User user = persistValidUser();
    Purchase purchase = validPurchase();
    purchase.setUser(user);

    Purchase persistedPurchase = testEntityManager.persistFlushFind(purchase);

    purchaseRepository.deleteById(persistedPurchase.getPurchaseId());
    purchaseRepository.flush();

    Purchase removedPurchase = testEntityManager.find(Purchase.class, persistedPurchase.getPurchaseId());

    assertThat(removedPurchase).isNull();
  }
}
