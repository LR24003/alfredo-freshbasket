package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.dto.response.InventoryLostResponseDTO;
import com.group1.proyect.freshbasket.service.InventoryLostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-lost-report")
@RequiredArgsConstructor
@Tag(name = "Inventario perdido", description = "Controlador exclusivo de solo lectura para reportes de inventario perdido")
public class InventoryLostController {

    private final InventoryLostService inventoryLostService;

    @GetMapping
    @Operation(summary = "Obtener todos los registros del inventario perdido o dañado")
    public ResponseEntity<List<InventoryLostResponseDTO>> getAll() {
        return ResponseEntity.ok(inventoryLostService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un registro especifico del inventario perdido por su Id")
    public ResponseEntity<InventoryLostResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryLostService.getById(id));
    }

    @GetMapping("/search/product-name")
    @Operation(summary = "Filtrar registros del inventario perdido por coincidencia en el nombre del producto")
    public ResponseEntity<List<InventoryLostResponseDTO>> getByProductName(@RequestParam String productName) {
        return ResponseEntity.ok(inventoryLostService.getByProductName(productName));
    }

    @GetMapping("/search/exit-reason")
    @Operation(summary = "Filtrar registros del inventario perdido por la razón de la salida")
    public ResponseEntity<List<InventoryLostResponseDTO>> getByExitReason(@RequestParam String exitReason) {
        return ResponseEntity.ok(inventoryLostService.getByExitReason(exitReason));
    }

    @GetMapping("/search/units-lost")
    @Operation(summary = "Filtrar registros por el numero de inventario perdido o dañado")
    public ResponseEntity<List<InventoryLostResponseDTO>> getByUnitsLost(@RequestParam String unitsLost) {
        return ResponseEntity.ok(inventoryLostService.getByUnitsLost(unitsLost));
    }

    @GetMapping("/export/excel")
    @Operation(summary = "Exportar el reporte completo de inventario perdido a formato Excel (.xlsx)")
    public ResponseEntity<byte[]> exportToExcel() {
        byte[] reportBytes = inventoryLostService.exportExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_inventario_perdido.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(reportBytes);
    }

    @GetMapping("/export/pdf")
    @Operation(summary = "Exportar el reporte completo de inventario perdido a formato PDF")
    public ResponseEntity<byte[]> exportToPdf() {
        byte[] reportBytes = inventoryLostService.exportPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_inventario_perdido.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(reportBytes);
    }
}
