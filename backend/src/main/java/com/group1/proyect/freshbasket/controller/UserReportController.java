package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.dto.response.UserReportResponseDTO;
import com.group1.proyect.freshbasket.service.UserReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user-report")
@RequiredArgsConstructor
@Tag(name = "Reporte de Usuarios", description = "Controlador exclusivo de solo lectura para reportes de usuarios")
public class UserReportController {

    private final UserReportService userReportService;

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna el listado completo de registros de usuarios sin filtros.")
    public ResponseEntity<List<UserReportResponseDTO>> getAll() {
        return ResponseEntity.ok(userReportService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener registros específicos de usuarios", description = "Retorna el listado completo de registros de usuarios sin filtros.")
    public ResponseEntity<UserReportResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userReportService.getById(id));
    }

    @GetMapping("/search/user-name")
    @Operation(summary = "Filtrar registros de usuarios por coincidencia en el nombre")
    public ResponseEntity<List<UserReportResponseDTO>> getByFullName(
            @RequestParam String fullName) {
        return ResponseEntity.ok(userReportService.getByFullName(fullName));
    }

    @GetMapping("/search/user-role")
    @Operation(summary = "Filtrar registros de usuarios por el rol")
    public ResponseEntity<List<UserReportResponseDTO>> getByRole(
            @RequestParam String role) {
        return ResponseEntity.ok(userReportService.getByRole(role));
    }


    @GetMapping("/search/country-name")
    @Operation(summary = "Filtrar registros del país de origen de los usuarios")
    public ResponseEntity<List<UserReportResponseDTO>> getByCountryName(
            @RequestParam String countryName) {
        return ResponseEntity.ok(userReportService.getByCountryName(countryName));
    }

    @GetMapping("/search/user-state")
    @Operation(summary = "Filtrar registros de usuarios por coincidencia en el nombre")
    public ResponseEntity<List<UserReportResponseDTO>> getByEstado(
            @RequestParam String estado) {
        return ResponseEntity.ok(userReportService.getByEstado(estado));
    }

    @GetMapping("/export/excel")
    @Operation(summary = "Exportar el reporte completo de usuarios a formato Excel (.xlsx)")
    public ResponseEntity<byte[]> exportToExcel() {
        byte[] reportBytes = userReportService.exportExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_de_usuarios.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(reportBytes);
    }

    @GetMapping("/export/pdf")
    @Operation(summary = "Exportar el reporte completo de usuarios a formato PDF")
    public ResponseEntity<byte[]> exportToPdf() {
        byte[] reportBytes = userReportService.exportPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_de_usuarios.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(reportBytes);
    }
}
