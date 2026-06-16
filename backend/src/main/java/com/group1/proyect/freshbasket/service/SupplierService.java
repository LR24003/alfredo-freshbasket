package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.request.SupplierRequestDTO;
import com.group1.proyect.freshbasket.dto.response.SupplierResponseDTO;
import com.group1.proyect.freshbasket.entity.Supplier;

import java.util.List;

public interface SupplierService extends GenericService<Supplier, SupplierRequestDTO, SupplierResponseDTO, Long> {

    List<SupplierResponseDTO> searchSuppliersByName(String name);
}