package com.group1.proyect.freshbasket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Detalles de los artículos en el Dte")
public class DteItemDetailRequestDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima debe ser 1")
    private Integer quantity;

    @NotNull(message = "El costo unitario es obligatorio")
    @DecimalMin(value = "0.0", message = "El costo unitario no puede ser negativo")
    private BigDecimal unitCost;

    @Builder.Default
    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    private BigDecimal discount = BigDecimal.ZERO;

    @Builder.Default
    @NotNull(message = "El tipo de ítem exento es obligatorio")
    private Integer tipoItemExento = 1;

    @Builder.Default
    @NotBlank(message = "La unidad de medida es obligatoria")
    @Size(max = 3)
    @Schema(description = "Código de unidad de medida según catálogo de Hacienda (Ej: 99 = Unidades/Piezas)", example = "99")
    private String unidadMedidaCodigo = "99";

    @Builder.Default
    @DecimalMin(value = "0.0")
    @Schema(description = "Monto de IVA correspondiente de manera individual a este ítem (Calculado por el backend)", example = "1.17")
    private BigDecimal ivaItem = BigDecimal.ZERO;
}


