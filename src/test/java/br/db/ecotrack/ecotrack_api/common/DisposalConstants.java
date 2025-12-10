package br.db.ecotrack.ecotrack_api.common;

import java.time.LocalDate;

import br.db.ecotrack.ecotrack_api.domain.entity.Disposal;
import br.db.ecotrack.ecotrack_api.domain.enums.DisposalDestination;
import br.db.ecotrack.ecotrack_api.domain.enums.MaterialType;

public class DisposalConstants {

  public static Disposal validDisposal() {
    Disposal disposal = new Disposal();
    disposal.setDisposalId(null);
    disposal.setDisposalProduct("Garrafa plástica usada");
    disposal.setQuantity(5);
    disposal.setMaterialType(MaterialType.PLASTIC);
    disposal.setDestination(DisposalDestination.RECYCLING);
    disposal.setDisposalDate(LocalDate.of(2025, 1, 1));
    disposal.setUser(null);
    return disposal;
  }

  public static Disposal invalidDisposal() {
    Disposal disposal = new Disposal();
    disposal.setDisposalId(null);
    disposal.setDisposalProduct("");
    disposal.setQuantity(-1);
    disposal.setMaterialType(null);
    disposal.setDestination(null);
    disposal.setDisposalDate(null);
    disposal.setUser(null);
    return disposal;
  }
}
