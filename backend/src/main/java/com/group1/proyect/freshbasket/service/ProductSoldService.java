package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.response.ProductsSoldResponseDTO;
import java.util.List;

public interface ProductSoldService {

    List<ProductsSoldResponseDTO> getAll();
    ProductsSoldResponseDTO getById(Long id);
    List<ProductsSoldResponseDTO> getByProductName(String productName);
    List<ProductsSoldResponseDTO> getByUnitsSold(String unitsSoldRange);

    byte[] exportExcel();
    byte[] exportPdf();

}
