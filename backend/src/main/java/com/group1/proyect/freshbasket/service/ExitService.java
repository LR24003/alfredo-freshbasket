package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.request.ExitRequestDTO;
import com.group1.proyect.freshbasket.dto.response.ExitResponseDTO;
import com.group1.proyect.freshbasket.entity.Exit;

public interface ExitService extends GenericService<Exit,
        ExitRequestDTO, ExitResponseDTO, Long> {

}