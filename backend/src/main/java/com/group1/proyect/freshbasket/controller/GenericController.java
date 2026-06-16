package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.service.GenericService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class GenericController<E, REQ, RES, ID> {

    protected final GenericService<E, REQ, RES, ID> service;
    protected final String resourceName;

    protected GenericController(GenericService<E, REQ, RES, ID> service, String resourceName) {
        this.service = service;
        this.resourceName = resourceName;
    }

    @Operation(summary = "Obtener todos los registros", description = "Retorna una lista completa de los elementos activos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros obtenidos exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<RES>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Obtener un registro por su ID", description = "Busca un elemento específico mediante su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RES> getById(
            @Parameter(description = "ID único del registro", required = true) @PathVariable ID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Crear un nuevo registro", description = "Guarda y procesa un nuevo elemento en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos provistos inválidos")
    })
    @PostMapping
    public ResponseEntity<RES> create(@Valid @RequestBody REQ requestDto) {
        return new ResponseEntity<>(service.create(requestDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un registro existente", description = "Reemplaza los datos de un elemento utilizando su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RES> update(
            @Parameter(description = "ID del registro a actualizar", required = true) @PathVariable ID id,
            @Valid @RequestBody REQ requestDto) {
        return ResponseEntity.ok(service.update(id, requestDto));
    }

    @Operation(summary = "Eliminar un registro", description = "Remueve o desactiva un elemento del sistema por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del registro a eliminar", required = true) @PathVariable ID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}