package com.group1.proyect.freshbasket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar datos de la CATEGORIA (con ID y relaciones)")
public class CategoryResponseDTO {

    @Schema(description = "ID de la categoria", example = "1")
    private Long id;

    @Schema(description = "El nombre de la categoria", example = "Frutas")
    private String name;

    @Schema(description = "Descripción de la categoria", example = "Frutas nacionales o importadas")
    private String description;
}
