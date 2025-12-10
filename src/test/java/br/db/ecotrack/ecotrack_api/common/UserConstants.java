package br.db.ecotrack.ecotrack_api.common;

import java.util.ArrayList;

import br.db.ecotrack.ecotrack_api.domain.entity.User;

public class UserConstants {

  public static User validUser() {
    return new User(
        null,
        "Teste",
        "test@gmail.com",
        "password123",
        new ArrayList<>(),
        new ArrayList<>());
  }

  public static User invalidUser() {
    return new User(
        null,
        "",
        "",
        "",
        new ArrayList<>(),
        new ArrayList<>());
  }
}
