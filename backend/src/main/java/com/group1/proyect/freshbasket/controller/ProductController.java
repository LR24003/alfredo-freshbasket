package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.dto.request.ProductRequestDTO;
import com.group1.proyect.freshbasket.dto.response.ProductResponseDTO;
import com.group1.proyect.freshbasket.entity.Product;
import com.group1.proyect.freshbasket.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "API optimizada para la gestión de productos del inventario FreshBasket")
public class ProductController extends GenericController<Product, ProductRequestDTO, ProductResponseDTO, Long> {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        super(productService, "producto");
        this.productService = productService;
    }

    @Operation(
            summary = "Buscar productos por nombre",
            description = "Retorna productos que coincidan con el nombre especificado (búsqueda parcial)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos encontrados con éxito"),
            @ApiResponse(responseCode = "404", description = "No se encontraron coincidencias")
    })
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProductsByName(
            @Parameter(description = "Nombre o parte del nombre a buscar", example = "Manzana", required = true)
            @RequestParam String name) {

        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

    @Operation(
            summary = "Obtener alertas de stock bajo",
            description = "Retorna una lista de los productos cuyo stock actual está por debajo o igual a su stock mínimo configurado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alertas encontradas con éxito"),
            @ApiResponse(responseCode = "404", description = "No se encontraron coincidencias")
    })
    @GetMapping("/alerts/low-stock")
    public ResponseEntity<List<ProductResponseDTO>> getLowStockAlerts() {
        List<ProductResponseDTO> alerts = productService.getLowStockAlerts();
        return ResponseEntity.ok(alerts);
    }

    @Operation(
            summary = "Obtener productos por categoría",
            description = "Retorna una lista de todos los productos activos asociados a una categoría específica utilizando su nombre."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos de la categoría obtenidos con éxito"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada o vacía")
    })
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(@PathVariable String categoryName) {
        List<ProductResponseDTO> products = productService.getProductsByCategory(categoryName);
        return ResponseEntity.ok(products);
    }
}