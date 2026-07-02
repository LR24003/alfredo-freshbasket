package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.response.InventoryLostResponseDTO;
import java.util.List;

public interface InventoryLostService {

    List<InventoryLostResponseDTO> getAll();
    InventoryLostResponseDTO getById(Long id);
    List<InventoryLostResponseDTO> getByProductName(String productName);
    List<InventoryLostResponseDTO> getByExitReason(String exitReason);
    List<InventoryLostResponseDTO> getByUnitsLost(String unitsLostRange);

    byte[] exportExcel();
    byte[] exportPdf();

}
