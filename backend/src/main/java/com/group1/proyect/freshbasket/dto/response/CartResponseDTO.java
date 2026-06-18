package com.group1.proyect.freshbasket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO que representa el estado completo del carrito de un usuario y el total general")
public class CartResponseDTO {

    @Schema(description = "ID del carrito", example = "5")
    private Long id;

    @Schema(description = "ID del usuario dueño del carrito", example = "2")
    private Long userId;

    @Schema(description = "Total general acumulado de la compra (Suma de subtotales activos)", example = "16.50")
    private BigDecimal totalPurchase;

    @Schema(description = "Estado de borrado lógico del carrito", example = "true")
    private boolean active;

    @Schema(description = "Lista de productos activos dentro del carrito")
    private List<CarritoResponseDTO> items;
}