package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.response.InventoryReportResponseDTO;
import java.util.List;

public interface InventoryReportService {

    List<InventoryReportResponseDTO> getAll();
    InventoryReportResponseDTO getById(Long id);
    List<InventoryReportResponseDTO> getByProductName(String productName);
    List<InventoryReportResponseDTO> getByStockAvailable(String stockAvailableRange);

    byte[] exportExcel();
    byte[] exportPdf();
}
