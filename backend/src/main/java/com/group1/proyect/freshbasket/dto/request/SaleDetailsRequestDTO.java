package com.group1.proyect.freshbasket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para recibir datos del detalle de una venta")
public class SaleDetailsRequestDTO {

    @NotBlank(message = "El nombre del producto es obligatorio.")
    @Schema(description = "El nombre del producto para buscarlo en la base de datos", example = "Carne de res")
    private String productName;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(description = "Cantidad vendida de este producto", example = "5")
    private Integer quantity;

    @NotNull(message = "El costo unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El costo unitario debe ser mayor a 0")
    @Schema(description = "Precio o costo unitario del producto al momento de la venta", example = "2.50")
    private BigDecimal unitCost;

    @NotNull(message = "El descuento es obligatorio (puede ser 0.0)")
    @DecimalMin(value = "0.0", inclusive = true, message = "El descuento no puede ser negativo")
    @Schema(description = "Monto de descuento aplicado a este producto", example = "0.00")
    private BigDecimal discount;

}
