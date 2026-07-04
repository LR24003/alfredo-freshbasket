package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.request.DteControlRequestDTO;
import com.group1.proyect.freshbasket.dto.response.DteControlResponseDTO;
import com.group1.proyect.freshbasket.entity.DteControl;

import java.util.List;
import java.util.UUID;

public interface DteControlService extends GenericService<DteControl, DteControlRequestDTO, DteControlResponseDTO, Long> {

    DteControlResponseDTO emitirDte(DteControlRequestDTO request);

    DteControlResponseDTO getByCodigoGeneracion(UUID codigoGeneracion);

    DteControlResponseDTO getByNumeroControl(String numeroControl);

    DteControlResponseDTO getBySaleId(Long saleId);

    List<DteControlResponseDTO> getByStatusFiscal(String statusFiscal);

    String generarSiguienteNumeroControl(String tipoDocumento);
}