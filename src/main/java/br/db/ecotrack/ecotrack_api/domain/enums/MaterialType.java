package br.db.ecotrack.ecotrack_api.domain.enums;

public enum MaterialType {
  PLASTIC("Plástico"),
  GLASS("Vidro"),
  METAL("Metal"),
  PAPER("Papel"),
  ORGANIC("Orgânico"),
  NOT_RECYCLABLE("Não Reciclavel");

  private final String typeName;

  MaterialType(String typeName) {
    this.typeName = typeName;
  }

  public String getTypeName() {
    return typeName;
  }
}
