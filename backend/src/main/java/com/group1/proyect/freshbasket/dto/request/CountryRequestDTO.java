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
@Schema(description = "DTO para recibir datos de una pais (sin ID)")
public class CountryRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    @Schema(description = "El nombre del pais", example = "El Salvador")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 200)
    @Column(nullable = false, length = 100)
    @Schema(description = "Descripción del pais", example = "País pequeño ubicado en Ámerica")
    private String description;

}
