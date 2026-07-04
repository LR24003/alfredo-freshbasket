package com.group1.proyect.freshbasket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar datos del producto al cliente (con ID, relaciones y configuración fiscal)")
public class ProductResponseDTO {

    @Schema(description = "ID del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Manzana Roja")
    private String name;

    @Schema(description = "Precio del producto", example = "0.50")
    private BigDecimal price;

    @Schema(description = "Stock disponible", example = "150")
    private Integer currentStock;

    @Schema(description = "Stock minimo disponible", example = "5")
    private Integer minStock;

    @Schema(description = "Descripción del producto", example = "Manzana fresca importada")
    private String description;

    @Schema(description = "URL de la imagen del producto", example = "https://miapp.com/img/manzana.jpg")
    private String imageUrl;

    @Schema(description = "Descuento decimal aplicable al producto", example = "0.10")
    private BigDecimal discount;

    @Schema(description = "Indica si el producto está activo", example = "true")
    private boolean active;

    @Schema(description = "ID de la categoría", example = "1")
    private Long categoryId;

    @Schema(description = "Nombre de la categoría", example = "Frutas")
    private String categoryName;

    @Schema(description = "ID del proveedor", example = "1")
    private Long supplierId;

    @Schema(description = "Nombre del proveedor", example = "Distribuidora El Campo")
    private String supplierName;

    @Schema(description = "ID del usuario", example = "1")
    private Long userId;

    @Schema(description = "Nombre del usuario", example = "Juan Martinez")
    private String userName;

    @Schema(description = "Clasificación del ítem según el catálogo del MH (1: Bienes/Mercancías, 2: Servicios/Fletes)", example = "1")
    private Integer tipoItem;

    @Schema(description = "Tratamiento tributario por defecto (1: Gravado con IVA 13%, 2: Exento, 3: No sujeto)", example = "1")
    private Integer tipoImpuestoDefecto;

    @Schema(description = "Código de unidad de medida del catálogo MH (Ej: '99' para unidades/piezas)", example = "99")
    private String unidadMedidaDefecto;
}