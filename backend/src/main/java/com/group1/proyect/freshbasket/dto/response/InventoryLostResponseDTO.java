package com.group1.proyect.freshbasket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar los datos del reporte de inventario perdido o dañado")
public class InventoryLostResponseDTO {

    @Schema(description = "Id del producto dañado o perdido", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto dañado o perdido", example = "Leche en polvo")
    private String ProductName;

    @Schema(description = "Razón por la cual el producto salio del inventario", example = "Merma, Daño, Caducidad")
    private String exitReason;

    @Schema(description = "Total de unidades perdidas o dañadas", example = "10")
    private Integer unitsLost;

    @Schema(description = "Total monetario al que asciende por inventario perdido o dañado", example = "$25.00")
    private BigDecimal totalLost;
}
