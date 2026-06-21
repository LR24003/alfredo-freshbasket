package com.group1.proyect.freshbasket.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar los datos de una venta (con ID y relaciones)")
public class SaleResponseDTO {

    @Schema(description = "ID de la salida", example = "1")
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    @Schema(description = "Fecha y hora en la que se ejecuto la salida", example = "29/04/2026 16:45")
    private LocalDateTime saleDate;

    @Schema(description = "El monto total de la venta", example = "$100.00")
    private BigDecimal totalAmount;

    @Schema(description = "El método de pago que usa el cliente", example = "Efectivo, Tarjeta de credito")
    private String paymentMethod;

    @Schema(description = "El estado de la venta", example = "Completada, Pendiente, Cancelada")
    private String status;

    @Schema(description = "El nombre del empleado que realiza la venta", example = "Ana María Cazzu")
    private String customerName;

    @Schema(description = "El nombre del empleado que realiza la venta", example = "Martin Einsten Jaramillo")
    private String employeeName;

    @Schema(description = "Lista con todos los productos que componen esta venta")
    private List<SaleDetailsResponseDTO> details;

}
