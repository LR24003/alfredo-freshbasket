package com.group1.proyect.freshbasket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar los datos del detalle de una venta")
public class SaleDetailsResponseDTO {

    @Schema(description = "ID del detalle de la venta", example = "1")
    private Long id;

    @Schema(description = "ID de la venta a la que pertenece este detalle", example = "10")
    private Long saleId;

    @Schema(description = "ID del producto en el detalle de la venta", example = "10")
    private Long productId;

    @Schema(description = "El nombre del producto para buscarlo en la base de datos", example = "Carne de res")
    private String productName;

    @Schema(description = "Cantidad vendida de este producto", example = "5")
    private Integer quantity;

    @Schema(description = "Precio o costo unitario del producto al momento de la venta", example = "2.50")
    private BigDecimal unitCost;

    @Schema(description = "Monto de descuento aplicado a este producto", example = "0.00")
    private BigDecimal discount;

}
