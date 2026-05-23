package com.group1.proyect.freshbasket.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para recibir datos de una entrada (sin ID)")
public class EntryRequestDTO {

    @NotNull(message = "El costo unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El costo unitario debe ser mayor a 0")
    @Schema(description = "Precio de cada producto en la entrada", example = "manzanas = 0.50")
    private BigDecimal unitCost;

    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Schema(description = "Cantidad total de la entrada", example = "500")
    private Integer quantity;

    @NotNull(message = "El producto es obligatorio")
    @Schema(description = "ID del producto", example = "1")
    private Long productId;

    @NotNull(message = "El proveedor es obligatorio")
    @Schema(description = "ID del proveedor", example = "1")
    private Long supplierId;

    @NotNull(message = "El usuario es obligatorio")
    @Schema(description = "ID del usuario", example = "1")
    private Long userId;
}