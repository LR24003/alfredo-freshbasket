package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.response.AuditLogReportResponseDTO;
import com.group1.proyect.freshbasket.entity.AuditLogReport;
import com.group1.proyect.freshbasket.repository.AuditLogReportRepository;
import com.group1.proyect.freshbasket.service.AuditLogReportService;
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
public class AuditLogReportServiceImpl implements AuditLogReportService {

    private final AuditLogReportRepository auditLogReportRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogReportResponseDTO> getAll() {
        return auditLogReportRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogReportResponseDTO getById(Long id) {
        return auditLogReportRepository.findById(id)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Registro de auditoría no encontrado con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogReportResponseDTO> getByUserName(String userName) {
        return auditLogReportRepository.findByUserNameContainingIgnoreCase(userName)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogReportResponseDTO> getByAction(String action) {
        return auditLogReportRepository.findByActionIgnoreCase(action)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private AuditLogReportResponseDTO convertToResponseDto(AuditLogReport entity) {
        AuditLogReportResponseDTO dto = new AuditLogReportResponseDTO();
        dto.setId(entity.getId());
        dto.setEntity(entity.getEntity());
        dto.setEntityId(entity.getEntityId());
        dto.setUserName(entity.getUserName());
        dto.setAction(entity.getAction());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportExcel() {
        List<AuditLogReportResponseDTO> data = this.getAll();
        String[] headers = {"ID", "Entidad", "ID Entidad", "Usuario", "Acción", "Fecha/hora"};

        List<Map<String, Object>> rows = new ArrayList<>();
        for (AuditLogReportResponseDTO dto : data) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", dto.getId());
            row.put("entity", dto.getEntity());
            row.put("entityId", dto.getEntityId());
            row.put("userName", dto.getUserName());
            row.put("action", dto.getAction());
            row.put("createdAt", dto.getCreatedAt() != null ? dto.getCreatedAt().toString() : "");
            rows.add(row);
        }

        return ExportDocUtil.toExcel("Reporte de Auditoría", headers, rows);
    }


    @Override
    @Transactional(readOnly = true)
    public byte[] exportPdf() {
        List<AuditLogReportResponseDTO> data = this.getAll();
        String[] headers = {"ID", "Entidad", "ID Entidad", "Usuario", "Acción", "Fecha/Hora"};

        List<List<String>> rows = new ArrayList<>();
        for (AuditLogReportResponseDTO dto : data) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(dto.getId()));
            row.add(dto.getEntity());
            row.add(String.valueOf(dto.getEntityId()));
            row.add(dto.getUserName());
            row.add(dto.getAction());
            row.add(dto.getCreatedAt() != null ? dto.getCreatedAt().toString() : "");
            rows.add(row);
        }

        return ExportDocUtil.toPdf("Reporte de Auditoría de Sistemas", headers, null, rows);
    }
}