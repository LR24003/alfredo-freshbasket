package com.group1.proyect.freshbasket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar los datos del reporte de productos mas vendidos")
public class ProductsSoldResponseDTO {

    @Schema(description = "Id del producto vendido", example = "3")
    private Long id;

    @Schema(description = "Nombre del producto vendido", example = "Leche Australian")
    private String productName;

    @Schema(description = "Total de unidades vendidas del producto", example = "10")
    private Integer unitsSold;

    @Schema(description = "Precio unitario al cual se vendió el producto", example = "$2.55")
    private BigDecimal unitPrice;

    @Schema(description = "Total monetario de la cantidad del producto vendido", example = "$50.00")
    private BigDecimal totalRevenue;
}
