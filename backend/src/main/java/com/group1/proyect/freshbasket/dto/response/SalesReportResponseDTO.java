package com.group1.proyect.freshbasket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar los datos del reporte de ventas")
public class SalesReportResponseDTO {

    @Id
    @Schema(description = "ID del registro de la venta", example = "1")
    private Long Id;

    @Schema(description = "Fecha y hora del registro de la venta", example = "10/06/2026 08:30")
    private LocalDateTime saleDate;

    @Schema(description = "Monto total de la venta", example = "$25.00")
    private BigDecimal totalAmount;

    @Schema(description = "Método de pago utilizado por el cliente", example = "Efectivo")
    private String paymentMethod;

    @Schema(description = "Estado de la venta", example = "Completada, pendiente o cancelada")
    private String status;

    @Schema(description = "Empleado que realizo la venta", example = "Martin Jaramillo")
    private String employeeName;

    @Schema(description = "Correo electrónico del empleado", example = "martin.einsten@mail.com")
    private String employeeEmail;
}
