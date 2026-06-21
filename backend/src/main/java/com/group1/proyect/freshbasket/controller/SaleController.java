package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.config.JwtUtil;
import com.group1.proyect.freshbasket.dto.request.SaleRequestDTO;
import com.group1.proyect.freshbasket.dto.response.SaleResponseDTO;
import com.group1.proyect.freshbasket.dto.response.SaleDetailsResponseDTO; // Importado
import com.group1.proyect.freshbasket.entity.Sale;
import com.group1.proyect.freshbasket.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@Tag(name = "Sales", description = "API optimizada para la gestión de ventas de FreshBasket")
public class SaleController extends GenericController<Sale, SaleRequestDTO, SaleResponseDTO, Long> {

    private final SaleService saleService;
    private final JwtUtil jwtUtil;

    public SaleController(SaleService saleService, JwtUtil jwtUtil) {
        super(saleService, "Venta");
        this.saleService = saleService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
            summary = "Buscar ventas por su estatus",
            description = "Retorna ventas que coincidan con el status especificado (COMPLETADA, PENDIENTE o CANCELADA)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ventas encontradas con éxito"),
            @ApiResponse(responseCode = "404", description = "No se encontraron coincidencias")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SaleResponseDTO>> getSalesByStatus(@PathVariable String status) {
        List<SaleResponseDTO> sales = saleService.getSalesByStatus(status.toUpperCase());
        return ResponseEntity.ok(sales);
    }

    @Operation(
            summary = "Buscar compras realizadas por el cliente autenticado",
            description = "Retorna únicamente las compras realizadas por el cliente a través de su JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compras encontradas con éxito"),
            @ApiResponse(responseCode = "404", description = "No se encontraron coincidencias")
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles de la venta devueltos con éxito"),
            @ApiResponse(responseCode = "404", description = "La venta no existe o no tiene detalles asociados")
    })
    @GetMapping("/{saleId}/details")
    public ResponseEntity<List<SaleDetailsResponseDTO>> getDetailsBySaleId(@PathVariable Long saleId) {
        List<SaleDetailsResponseDTO> details = saleService.getDetailsBySaleId(saleId);
        return ResponseEntity.ok(details);
    }
}