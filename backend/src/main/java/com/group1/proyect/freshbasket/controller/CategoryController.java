package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.dto.request.CategoryRequestDTO;
import com.group1.proyect.freshbasket.dto.response.CategoryResponseDTO;
import com.group1.proyect.freshbasket.entity.Category;
import com.group1.proyect.freshbasket.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "API optimizada para la gestión de categorías de FreshBasket")
public class CategoryController extends GenericController<Category,
        CategoryRequestDTO, CategoryResponseDTO, Long> {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        super(categoryService, "categoría");
        this.categoryService = categoryService;
    }

    @Operation(
            summary = "Buscar categorías por nombre",
            description = "Retorna categorías que coincidan con el nombre especificado utilizando una búsqueda parcial)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorías encontradas exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron categorías con ese criterio")
    })
    @GetMapping("/search")
    public ResponseEntity<List<CategoryResponseDTO>> searchCategoriesByName(
            @Parameter(description = "Nombre o parte del nombre de la categoría a buscar", example = "Frutas", required = true)
            @RequestParam String name) {

        return ResponseEntity.ok(categoryService.searchCategoriesByName(name));
    }
}