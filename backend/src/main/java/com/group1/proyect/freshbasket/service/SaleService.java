package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.request.SaleRequestDTO;
import com.group1.proyect.freshbasket.dto.response.SaleDetailsResponseDTO;
import com.group1.proyect.freshbasket.dto.response.SaleResponseDTO;
import com.group1.proyect.freshbasket.entity.Sale;

import java.util.List;

public interface SaleService extends GenericService<Sale, SaleRequestDTO, SaleResponseDTO, Long> {

    List<SaleResponseDTO> getSalesByStatus(String status);

    List<SaleResponseDTO> getSalesByCustomerId(Long customerId);

    List<SaleDetailsResponseDTO> getDetailsBySaleId(Long saleId);
}