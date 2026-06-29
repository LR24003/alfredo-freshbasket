package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.dto.response.AuditLogReportResponseDTO;
import com.group1.proyect.freshbasket.service.AuditLogReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs-report")
@RequiredArgsConstructor
@Tag(name = "Auditlogs", description = "Controlador exclusivo de solo lectura para reportes de auditoría")
public class AuditLogReportController {

    private final AuditLogReportService auditLogReportService;

    @GetMapping
    @Operation(summary = "Obtener todos los registros de auditoría")
    public ResponseEntity<List<AuditLogReportResponseDTO>> getAll() {
        return ResponseEntity.ok(auditLogReportService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un registro de auditoría específico por su ID")
    public ResponseEntity<AuditLogReportResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(auditLogReportService.getById(id));
    }

    @GetMapping("/search/username")
    @Operation(summary = "Filtrar registros de auditoría por coincidencia en el nombre de usuario")
    public ResponseEntity<List<AuditLogReportResponseDTO>> getByUserName(@RequestParam String userName) {
        return ResponseEntity.ok(auditLogReportService.getByUserName(userName));
    }

    @GetMapping("/search/action")
    @Operation(summary = "Filtrar registros de auditoría por acción exacta (INSERT, UPDATE, DELETE)")
    public ResponseEntity<List<AuditLogReportResponseDTO>> getByAction(@RequestParam String action) {
        return ResponseEntity.ok(auditLogReportService.getByAction(action));
    }

    @GetMapping("/export/excel")
    @Operation(summary = "Exportar el reporte completo de auditoría a formato Excel (.xlsx)")
    public ResponseEntity<byte[]> exportToExcel() {
        byte[] reportBytes = auditLogReportService.exportExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Auditoria.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(reportBytes);
    }

    @GetMapping("/export/pdf")
    @Operation(summary = "Exportar el reporte completo de auditoría a formato PDF")
    public ResponseEntity<byte[]> exportToPdf() {
        byte[] reportBytes = auditLogReportService.exportPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Auditoria.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(reportBytes);
    }
}
