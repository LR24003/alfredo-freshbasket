package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.response.ProductsSoldResponseDTO;
import com.group1.proyect.freshbasket.entity.ProductSoldReport;
import com.group1.proyect.freshbasket.repository.ProductsSoldRepository;
import com.group1.proyect.freshbasket.service.ProductSoldService;
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
public class ProductSoldServiceImpl implements ProductSoldService {

    private final ProductsSoldRepository productsSoldRepository;

    @Override
    public List<ProductsSoldResponseDTO> getAll(){
        return productsSoldRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductsSoldResponseDTO getById(Long id){
        return productsSoldRepository.findById(id)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Registro de producto mas vendido no encontrado con ese Id: " + id));
    }

    @Override
    public List<ProductsSoldResponseDTO> getByProductName(String productName) {
        return productsSoldRepository.findByProductNameContainingIgnoreCase(productName)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductsSoldResponseDTO> getByUnitsSold(String unitsSoldRange){
        int min = 0;
        int max = Integer.MAX_VALUE;

        if(unitsSoldRange != null && !unitsSoldRange.trim().isEmpty()){
            String range = unitsSoldRange.trim();

            if (range.contains(" a ")) {
                String[] parts = range.split(" a ");
                try {
                    min = Integer.parseInt(parts[0].trim());
                    max = Integer.parseInt(parts[1].trim());
                }catch (NumberFormatException e){
                    throw new RuntimeException("Formato de rango invalido: " + unitsSoldRange);
                }
            }else if (range.endsWith("+")){
                try {
                    min = Integer.parseInt(range.replace("+", "").trim());
                } catch (NumberFormatException e){
                    throw new RuntimeException("Formato de rango invalido: " + unitsSoldRange);
                }
            } else {
                try {
                    min = Integer.parseInt(range);
                    max = min;
                }catch (NumberFormatException e){
                    throw new RuntimeException("Formato de rango invalido: " + unitsSoldRange);
                }
            }
        }

        return productsSoldRepository.findByUnitsSoldBetween(min, max)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private ProductsSoldResponseDTO convertToResponseDto(ProductSoldReport Entity){
        ProductsSoldResponseDTO dto = new ProductsSoldResponseDTO();
        dto.setId(Entity.getId());
        dto.setProductName(Entity.getProductName());
        dto.setUnitPrice(Entity.getUnitPrice());
        dto.setUnitsSold(Entity.getUnitsSold());
        dto.setTotalRevenue(Entity.getTotalRevenue());

        return dto;
    }

    @Override
    public byte[] exportExcel(){
        List<ProductsSoldResponseDTO> data = this.getAll();
        String[] headers = {"Id", "Nombre del producto", "Precio unitario", "Unidades vendidas","Precio total"};

        List<Map<String, Object>> rows = new ArrayList<>();
        for (ProductsSoldResponseDTO dto : data) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", dto.getId());
            row.put("name", dto.getProductName());
            row.put("price", dto.getUnitPrice());
            row.put("units", dto.getUnitsSold());
            row.put("revenues", dto.getTotalRevenue());
            rows.add(row);
        }

        return ExportDocUtil.toExcel("Reporte de productos mas vendidos", headers, rows);
    }

    @Override
    public byte[] exportPdf(){
        List<ProductsSoldResponseDTO> data = this.getAll();
        String[] headers = {"Id", "Nombre del producto", "Precio unitario", "Unidades vendidas","Precio total"};

        List<List<String>> rows = new ArrayList<>();
        for (ProductsSoldResponseDTO dto : data) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(dto.getId()));
            row.add(dto.getProductName());
            row.add("$" + dto.getUnitPrice());
            row.add(String.valueOf(dto.getUnitsSold()));
            row.add("$" + dto.getTotalRevenue());
            rows.add(row);
        }

        return ExportDocUtil.toPdf("Reporte de productos mas vendidos", headers, null, rows);
    }

}
