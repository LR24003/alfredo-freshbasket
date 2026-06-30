package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.response.CustomerLoyalResponseDTO;
import java.util.List;

public interface CustomerLoyalService {

    List<CustomerLoyalResponseDTO> getAll();
    CustomerLoyalResponseDTO getById(Long id);
    List<CustomerLoyalResponseDTO> getByCustomerName(String customerName);
    List<CustomerLoyalResponseDTO> getByTotalPurchases(String totalPurchasesRange);

    byte[] exportExcel();
    byte[] exportPdf();
}
