package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.dto.request.CountryRequestDTO;
import com.group1.proyect.freshbasket.dto.response.CountryResponseDTO;
import com.group1.proyect.freshbasket.entity.Country;
import com.group1.proyect.freshbasket.service.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@Tag(name = "Countries", description = "API para la gestión del catálogo de países de FreshBasket")
public class CountryController extends GenericController<Country,
        CountryRequestDTO, CountryResponseDTO, Long> {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        super(countryService, "país");
        this.countryService = countryService;
    }

    @Operation(
            summary = "Buscar países por coincidencia de nombre",
            description = "Filtra dinámicamente los países activos cuyo nombre contenga el término enviado. Ideal para buscadores en React."
    )
    @GetMapping("/search")
    public ResponseEntity<List<CountryResponseDTO>> searchCountriesByName(
            @Parameter(description = "Nombre o parte del nombre del país a buscar", example = "El Salvador", required = true)
            @RequestParam String name) {

        return ResponseEntity.ok(countryService.searchCountriesByName(name));
    }
}