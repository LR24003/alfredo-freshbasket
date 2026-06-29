package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.dto.response.CustomerLoyalResponseDTO;
import com.group1.proyect.freshbasket.service.CustomerLoyalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-loyal-report")
@RequiredArgsConstructor
@Tag(name = "CostumerLoyal", description = "Controlador exclusivo de solo lectura para reportes de compras de los clientes")
public class CustomerLoyalController {

    private final CustomerLoyalService customerLoyalService;

    @GetMapping
    @Operation(summary = "Obtener todos los registros de compras de los clientes")
    public ResponseEntity<List<CustomerLoyalResponseDTO>> getAll(){
        return ResponseEntity.ok(customerLoyalService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un registro de compra especifico de un cliente")
    public ResponseEntity<CustomerLoyalResponseDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(customerLoyalService.getById(id));
    }

    @GetMapping("/search/customername")
    @Operation(summary = "Filtrar registros de compras por coincidencia en el nombre del cliente")
    public ResponseEntity<List<CustomerLoyalResponseDTO>> getByCostumerName(@RequestParam String customerName) {
        return ResponseEntity.ok(customerLoyalService.getByCustomerName(customerName));
    }

    @GetMapping("/search/totalpurchases")
    @Operation(summary = "Filtrar registros por el numero de compras realizadas")
    public ResponseEntity<List<CustomerLoyalResponseDTO>> getByTotalPurchases(@RequestParam String totalPurchases) {
        return ResponseEntity.ok(customerLoyalService.getByTotalPurchases(totalPurchases));
    }

    @GetMapping("/export/excel")
    @Operation(summary = "Exportar el reporte completo de fidelización a formato Excel (.xlsx)")
    public ResponseEntity<byte[]> exportToExcel() {
        byte[] reportBytes = customerLoyalService.exportExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Clientes_Fieles.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(reportBytes);
    }

    @GetMapping("/export/pdf")
    @Operation(summary = "Exportar el reporte completo de fidelización a formato PDF")
    public ResponseEntity<byte[]> exportToPdf() {
        byte[] reportBytes = customerLoyalService.exportPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Clientes_Fieles.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(reportBytes);
    }
}
