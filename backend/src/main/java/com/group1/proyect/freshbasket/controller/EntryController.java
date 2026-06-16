package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.dto.request.EntryRequestDTO;
import com.group1.proyect.freshbasket.dto.response.EntryResponseDTO;
import com.group1.proyect.freshbasket.entity.Entry;
import com.group1.proyect.freshbasket.service.EntryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/entries")
@Tag(name = "Entries", description = "API para la gestión de entradas del inventario de FreshBasket")
public class EntryController extends GenericController<Entry, EntryRequestDTO, EntryResponseDTO, Long> {

    private final EntryService entryService;

    public EntryController(EntryService entryService) {
        super(entryService, "entrada de inventario");
        this.entryService = entryService;
    }

}