package com.group1.proyect.freshbasket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para agregar o actualizar un producto dentro del carrito de compras")
public class CarritoRequestDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(description = "El ID del producto que se desea añadir", example = "1")
    private Long productId;

    @Min(value = 1, message = "La cantidad mínima a agregar debe ser 1")
    @Schema(description = "La cantidad de unidades del producto", example = "3")
    private Integer quantity;
}