package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.request.ExitRequestDTO;
import com.group1.proyect.freshbasket.dto.response.ExitResponseDTO;

import java.util.List;

public interface ExitService {

    List<ExitResponseDTO> getAllExits();

    ExitResponseDTO getExitById(Long id);

    ExitResponseDTO createExit(ExitRequestDTO requestDTO);

    ExitResponseDTO updateExit(Long id, ExitRequestDTO requestDTO);

    void deleteExit(Long id);

}