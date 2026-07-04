package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.response.SuppliersReportResponseDTO;
import com.group1.proyect.freshbasket.entity.SuppliersReport;
import com.group1.proyect.freshbasket.repository.SuppliersReportRepository;
import com.group1.proyect.freshbasket.service.SuppliersReportService;
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
public class SuppliersReportServiceImpl implements SuppliersReportService {

    private final SuppliersReportRepository suppliersReportRepository;

    @Override
    public List<SuppliersReportResponseDTO> getAll() {
        return suppliersReportRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public SuppliersReportResponseDTO getById(Long id){
        return suppliersReportRepository.findById(id)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Registro de proveedor no encontrado con ese Id: " + id));
    }

    public List<SuppliersReportResponseDTO> getBySupplierName(String supplierName){
        return suppliersReportRepository.findBySupplierNameContainingIgnoreCase(supplierName)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

    }

    public List<SuppliersReportResponseDTO> getByCountry(String country){
        return suppliersReportRepository.findByCountryContainingIgnoreCase(country)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<SuppliersReportResponseDTO> getByTotalProducts(String totalProductsRange){
        int min = 0;
        int max = Integer.MAX_VALUE;

        if (totalProductsRange != null && !totalProductsRange.trim().isEmpty()) {
            String range = totalProductsRange.trim();

            if (range.contains(" a ")) {
                String[] parts = range.split(" a ");
                try {
                    min = Integer.parseInt(parts[0].trim());
                    max = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Formato de rango inválido: " + totalProductsRange);
                }
            } else if (range.endsWith("+")) {
                try {
                    min = Integer.parseInt(range.replace("+", "").trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Formato de rango inválido: " + totalProductsRange);
                }
            } else {
                try {
                    min = Integer.parseInt(range);
                    max = min;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Formato de rango inválido: " + totalProductsRange);
                }
            }
        }

        return suppliersReportRepository.findByTotalProductsBetween(min, max)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private SuppliersReportResponseDTO convertToResponseDto(SuppliersReport entity) {
        SuppliersReportResponseDTO dto = new SuppliersReportResponseDTO();
        dto.setId(entity.getId());
        dto.setSupplierName(entity.getSupplierName());
        dto.setCountry(entity.getCountry());
        dto.setMainProduct(entity.getMainProduct());
        dto.setSuppliedVolume(entity.getSuppliedVolume());
        dto.setTotalProducts(entity.getTotalProducts());
        dto.setTotalStock(entity.getTotalStock());
        dto.setTotalPurchased(entity.getTotalPurchased());

        return dto;
    }

    @Override
    public byte[] exportExcel(){
        List<SuppliersReportResponseDTO> data = this.getAll();
        String[] headers = {"Id", "Nombre", "Pais", "Producto principal", "Volumen", "Productos catalogo", "Inventario actual", "Total($)"};

        List<Map<String, Object>> rows = new ArrayList<>();
        for (SuppliersReportResponseDTO dto : data) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", dto.getId());
            row.put("name", dto.getSupplierName());
            row.put("country", dto.getCountry());
            row.put("main", dto.getMainProduct());
            row.put("volume", dto.getSuppliedVolume());
            row.put("catalog", dto.getTotalProducts());
            row.put("stock", dto.getTotalStock());
            row.put("purchased", dto.getTotalPurchased());
            rows.add(row);
        }

        return ExportDocUtil.toExcel("Reporte de proveedores y productos principales", headers, rows);
    }

    @Override
    public byte[] exportPdf(){
        List<SuppliersReportResponseDTO> data = this.getAll();
        String[] headers = {"Id", "Nombre", "Pais", "Producto principal", "Volumen", "Productos catalogo", "Inventario actual", "Total($)"};

        List<List<String>> rows = new ArrayList<>();
        for (SuppliersReportResponseDTO dto : data) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(dto.getId()));
            row.add(String.valueOf(dto.getSupplierName()));
            row.add(String.valueOf(dto.getCountry()));
            row.add(String.valueOf(dto.getMainProduct()));
            row.add(dto.getSuppliedVolume());
            row.add(String.valueOf(dto.getTotalProducts()));
            row.add(String.valueOf(dto.getTotalStock()));
            row.add("$" + dto.getTotalPurchased());
            rows.add(row);
        }

        return ExportDocUtil.toPdf("Reporte de proveedores y productos principales", headers, null, rows);
    }
}
