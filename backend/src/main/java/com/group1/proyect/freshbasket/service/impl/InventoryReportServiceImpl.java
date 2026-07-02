package com.group1.proyect.freshbasket.service.impl;


import com.group1.proyect.freshbasket.dto.response.InventoryReportResponseDTO;
import com.group1.proyect.freshbasket.entity.InventoryReport;
import com.group1.proyect.freshbasket.repository.InventoryReportRepository;
import com.group1.proyect.freshbasket.service.InventoryReportService;
import com.group1.proyect.freshbasket.utils.ExportDocUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryReportServiceImpl implements InventoryReportService {

    private final InventoryReportRepository inventoryReportRepository;

    @Override
    public List<InventoryReportResponseDTO> getAll(){
        return inventoryReportRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }


    @Override
    public InventoryReportResponseDTO getById(Long id){
        return inventoryReportRepository.findById(id)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Registro de inventario no encontrado con ese Id: " + id));
    }

    @Override
    public List<InventoryReportResponseDTO> getByProductName(String productName) {
        return inventoryReportRepository.findByProductNameContainingIgnoreCase(productName)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryReportResponseDTO> getByStockAvailable(String stockAvailableRange){
        int min = 0;
        int max = Integer.MAX_VALUE;

        if(stockAvailableRange != null && !stockAvailableRange.trim().isEmpty()){
            String range = stockAvailableRange.trim();

            if (range.contains(" a ")) {
                String[] parts = range.split(" a ");
                try {
                    min = Integer.parseInt(parts[0].trim());
                    max = Integer.parseInt(parts[1].trim());
                }catch (NumberFormatException e){
                    throw new RuntimeException("Formato de rango invalido: " + stockAvailableRange);
                }
            }else if (range.endsWith("+")){
                try {
                    min = Integer.parseInt(range.replace("+", "").trim());
                } catch (NumberFormatException e){
                    throw new RuntimeException("Formato de rango invalido: " + stockAvailableRange);
                }
            } else {
                try {
                    min = Integer.parseInt(range);
                    max = min;
                }catch (NumberFormatException e){
                    throw new RuntimeException("Formato de rango invalido: " + stockAvailableRange);
                }
            }
        }

        return inventoryReportRepository.findByStockAvailableBetween(min, max)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private InventoryReportResponseDTO convertToResponseDto(InventoryReport Entity){
        InventoryReportResponseDTO dto = new InventoryReportResponseDTO();
        dto.setId(Entity.getId());
        dto.setProductName(Entity.getProductName());
        dto.setCurrentPrice(Entity.getCurrentPrice());
        dto.setTotalEntries(Entity.getTotalEntries());
        dto.setTotalExits(Entity.getTotalExits());
        dto.setStockAvailable(Entity.getStockAvailable());

        return dto;
    }

    @Override
    public byte[] exportExcel(){
        List<InventoryReportResponseDTO> data = this.getAll();
        String[] headers = {"Id", "Nombre del producto", "Precio actual", "Total Entradas", "Total Salidas", "Inventario disponible"};

        List<Map<String, Object>> rows = new ArrayList<>();
        for (InventoryReportResponseDTO dto : data) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", dto.getId());
            row.put("name", dto.getProductName());
            row.put("price", dto.getCurrentPrice());
            row.put("entries", dto.getTotalEntries());
            row.put("exits", dto.getTotalExits());
            row.put("available", dto.getStockAvailable());
            rows.add(row);
        }

        return ExportDocUtil.toExcel("Reporte de inventario disponible", headers, rows);
    }

    @Override
    public byte[] exportPdf(){
        List<InventoryReportResponseDTO> data = this.getAll();
        String[] headers = {"Id", "Nombre del producto", "Precio actual", "Total Entradas", "Total Salidas", "Inventario disponible"};

        List<List<String>> rows = new ArrayList<>();
        for (InventoryReportResponseDTO dto : data) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(dto.getId()));
            row.add(dto.getProductName());
            row.add("$" + dto.getCurrentPrice());
            row.add(String.valueOf(dto.getTotalEntries()));
            row.add(String.valueOf(dto.getTotalExits()));
            row.add(String.valueOf(dto.getStockAvailable()));
            rows.add(row);
        }

        return ExportDocUtil.toPdf("Reporte de inventario disponible", headers, null, rows);
    }
}
