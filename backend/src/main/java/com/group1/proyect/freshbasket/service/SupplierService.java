package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.request.SupplierRequestDTO;
import com.group1.proyect.freshbasket.dto.response.SupplierResponseDTO;

import java.util.List;

public interface SupplierService {

    List<SupplierResponseDTO> getAllSuppliers();

    SupplierResponseDTO getSupplierById(Long id);

    SupplierResponseDTO createSupplier(SupplierRequestDTO requestDTO);

    SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO requestDTO);

    void deleteSupplier(Long id);

    List<SupplierResponseDTO> searchSuppliersByName(String name);
}
