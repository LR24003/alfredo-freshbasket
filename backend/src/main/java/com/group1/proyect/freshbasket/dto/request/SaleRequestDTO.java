package com.group1.proyect.freshbasket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para recibir datos de una venta con desglose fiscal")
public class SaleRequestDTO {

    @NotNull(message = "El monto total es obligatorio.")
    @Positive(message = "El monto total debe ser un número mayor a cero.")
    @Schema(description = "El monto total de la venta (Para consumidor final, ya incluye el IVA)", example = "100.00")
    private BigDecimal totalAmount;

    @NotBlank(message = "El método de pago es obligatorio.")
    @Size(max = 50, message = "El método de pago no puede superar los 50 caracteres.")
    @Schema(description = "El método de pago que usa el cliente", example = "Efectivo")
    private String paymentMethod;

    @NotBlank(message = "El estado de la venta es obligatorio.")
    @Size(max = 30, message = "El estado no puede superar los 30 caracteres.")
    @Schema(description = "El estado interno de la venta", example = "COMPLETADA")
    private String status;

    @NotNull(message = "El ID del cliente es obligatorio.")
    @Schema(description = "ID del usuario/cliente que realiza la compra", example = "45")
    private Long customerId;

    @NotNull(message = "El ID del empleado es obligatorio.")
    @Schema(description = "ID del empleado o cajero que realiza la venta", example = "2")
    private Long employeeId;

    @NotNull(message = "La condición de operación es obligatoria")
    @Min(value = 1) @Max(value = 3)
    @Schema(description = "Condición de la venta según catálogo MH (1: Contado, 2: Crédito, 3: Otro)", example = "1")
    private Integer condicionOperacion;

    @NotNull(message = "El IVA total es obligatorio")
    @DecimalMin(value = "0.0")
    @Schema(description = "Sumatoria desglosada del IVA de todos los ítems de la venta (Calculado preferiblemente por backend)", example = "11.50")
    private BigDecimal ivaTotal;

    @Size(max = 255)
    @Schema(description = "Monto total expresado formalmente en letras", example = "CIEN DÓLARES CON CERO CENTAVOS DE DÓLAR")
    private String totalLetras;

    @NotNull(message = "El carrito de compras no puede estar vacío.")
    @NotEmpty(message = "La lista de detalles debe contener al menos un artículo.")
    @Valid
    @Schema(description = "Lista con el desglose de productos vendidos")
    private List<SaleDetailsRequestDTO> details;
}