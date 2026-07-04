package com.group1.proyect.freshbasket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar los datos del detalle de una venta con su respectivo desglose fiscal")
public class SaleDetailsResponseDTO {

    @Schema(description = "ID único de la fila del detalle de venta", example = "1")
    private Long id;

    @Schema(description = "ID de la venta a la que pertenece este detalle", example = "10")
    private Long saleId;

    @Schema(description = "ID del producto facturado", example = "10")
    private Long productId;

    @Schema(description = "El nombre comercial del producto vendido", example = "Carne de res")
    private String productName;

    @Schema(description = "Cantidad vendida de este producto", example = "5")
    private Integer quantity;

    @Schema(description = "Precio o costo unitario al momento de la venta (Para consumidor final, incluye el IVA)", example = "2.50")
    private BigDecimal unitCost;

    @Schema(description = "Monto de descuento directo aplicado a este producto", example = "0.00")
    private BigDecimal discount;

    @Schema(description = "Clasificación fiscal del producto en este detalle (1: Gravado con IVA 13%, 2: Exento, 3: No sujeto)", example = "1")
    private Integer tipoItemExento;

    @Schema(description = "Código de unidad de medida según catálogo de Hacienda (Ej: '99' para Unidades/Piezas)", example = "99")
    private String unidadMedidaCodigo;

    @Schema(description = "Monto del IVA calculado individualmente para esta línea de productos", example = "1.17")
    private BigDecimal ivaItem;
}