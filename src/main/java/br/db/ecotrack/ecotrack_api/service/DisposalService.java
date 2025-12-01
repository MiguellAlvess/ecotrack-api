package br.db.ecotrack.ecotrack_api.service;

import org.springframework.stereotype.Service;

import br.db.ecotrack.ecotrack_api.controller.request.DisposalRequestDto;
import br.db.ecotrack.ecotrack_api.controller.response.DisposalResponseDto;
import br.db.ecotrack.ecotrack_api.domain.entity.Disposal;
import br.db.ecotrack.ecotrack_api.mapper.DisposalMapper;
import br.db.ecotrack.ecotrack_api.repository.DisposalRepository;
import jakarta.transaction.Transactional;

@Service
public class DisposalService {

  private final DisposalRepository disposalRepository;
  private final DisposalMapper disposalMapper;

  public DisposalService(DisposalRepository disposalRepository, DisposalMapper disposalMapper) {
    this.disposalRepository = disposalRepository;
    this.disposalMapper = disposalMapper;
  }

  @Transactional
  public DisposalResponseDto createDisposal(DisposalRequestDto disposalRequestDto) {
    Disposal disposalToSave = disposalMapper.toEntity(disposalRequestDto);
    Disposal savedDisposal = disposalRepository.save(disposalToSave);
    return disposalMapper.toDto(savedDisposal);
  }

}
