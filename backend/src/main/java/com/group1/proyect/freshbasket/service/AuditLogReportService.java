package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.response.AuditLogReportResponseDTO;
import java.util.List;

public interface AuditLogReportService {

    List<AuditLogReportResponseDTO> getAll();
    AuditLogReportResponseDTO getById(Long id);
    List<AuditLogReportResponseDTO> getByUserName(String userName);
    List<AuditLogReportResponseDTO> getByAction(String action);

    byte[] exportExcel();
    byte[] exportPdf();
}