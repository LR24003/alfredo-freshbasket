package com.group1.proyect.freshbasket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para recibir datos de una categoria (sin ID)")
public class CategoryRequestDTO {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre de la categoría", example = "Frutas")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 200)
    @Column(nullable = false, length = 100)
    @Schema(description = "Descripción de la categoría", example = "Frutas nacionales o importadas")
    private String description;

}
