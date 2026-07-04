package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.config.JwtUtil;
import com.group1.proyect.freshbasket.dto.request.DteControlRequestDTO;
import com.group1.proyect.freshbasket.dto.request.SaleRequestDTO;
import com.group1.proyect.freshbasket.dto.response.DteControlResponseDTO;
import com.group1.proyect.freshbasket.dto.response.SaleResponseDTO;
import com.group1.proyect.freshbasket.dto.response.SaleDetailsResponseDTO;
import com.group1.proyect.freshbasket.entity.Sale;
import com.group1.proyect.freshbasket.service.DteControlService;
import com.group1.proyect.freshbasket.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@Tag(name = "Sales", description = "API optimizada para la gestión de ventas de FreshBasket")
public class SaleController extends GenericController<Sale, SaleRequestDTO, SaleResponseDTO, Long> {

    private final SaleService saleService;
    private final DteControlService dteControlService;
    private final JwtUtil jwtUtil;

    public SaleController(SaleService saleService, DteControlService dteControlService, JwtUtil jwtUtil) {
        super(saleService, "Venta");
        this.saleService = saleService;
        this.dteControlService = dteControlService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
            summary = "Buscar ventas por su estatus",
            description = "Retorna ventas que coincidan con el status especificado (COMPLETADA, FACTURADA, PENDIENTE o CANCELADA)"
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SaleResponseDTO>> getSalesByStatus(@PathVariable String status) {
        List<SaleResponseDTO> sales = saleService.getSalesByStatus(status.toUpperCase());
        return ResponseEntity.ok(sales);
    }

    @Operation(
            summary = "Buscar compras realizadas por el cliente autenticado",
            description = "Retorna únicamente las compras realizadas por el cliente a través de su JWT token"
    )
    @GetMapping("/my-purchases")
    public ResponseEntity<List<SaleResponseDTO>> getMyPurchases(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new RuntimeException("Token de autenticación faltante o inválido");
        }

        String token = bearerToken.substring(7);
        Long customerId = jwtUtil.extractUserId(token);

        List<SaleResponseDTO> myPurchases = saleService.getSalesByCustomerId(customerId);
        return ResponseEntity.ok(myPurchases);
    }

    @Operation(
            summary = "Obtener los productos detallados de una venta específica",
            description = "Retorna la lista de artículos del carrito de compras vinculados a una venta."
    )
    @GetMapping("/{saleId}/details")
    public ResponseEntity<List<SaleDetailsResponseDTO>> getDetailsBySaleId(@PathVariable Long saleId) {
        List<SaleDetailsResponseDTO> details = saleService.getDetailsBySaleId(saleId);
        return ResponseEntity.ok(details);
    }

    @Operation(
            summary = "Buscar ventas dentro de un rango de fechas",
            description = "Retorna el historial transaccional filtrado por fechas en formato ISO (yyyy-MM-ddTHH:mm:ss)"
    )
    @GetMapping("/by-date-range")
    public ResponseEntity<List<SaleResponseDTO>> getSalesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<SaleResponseDTO> sales = saleService.getSalesByDateRange(start, end);
        return ResponseEntity.ok(sales);
    }

    @Operation(
            summary = "Obtener el total monetario de ventas del día actual",
            description = "Suma los montos de todas las órdenes activas ejecutadas hoy desde las 00:00 horas"
    )
    @GetMapping("/daily-total")
    public ResponseEntity<BigDecimal> getDailyTotalSales() {
        return ResponseEntity.ok(saleService.getDailyTotalSales());
    }

    @Operation(
            summary = "Emitir Documento Tributario Electrónico (DTE)",
            description = "Genera la factura electrónica simulada ante el Ministerio de Hacienda para una venta, cambiando su estado a FACTURADA."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "DTE Emitido y firmado exitosamente"),
            @ApiResponse(responseCode = "400", description = "La venta ya posee DTE o los datos son inválidos")
    })
    @PostMapping("/{saleId}/emitir-dte")
    public ResponseEntity<DteControlResponseDTO> emitirDte(
            @PathVariable Long saleId,
            @Valid @RequestBody DteControlRequestDTO requestDto) {

        requestDto.setSaleId(saleId);

        DteControlResponseDTO dteResponse = dteControlService.emitirDte(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dteResponse);
    }
}