package com.group1.proyect.freshbasket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar los datos del reporte de proveedores")
public class SuppliersReportResponseDTO {

    @Schema(description = "Id del proveedor", example = "1")
    private Long id;

    @Schema(description = "Nombre completo del proveedor", example = "Rancho 17 SA de CV")
    private String supplierName;

    @Schema(description = "Pais de origen del proveedor", example = "El Salvador")
    private String country;

    @Schema(description = "Producto principal que mas provee al inventario", example = "Carne de Res")
    private String mainProduct;

    @Schema(description = "Total del producto suministrado a la fecha", example = "50")
    private String suppliedVolume;

    @Schema(description = "Cantidad de productos de catalogo suministrados por el proveedor", example = "5")
    private Integer totalProducts;

    @Schema(description = "Cantidad total de productos en stock disponible", example = "40")
    private Integer totalStock;

    @Schema(description = "Monto total monetario comprado al proveedor", example = "$150.00")
    private BigDecimal totalPurchased;
}
