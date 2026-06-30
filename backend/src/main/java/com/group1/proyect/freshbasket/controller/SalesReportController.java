package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.dto.response.SalesReportResponseDTO;
import com.group1.proyect.freshbasket.service.SalesReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sales-report")
@RequiredArgsConstructor
@Tag(name = "Reporte de Ventas", description = "Controlador exclusivo de solo lectura para reportes de ventas")
public class SalesReportController {

    private final SalesReportService salesReportService;

    @GetMapping
    @Operation(summary = "Obtener todas las ventas", description = "Retorna el listado completo de registros de ventas sin filtros.")
    public ResponseEntity<List<SalesReportResponseDTO>> getAll() {
        return ResponseEntity.ok(salesReportService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una venta por ID", description = "Busca un registro de venta específico mediante su ID.")
    public ResponseEntity<SalesReportResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(salesReportService.getById(id));
    }

    @GetMapping("/search/employee-name")
    @Operation(summary = "Filtrar registros de ventas por coincidencia en el nombre del empleado")
    public ResponseEntity<List<SalesReportResponseDTO>> getByProductName(
            @RequestParam String employeeName) {
        return ResponseEntity.ok(salesReportService.getByEmployeeName(employeeName));
    }

    @GetMapping("/filter-sales")
    @Operation(summary = "Filtrar ventas por Día, Mes y/o Método de Pago", description = "Permite realizar búsquedas dinámicas.")
    public ResponseEntity<List<SalesReportResponseDTO>> getFilteredSales(
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String paymentMethod) {
        List<SalesReportResponseDTO> sales = salesReportService.getFilteredSales(day, month, paymentMethod);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/filter-by-range")
    @Operation(summary = "Filtrar ventas por rango de fechas", description = "Filtra con precisión usando objetos de fecha y hora completos.")
    public ResponseEntity<List<SalesReportResponseDTO>> getFilteredSalesByDateRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String paymentMethod) {
        List<SalesReportResponseDTO> sales = salesReportService.getFilteredSalesByDateRange(startDate, endDate, paymentMethod);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/export/excel")
    @Operation(summary = "Exportar el reporte completo de ventas a formato Excel (.xlsx)")
    public ResponseEntity<byte[]> exportToExcel() {
        byte[] reportBytes = salesReportService.exportExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_de_ventas.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(reportBytes);
    }

    @GetMapping("/export/pdf")
    @Operation(summary = "Exportar el reporte completo de ventas a formato PDF")
    public ResponseEntity<byte[]> exportToPdf() {
        byte[] reportBytes = salesReportService.exportPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_de_ventas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(reportBytes);
    }
}