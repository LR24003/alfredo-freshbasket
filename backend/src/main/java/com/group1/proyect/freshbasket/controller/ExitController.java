package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.dto.request.ExitRequestDTO;
import com.group1.proyect.freshbasket.dto.response.ExitResponseDTO;
import com.group1.proyect.freshbasket.entity.Exit;
import com.group1.proyect.freshbasket.service.ExitService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exits")
@Tag(name = "Exits", description = "API para la gestión de salidas del inventario FreshBasket")
public class ExitController extends GenericController<Exit, ExitRequestDTO, ExitResponseDTO, Long> {

    private final ExitService exitService;

    public ExitController(ExitService exitService) {
        super(exitService, "salida de inventario");
        this.exitService = exitService;
    }

}