package br.db.ecotrack.ecotrack_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.db.ecotrack.ecotrack_api.domain.entity.Disposal;
import br.db.ecotrack.ecotrack_api.domain.entity.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DisposalRepository extends JpaRepository<Disposal, Long> {
  List<Disposal> findByUser(User user);

  List<Disposal> findByUserAndDisposalDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
