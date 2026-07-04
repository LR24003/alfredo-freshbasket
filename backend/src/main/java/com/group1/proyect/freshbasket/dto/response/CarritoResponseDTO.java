package com.group1.proyect.freshbasket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO que representa una fila o producto dentro del carrito con sus subtotales")
public class CarritoResponseDTO {

    @Schema(description = "ID del ítem del carrito", example = "15")
    private Long id;

    @Schema(description = "ID del producto", example = "1")
    private Long productId;

    @Schema(description = "Nombre del producto", example = "Carne de Res")
    private String productName;

    @Schema(description = "Cantidad de unidades en el carrito", example = "3")
    private Integer quantity;

    @Schema(description = "Precio unitario actual del producto", example = "5.50")
    private BigDecimal unitPrice;

    @Schema(description = "Descuentos por promociones", example = "10%")
    private BigDecimal discount;

    @Schema(description = "Subtotal de este producto (Cantidad x Precio Unitario)", example = "16.50")
    private BigDecimal subtotal;

    @Schema(description = "Estado de borrado lógico del ítem", example = "true")
    private boolean active;
}
