package br.db.ecotrack.ecotrack_api.domain.entity;

import java.time.LocalDate;
import br.db.ecotrack.ecotrack_api.domain.enums.DisposalDestination;
import br.db.ecotrack.ecotrack_api.domain.enums.MaterialType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "DISPOSALS")
public class Disposal {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long disposalId;

  @Column(nullable = false, length = 50)
  private String disposalProduct;

  @Column(nullable = false)
  private Integer quantity;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private MaterialType materialType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private DisposalDestination destination;

  @Column(nullable = false)
  private LocalDate disposalDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
