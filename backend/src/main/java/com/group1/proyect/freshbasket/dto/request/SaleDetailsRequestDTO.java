package com.group1.proyect.freshbasket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para recibir datos del detalle de una venta con especificaciones fiscales")
public class SaleDetailsRequestDTO {

    @NotBlank(message = "El nombre del producto es obligatorio.")
    @Schema(description = "El nombre del producto para buscarlo en la base de datos", example = "Carne de res")
    private String productName;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(description = "Cantidad vendida de este producto", example = "5")
    private Integer quantity;

    @NotNull(message = "El costo unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El costo unitario debe ser mayor o igual a 0 (Para consumidor final, incluye IVA)")
    @Schema(description = "Precio o costo unitario del producto al momento de la venta", example = "2.50")
    private BigDecimal unitCost;

    @NotNull(message = "El descuento es obligatorio (puede ser 0.0)")
    @DecimalMin(value = "0.0", inclusive = true, message = "El descuento no puede ser negativo")
    @Schema(description = "Monto de descuento aplicado a este producto", example = "0.00")
    private BigDecimal discount;

    @NotNull(message = "El tipo de ítem exento es obligatorio")
    @Min(1) @Max(3)
    @Schema(description = "Clasificación fiscal del producto en este detalle (1: Gravado con IVA 13%, 2: Exento, 3: No sujeto)", example = "1")
    private Integer tipoItemExento;

    @NotBlank(message = "La unidad de medida es obligatoria")
    @Size(max = 3)
    @Schema(description = "Código de unidad de medida según catálogo de Hacienda (Ej: '99' para Unidades/Piezas)", example = "99")
    private String unidadMedidaCodigo;

    @NotNull(message = "El IVA del ítem es obligatorio")
    @DecimalMin(value = "0.0")
    @Schema(description = "Monto del IVA calculado individualmente para esta partida de productos", example = "1.17")
    private BigDecimal ivaItem;
}