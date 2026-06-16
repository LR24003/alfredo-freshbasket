package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.request.EntryRequestDTO;
import com.group1.proyect.freshbasket.dto.response.EntryResponseDTO;
import com.group1.proyect.freshbasket.entity.Entry;

public interface EntryService extends GenericService<Entry,
        EntryRequestDTO, EntryResponseDTO, Long> {

}