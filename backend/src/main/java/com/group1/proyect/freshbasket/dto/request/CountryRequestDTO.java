package com.group1.proyect.freshbasket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para recibir datos de un país (sin ID)")
public class CountryRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    @Schema(description = "El nombre del país", example = "El Salvador")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 200, message = "La descripción no puede exceder los 200 caracteres")
    @Schema(description = "Descripción del país", example = "País ubicado en Centroamérica")
    private String description;
}
