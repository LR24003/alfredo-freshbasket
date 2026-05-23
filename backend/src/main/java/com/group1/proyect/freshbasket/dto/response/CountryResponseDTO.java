package com.group1.proyect.freshbasket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar datos del Pais (con ID y relaciones)")
public class CountryResponseDTO {

    @Schema(description = "ID del pais", example = "3")
    private Long id;

    @Schema(description = "El nombre del pais", example = "El Salvador")
    private String name;

    @Schema(description = "Descripcion del pais", example = "Pais pequeño ubicado en America")
    private String description;

}
