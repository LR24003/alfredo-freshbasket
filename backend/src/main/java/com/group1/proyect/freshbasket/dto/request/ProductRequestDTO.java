package com.group1.proyect.freshbasket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para recibir datos de un producto (sin ID) con configuración fiscal")
public class ProductRequestDTO {

    @Schema(description = "Nombre del producto", example = "Manzana Roja")
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @Schema(description = "Precio del producto", example = "0.50")
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio debe ser mayor o igual a 0")
    private BigDecimal price;

    @Schema(description = "Stock disponible", example = "150")
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    private Integer currentStock;

    @NotNull(message = "El stock minimo es obligatorio")
    @Schema(description = "Stock minimo disponible", example = "5")
    private Integer minStock;

    @Schema(description = "Descripción del producto", example = "Manzana fresca importada")
    private String description;

    @Schema(description = "URL de la imagen del producto", example = "https://miapp.com/img/manzana.jpg")
    private String imageUrl;

    @Schema(description = "Descuento decimal aplicable al producto", example = "0.10")
    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    private BigDecimal discount;

    @Schema(description = "Indica si el producto está activo", example = "true")
    private boolean active = true;

    @Schema(description = "Nombre de la categoría", example = "Frutas")
    @NotNull(message = "El nombre de la categoría es obligatorio")
    private String categoryName;

    @Schema(description = "El nombre del proveedor", example = "Distribuidora del monte")
    @NotNull(message = "El nombre del proveedor es obligatorio")
    private String supplierName;

    @Schema(description = "Nombre del usuario", example = "Juan Martinez")
    @NotNull(message = "El nombre del usuario es obligatorio")
    private String userName;

    @NotNull(message = "El tipo de ítem de Hacienda es obligatorio")
    @Min(1) @Max(2)
    @Schema(description = "Clasificación del ítem según el catálogo del MH (1: Bienes/Mercancías, 2: Servicios/Fletes)", example = "1")
    private Integer tipoItem;

    @NotNull(message = "El tipo de impuesto por defecto es obligatorio")
    @Min(1) @Max(3)
    @Schema(description = "Tratamiento tributario por defecto (1: Gravado con IVA 13%, 2: Exento, 3: No sujeto)", example = "1")
    private Integer tipoImpuestoDefecto;

    @NotBlank(message = "La unidad de medida por defecto es obligatoria")
    @Size(max = 3)
    @Schema(description = "Código de unidad de medida del catálogo MH (Ej: '99' para unidades/Piezas, '57' para servicios)", example = "99")
    private String unidadMedidaDefecto;
}