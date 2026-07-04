package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.dto.response.SuppliersReportResponseDTO;
import com.group1.proyect.freshbasket.service.SuppliersReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers-report")
@RequiredArgsConstructor
@Tag(name = "Reporte de proveedores", description = "Controlador exclusivo de solo lectura para reportes de proveedores")
public class SuppliersReportController {

    private final SuppliersReportService suppliersReportService;

    @GetMapping
    @Operation(summary = "Obtener todos los registros de los proveedores")
    public ResponseEntity<List<SuppliersReportResponseDTO>> getAll() {
        return ResponseEntity.ok(suppliersReportService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un registro especifico de proveedores por su Id")
    public ResponseEntity<SuppliersReportResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(suppliersReportService.getById(id));
    }

    @GetMapping("/search/supplier-name")
    @Operation(summary = "Filtrar registros de proveedores por coincidencia en el nombre")
    public ResponseEntity<List<SuppliersReportResponseDTO>> getBySupplierName(@RequestParam String supplierName) {
        return ResponseEntity.ok(suppliersReportService.getBySupplierName(supplierName));
    }

    @GetMapping("/search/supplier-country")
    @Operation(summary = "Filtrar registros de proveedores por Pais de origen")
    public ResponseEntity<List<SuppliersReportResponseDTO>> getByCountry(@RequestParam String country) {
        return ResponseEntity.ok(suppliersReportService.getByCountry(country));
    }

    @GetMapping("/search/total-products")
    @Operation(summary = "Filtrar registros por el numero de inventario perdido o dañado")
    public ResponseEntity<List<SuppliersReportResponseDTO>> getByTotalProducts(@RequestParam String totalProducts) {
        return ResponseEntity.ok(suppliersReportService.getByTotalProducts(totalProducts));
    }

    @GetMapping("/export/excel")
    @Operation(summary = "Exportar el reporte completo de proveedores a formato Excel (.xlsx)")
    public ResponseEntity<byte[]> exportToExcel() {
        byte[] reportBytes = suppliersReportService.exportExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Proveedores.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(reportBytes);
    }

    @GetMapping("/export/pdf")
    @Operation(summary = "Exportar el reporte completo de proveedores a formato PDF")
    public ResponseEntity<byte[]> exportToPdf() {
        byte[] reportBytes = suppliersReportService.exportPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Proveedores.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(reportBytes);
    }
}
