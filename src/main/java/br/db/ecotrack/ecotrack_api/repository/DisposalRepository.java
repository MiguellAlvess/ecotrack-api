package br.db.ecotrack.ecotrack_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.db.ecotrack.ecotrack_api.domain.entity.Disposal;

@Repository
public interface DisposalRepository extends JpaRepository<Disposal, Long> {

}
