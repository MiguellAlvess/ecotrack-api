package br.db.ecotrack.ecotrack_api.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "USERS")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(nullable = false, length = 250)
  private String name;

  @Pattern(regexp = "^[\\w.-]+@[\\w.-]+\\.\\w{2,}$", message = "Invalid email format")
  @Column(nullable = false, length = 500, unique = true)
  private String email;

  @Pattern(regexp = ".{8,}", message = "Password must be at least 8 characters long")
  @Column(nullable = false, length = 225)
  private String password;

}
