package com.group1.proyect.freshbasket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para recibir datos de una venta")
public class SaleRequestDTO {

    @NotNull(message = "El monto total es obligatorio.")
    @Positive(message = "El monto total debe ser un número mayor a cero.")
    @Schema(description = "El monto total de la venta", example = "$100.00")
    private BigDecimal totalAmount;

    @NotBlank(message = "El método de pago es obligatorio.")
    @Size(max = 50, message = "El método de pago no puede superar los 50 caracteres.")
    @Schema(description = "El método de pago que usa el cliente", example = "Efectivo, Tarjeta de credito")
    private String paymentMethod;

    @NotBlank(message = "El estado de la venta es obligatorio.")
    @Size(message = "El estado no puede superar los 30 caracteres.")
    @Schema(description = "El estado de la venta", example = "Completada, Pendiente, Cancelada")
    private String status;

    @NotNull(message = "El ID del cliente es obligatorio.")
    @Schema(description = "El nombre del usuario que realiza la compra", example = "Juan Martinez")
    private Long customerId;

    @NotNull(message = "El ID del empleado es obligatorio.")
    @Schema(description = "El nombre del empleado que realiza la venta", example = "Martin Einsten Jaramillo")
    private Long employeeId;

    @NotNull(message = "El carrito de compras no puede estar vacío.")
    @Schema(description = "Lista con el desglose de productos vendidos (el carrito)")
    private List<SaleDetailsRequestDTO> details;

}
