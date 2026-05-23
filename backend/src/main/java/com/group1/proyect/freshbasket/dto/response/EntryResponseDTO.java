package com.group1.proyect.freshbasket.dto.response;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar datos de la entrada (con ID y relaciones)")
public class EntryResponseDTO {

    @Schema(description = "ID de la entrada", example = "1")
    private Long id;

    @Schema(description = "Precio de cada producto en la entrada", example = "manzanas = 0.50")
    private BigDecimal unitCost;

    @Schema(description = "Cantidad total de la entrada", example = "500")
    private Integer quantity;

    @Schema(description = "ID del producto", example = "1")
    private Long productId;

    @Schema(description = "ID del proveedor", example = "1")
    private Long supplierId;

    @Schema(description = "ID del usuario", example = "1")
    private Long userId;
} 
