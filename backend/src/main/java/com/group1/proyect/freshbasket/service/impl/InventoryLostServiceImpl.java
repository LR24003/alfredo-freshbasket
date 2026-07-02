package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.response.InventoryLostResponseDTO;
import com.group1.proyect.freshbasket.entity.InventoryLostReport;
import com.group1.proyect.freshbasket.repository.InventoryLostRepository;
import com.group1.proyect.freshbasket.service.InventoryLostService;
import com.group1.proyect.freshbasket.utils.ExportDocUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryLostServiceImpl implements InventoryLostService {

    private final InventoryLostRepository inventoryLostRepository;

    @Override
    public List<InventoryLostResponseDTO> getAll(){
        return inventoryLostRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryLostResponseDTO getById(Long id){
        return inventoryLostRepository.findById(id)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Registro de perdidas de inventario no encontrado con ese Id: " + id));
    }

    @Override
    public List<InventoryLostResponseDTO> getByProductName(String productName) {
        return inventoryLostRepository.findByProductNameContainingIgnoreCase(productName)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryLostResponseDTO> getByExitReason(String exitReason) {
        return inventoryLostRepository.findByExitReasonContainingIgnoreCase(exitReason)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryLostResponseDTO> getByUnitsLost(String unitsLostRange){
        int min = 0;
        int max = Integer.MAX_VALUE;

        if(unitsLostRange != null && !unitsLostRange.trim().isEmpty()){
            String range = unitsLostRange.trim();

            if (range.contains(" a ")) {
                String[] parts = range.split(" a ");
                try {
                    min = Integer.parseInt(parts[0].trim());
                    max = Integer.parseInt(parts[1].trim());
                }catch (NumberFormatException e){
                    throw new RuntimeException("Formato de rango invalido: " + unitsLostRange);
                }
            }else if (range.endsWith("+")){
                try {
                    min = Integer.parseInt(range.replace("+", "").trim());
                } catch (NumberFormatException e){
                    throw new RuntimeException("Formato de rango invalido: " + unitsLostRange);
                }
            } else {
                try {
                    min = Integer.parseInt(range);
                    max = min;
                }catch (NumberFormatException e){
                    throw new RuntimeException("Formato de rango invalido: " + unitsLostRange);
                }
            }
        }

        return inventoryLostRepository.findByUnitsLostBetween(min, max)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private InventoryLostResponseDTO convertToResponseDto(InventoryLostReport Entity){
        InventoryLostResponseDTO dto = new InventoryLostResponseDTO();
        dto.setId(Entity.getId());
        dto.setProductName(Entity.getProductName());
        dto.setExitReason(Entity.getExitReason());
        dto.setUnitsLost(Entity.getUnitsLost());
        dto.setTotalLost(Entity.getTotalLost());

        return dto;
    }

    @Override
    public byte[] exportExcel(){
        List<InventoryLostResponseDTO> data = this.getAll();
        String[] headers = {"Id", "Nombre del producto", "Razón salida", "Total unidades perdidas", "Monto perdidas($)"};

        List<Map<String, Object>> rows = new ArrayList<>();
        for (InventoryLostResponseDTO dto : data) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", dto.getId());
            row.put("name", dto.getProductName());
            row.put("reason", dto.getExitReason());
            row.put("units", dto.getUnitsLost());
            row.put("total", dto.getTotalLost());
            rows.add(row);
        }

        return ExportDocUtil.toExcel("Reporte de inventario perdido o dañado", headers, rows);
    }

    @Override
    public byte[] exportPdf(){
        List<InventoryLostResponseDTO> data = this.getAll();
        String[] headers = {"Id", "Nombre del producto", "Razón salida", "Total unidades perdidas", "Monto perdidas($)"};

        List<List<String>> rows = new ArrayList<>();
        for (InventoryLostResponseDTO dto : data) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(dto.getId()));
            row.add(dto.getProductName());
            row.add(String.valueOf(dto.getExitReason()));
            row.add(String.valueOf(dto.getUnitsLost()));
            row.add("$" + dto.getTotalLost());
            rows.add(row);
        }

        return ExportDocUtil.toPdf("Reporte de inventario perdido o dañado", headers, null, rows);
    }
}
