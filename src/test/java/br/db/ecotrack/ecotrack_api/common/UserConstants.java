package br.db.ecotrack.ecotrack_api.common;

import java.util.ArrayList;

import br.db.ecotrack.ecotrack_api.domain.entity.User;

public class UserConstants {
  public static final User USER = new User(
      null,
      "Teste",
      "test@gmail.com",
      "password123456",
      new ArrayList<>(),
      new ArrayList<>());

  public static final User INVALID_USER = new User(
      null,
      "",
      "",
      "",
      new ArrayList<>(),
      new ArrayList<>());
}
