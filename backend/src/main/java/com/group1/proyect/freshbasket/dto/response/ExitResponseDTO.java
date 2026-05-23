package com.group1.proyect.freshbasket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar datos del USUARIO (con ID y relaciones)")
public class ExitResponseDTO {

    @Schema(description = "ID de la salida", example = "1")
    private Long id;

    @Schema(description = "Fecha y hora en la que se ejecuto la entrada", example = "29/04/2026 16:45")
    private LocalDateTime exitDate;

    @Schema(description = "Cantidad total de la salida", example = "50")
    private Integer quantity;

    @Schema(description = "ID del producto", example = "1")
    private Long productId;

    @Schema(description = "ID del usuario", example = "1")
    private Long userId;
}