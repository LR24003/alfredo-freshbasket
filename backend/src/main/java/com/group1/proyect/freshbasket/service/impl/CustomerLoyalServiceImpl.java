package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.response.CustomerLoyalResponseDTO;
import com.group1.proyect.freshbasket.entity.CustomerLoyal;
import com.group1.proyect.freshbasket.repository.CustomerLoyalRepository;
import com.group1.proyect.freshbasket.service.CustomerLoyalService;
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
public class CustomerLoyalServiceImpl implements CustomerLoyalService {

    private final CustomerLoyalRepository costumerLoyalRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerLoyalResponseDTO> getAll() {
        return costumerLoyalRepository.findAll()
            .stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
        }

    @Override
    @Transactional(readOnly = true)
    public CustomerLoyalResponseDTO getById(Long id) {
        return costumerLoyalRepository.findById(id)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Registro de compras del cliente no encontrado con ese ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerLoyalResponseDTO> getByCustomerName(String customerName) {
        return costumerLoyalRepository.findByCustomerNameContainingIgnoreCase(customerName)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<CustomerLoyalResponseDTO> getByTotalPurchases(String totalPurchasesRange) {
        int min = 0;
        int max = Integer.MAX_VALUE;

        if (totalPurchasesRange != null && !totalPurchasesRange.trim().isEmpty()) {
            String range = totalPurchasesRange.trim();

            if (range.contains(" a ")) {
                String[] parts = range.split(" a ");
                try {
                    min = Integer.parseInt(parts[0].trim());
                    max = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Formato de rango inválido: " + totalPurchasesRange);
                }
            } else if (range.endsWith("+")) {
                try {
                    min = Integer.parseInt(range.replace("+", "").trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Formato de rango inválido: " + totalPurchasesRange);
                }
            } else {
                try {
                    min = Integer.parseInt(range);
                    max = min;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Formato de rango inválido: " + totalPurchasesRange);
                }
            }
        }

        return costumerLoyalRepository.findByTotalPurchasesBetween(min, max)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private CustomerLoyalResponseDTO convertToResponseDto(CustomerLoyal entity) {
        CustomerLoyalResponseDTO dto = new  CustomerLoyalResponseDTO();
        dto.setId(entity.getId());
        dto.setCustomerName(entity.getCustomerName());
        dto.setCustomerEmail(entity.getCustomerEmail());
        dto.setTotalPurchases(entity.getTotalPurchases());
        dto.setTotalSpent(entity.getTotalSpent());

        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] exportExcel() {
        List<CustomerLoyalResponseDTO> data = this.getAll();
        String[] headers = {"ID", "Nombre Cliente", "Correo", "Total Compras", "Total Gastado"};

        List<Map<String, Object>> rows = new ArrayList<>();
        for (CustomerLoyalResponseDTO dto : data) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", dto.getId());
            row.put("name", dto.getCustomerName());
            row.put("email", dto.getCustomerEmail());
            row.put("purchases", dto.getTotalPurchases());
            row.put("spent", dto.getTotalSpent());
            rows.add(row);
        }

        return ExportDocUtil.toExcel("Clientes Fieles", headers, rows);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportPdf() {
        List<CustomerLoyalResponseDTO> data = this.getAll();
        String[] headers = {"ID", "Nombre Cliente", "Correo", "Total Compras", "Total Gastado"};

        List<List<String>> rows = new ArrayList<>();
        for (CustomerLoyalResponseDTO dto : data) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(dto.getId()));
            row.add(dto.getCustomerName());
            row.add(dto.getCustomerEmail());
            row.add(String.valueOf(dto.getTotalPurchases()));
            row.add("$" + dto.getTotalSpent());
            rows.add(row);
        }

        return ExportDocUtil.toPdf("Reporte de fidelización de clientes", headers, null, rows);
    }


}
