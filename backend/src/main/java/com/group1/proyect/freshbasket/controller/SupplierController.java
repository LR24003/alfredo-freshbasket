package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.dto.request.SupplierRequestDTO;
import com.group1.proyect.freshbasket.dto.response.SupplierResponseDTO;
import com.group1.proyect.freshbasket.entity.Supplier;
import com.group1.proyect.freshbasket.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@Tag(name = "Suppliers", description = "API optimizada para la gestión de proveedores de FreshBasket")
public class SupplierController extends GenericController<Supplier, SupplierRequestDTO, SupplierResponseDTO, Long> {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        super(supplierService, "proveedor");
        this.supplierService = supplierService;
    }

    @Operation(
            summary = "Buscar proveedores por nombre",
            description = "Retorna proveedores que coincidan con el nombre especificado (búsqueda parcial)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proveedores encontrados con éxito"),
            @ApiResponse(responseCode = "404", description = "No se encontraron proveedores con ese nombre")
    })
    @GetMapping("/search")
    public ResponseEntity<List<SupplierResponseDTO>> searchSuppliersByName(
            @Parameter(description = "Nombre o parte del nombre a buscar", example = "Distribuidora", required = true)
            @RequestParam String name) {

        return ResponseEntity.ok(supplierService.searchSuppliersByName(name));
    }
}