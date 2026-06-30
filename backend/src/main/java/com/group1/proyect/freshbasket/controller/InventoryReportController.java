package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.dto.response.InventoryReportResponseDTO;
import com.group1.proyect.freshbasket.service.InventoryReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-report")
@RequiredArgsConstructor
@Tag(name = "InventoryReport", description = "Controlador exclusivo de solo lectura para reportes de inventario")
public class InventoryReportController {

    private final InventoryReportService inventoryReportService;

    @GetMapping
    @Operation(summary = "Obtener todos los registros del inventario disponible")
    public ResponseEntity<List<InventoryReportResponseDTO>> getAll() {
        return ResponseEntity.ok(inventoryReportService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un registro especifico del inventario por su Id")
    public ResponseEntity<InventoryReportResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryReportService.getById(id));
    }

    @GetMapping("/search/productname")
    @Operation(summary = "Filtrar registros del inventario por coincidencia en el nombre del producto")
    public ResponseEntity<List<InventoryReportResponseDTO>> getByProductName(@RequestParam String productName) {
        return ResponseEntity.ok(inventoryReportService.getByProductName(productName));
    }

    @GetMapping("/search/stockavailable")
    @Operation(summary = "Filtrar registros por el numero de inventario disponible")
    public ResponseEntity<List<InventoryReportResponseDTO>> getByStockAvailable(@RequestParam String stockAvailable) {
        return ResponseEntity.ok(inventoryReportService.getByStockAvailable(stockAvailable));
    }

    @GetMapping("/export/excel")
    @Operation(summary = "Exportar el reporte completo de inventario a formato Excel (.xlsx)")
    public ResponseEntity<byte[]> exportToExcel() {
        byte[] reportBytes = inventoryReportService.exportExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Inventario.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(reportBytes);
    }

    @GetMapping("/export/pdf")
    @Operation(summary = "Exportar el reporte completo de inventario a formato PDF")
    public ResponseEntity<byte[]> exportToPdf() {
        byte[] reportBytes = inventoryReportService.exportPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Inventario.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(reportBytes);
    }
}
