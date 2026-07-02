package com.group1.proyect.freshbasket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar los datos del reporte de inventario")
public class InventoryReportResponseDTO {

    @Id
    @Schema(description = "ID del registro del inventario", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto en el registro del inventario", example = "Leche en polvo")
    private String productName;

    @Schema(description = "Precio actual en el registro del inventario", example = "$3.50")
    private BigDecimal currentPrice;

    @Schema(description = "Cantidad actual de entradas del producto en el registro del inventario", example = "50")
    private Integer totalEntries;

    @Schema(description = "Cantidad actual de salidas del producto en el registro del inventario", example = "$20")
    private Integer totalExits;

    @Schema(description = "Cantidad disponible actual en el registro del inventario", example = "30")
    private Integer stockAvailable;

}
