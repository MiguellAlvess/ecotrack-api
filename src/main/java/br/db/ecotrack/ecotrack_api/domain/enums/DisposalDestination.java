package br.db.ecotrack.ecotrack_api.domain.enums;

public enum DisposalDestination {
  RECYCLING("Reciclagem"),
  COMPOSTING("Compostagem"),
  WASTE("Rejeito"),
  DONATION("Doação");

  private final String description;

  DisposalDestination(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
