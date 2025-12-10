package br.db.ecotrack.ecotrack_api.common;

import java.time.LocalDate;

import br.db.ecotrack.ecotrack_api.domain.entity.Purchase;
import br.db.ecotrack.ecotrack_api.domain.enums.MaterialType;

public class PurchaseConstants {

  public static Purchase validPurchase() {
    return new Purchase(
        null,
        "Produto teste",
        10,
        MaterialType.PLASTIC,
        LocalDate.now(),
        UserConstants.validUser());
  }

  public static Purchase invalidPurchase() {
    return new Purchase(
        null,
        "",
        -1,
        null,
        null,
        null);
  }
}
