package br.db.ecotrack.ecotrack_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.db.ecotrack.ecotrack_api.domain.entity.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findAllByUser_UserId(Long userId);
}
