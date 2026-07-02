package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.response.SalesReportResponseDTO;
import com.group1.proyect.freshbasket.entity.SalesReport;
import com.group1.proyect.freshbasket.repository.SalesReportRepository;
import com.group1.proyect.freshbasket.service.SalesReportService;
import com.group1.proyect.freshbasket.utils.ExportDocUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesReportServiceImpl implements SalesReportService {

    private final SalesReportRepository salesReportRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<SalesReportResponseDTO> getAll(){
        return salesReportRepository.findAll()
                .stream()
            .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public SalesReportResponseDTO getById(Long id){
        return salesReportRepository.findById(id)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Registro de ventas no encontrado con ese Id: " + id));
    }

    public List<SalesReportResponseDTO> getByEmployeeName(String employeeName) {
        return salesReportRepository.findByEmployeeNameContainingIgnoreCase(employeeName)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SalesReportResponseDTO> getFilteredSales(Integer day, Integer month, String paymentMethod) {
        if (day != null && (day < 1 || day > 31)) {
            throw new IllegalArgumentException("El día debe estar entre 1 y 31");
        }
        if (month != null && (month < 1 || month > 12)) {
            throw new IllegalArgumentException("El mes debe estar entre 1 y 12");
        }

        String cleanPaymentMethod = sanitizeString(paymentMethod);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SalesReport> cq = cb.createQuery(SalesReport.class);
        Root<SalesReport> root = cq.from(SalesReport.class);

        List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

        // Usar date_part en lugar de DAY/MONTH
        if (day != null) {
            predicates.add(cb.equal(
                    cb.function("date_part", Integer.class, cb.literal("day"), root.get("saleDate")),
                    day
            ));
        }

        if (month != null) {
            predicates.add(cb.equal(
                    cb.function("date_part", Integer.class, cb.literal("month"), root.get("saleDate")),
                    month
            ));
        }

        if (cleanPaymentMethod != null && !cleanPaymentMethod.isEmpty()) {
            predicates.add(cb.like(
                    cb.lower(root.get("paymentMethod")),
                    "%" + cleanPaymentMethod.toLowerCase() + "%"
            ));
        }

        cq.where(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        TypedQuery<SalesReport> query = entityManager.createQuery(cq);

        return query.getResultList()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SalesReportResponseDTO> getFilteredSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate, String paymentMethod) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        String cleanPaymentMethod = sanitizeString(paymentMethod);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SalesReport> cq = cb.createQuery(SalesReport.class);
        Root<SalesReport> root = cq.from(SalesReport.class);

        List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("saleDate"), startDate));
        }

        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("saleDate"), endDate));
        }

        if (cleanPaymentMethod != null && !cleanPaymentMethod.isEmpty()) {
            predicates.add(cb.like(
                    cb.lower(root.get("paymentMethod")),
                    "%" + cleanPaymentMethod.toLowerCase() + "%"
            ));
        }

        cq.where(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        TypedQuery<SalesReport> query = entityManager.createQuery(cq);

        return query.getResultList()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }


    private String sanitizeString(String input) {
        return (input != null && !input.trim().isEmpty()) ? input.trim() : null;
    }


    private SalesReportResponseDTO convertToResponseDto(SalesReport Entity){
        SalesReportResponseDTO dto = new SalesReportResponseDTO();
        dto.setId(Entity.getId());
        dto.setSaleDate(Entity.getSaleDate());
        dto.setTotalAmount(Entity.getTotalAmount());
        dto.setPaymentMethod(Entity.getPaymentMethod());
        dto.setStatus(Entity.getStatus());
        dto.setEmployeeName(Entity.getEmployeeName());
        dto.setEmployeeEmail(Entity.getEmployeeEmail());

        return dto;
    }

    @Override
    public byte[] exportExcel(){
        List<SalesReportResponseDTO> data = this.getAll();
        String[] headers = {"Id", "Fecha venta", "Precio total", "Método pago", "Estado", "Nombre empleado", "Correo"};

        List<Map<String, Object>> rows = new ArrayList<>();
        for (SalesReportResponseDTO dto : data) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", dto.getId());
            row.put("date", dto.getSaleDate());
            row.put("total", dto.getTotalAmount());
            row.put("method", dto.getPaymentMethod());
            row.put("status", dto.getStatus());
            row.put("name", dto.getEmployeeName());
            row.put("email", dto.getEmployeeEmail());
            rows.add(row);
        }

        return ExportDocUtil.toExcel("Reporte de ventas", headers, rows);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportPdf() {
        List<SalesReportResponseDTO> data = this.getAll();
        String[] headers = {"Id", "Fecha venta", "Precio total", "Método pago", "Estado", "Nombre empleado", "Correo"};

        List<List<String>> rows = new ArrayList<>();
        for (SalesReportResponseDTO dto : data) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(dto.getId()));
            row.add(String.valueOf(dto.getSaleDate()));
            row.add("$" + dto.getTotalAmount());
            row.add(String.valueOf(dto.getPaymentMethod()));
            row.add(String.valueOf(dto.getStatus()));
            row.add(String.valueOf(dto.getEmployeeName()));
            row.add(String.valueOf(dto.getEmployeeEmail()));
            rows.add(row);
        }

        return ExportDocUtil.toPdf("Reporte de ventas", headers, null, rows);
    }
}
