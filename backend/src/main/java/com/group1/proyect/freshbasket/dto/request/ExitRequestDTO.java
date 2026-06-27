package com.group1.proyect.freshbasket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para recibir datos de una salida")
public class ExitRequestDTO {

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Schema(description = "Cantidad total de la salida", example = "50")
    private Integer quantity;

    @NotNull(message = "El nombre del producto es obligatorio")
    @Schema(description = "El nombre exacto o descriptivo del producto", example = "Pollo Indio")
    private String productName;

    @NotNull(message = "La razón de la salida es obligatoria")
    @Schema(description = "Razón de o motivo de la salida", example = "Venta")
    private String exitReason;

    @Valid
    @Schema(description = "ID de la venta", example = "1")
    private Long saleId;

    @NotNull(message = "El nombre del usuario es obligatorio")
    @Schema(description = "Nombre completo del usuario que registra la operación", example = "Juan Martinez")
    private String userName;
}