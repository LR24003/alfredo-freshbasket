package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.dto.response.ProductsSoldResponseDTO;
import com.group1.proyect.freshbasket.service.ProductSoldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products-sold-report")
@RequiredArgsConstructor
@Tag(name = "Productos mas Vendidos", description = "Controlador exclusivo de solo lectura para reportes de los productos mas vendidos")
public class ProductSoldController {

    private final ProductSoldService productSoldService;

    @GetMapping
    @Operation(summary = "Obtener todos los registros de los productos mas vendidos")
    public ResponseEntity<List<ProductsSoldResponseDTO>> getAll() {
        return ResponseEntity.ok(productSoldService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un registro especifico del inventario por su Id")
    public ResponseEntity<ProductsSoldResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productSoldService.getById(id));
    }

    @GetMapping("/search/product-name")
    @Operation(summary = "Filtrar registros de productos mas vendidos por coincidencia en el nombre del producto")
    public ResponseEntity<List<ProductsSoldResponseDTO>> getByProductName(@RequestParam String productName) {
        return ResponseEntity.ok(productSoldService.getByProductName(productName));
    }

    @GetMapping("/search/units-sold")
    @Operation(summary = "Filtrar registros por el numero de productos mas vendidos")
    public ResponseEntity<List<ProductsSoldResponseDTO>> getByUnitsSold(@RequestParam String unitsSold) {
        return ResponseEntity.ok(productSoldService.getByUnitsSold(unitsSold));
    }

    @GetMapping("/export/excel")
    @Operation(summary = "Exportar el reporte completo de productos mas vendidos a formato Excel (.xlsx)")
    public ResponseEntity<byte[]> exportToExcel() {
        byte[] reportBytes = productSoldService.exportExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_productos_mas_vendidos.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(reportBytes);
    }

    @GetMapping("/export/pdf")
    @Operation(summary = "Exportar el reporte completo de productos mas vendidos a formato PDF")
    public ResponseEntity<byte[]> exportToPdf() {
        byte[] reportBytes = productSoldService.exportPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_productos_mas_vendidos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(reportBytes);
    }
}
