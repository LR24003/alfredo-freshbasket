package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.response.AuditLogReportResponseDTO;
import com.group1.proyect.freshbasket.entity.AuditLogReport;
import com.group1.proyect.freshbasket.repository.AuditLogReportRepository;
import com.group1.proyect.freshbasket.service.AuditLogReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
}