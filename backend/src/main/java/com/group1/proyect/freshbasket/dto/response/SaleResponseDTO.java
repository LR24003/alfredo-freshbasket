package com.group1.proyect.freshbasket.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar los datos de una venta (con ID, relaciones y desglose fiscal)")
public class SaleResponseDTO {

    @Schema(description = "ID único de la venta transaccionada", example = "1")
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    @Schema(description = "Fecha y hora en la que se ejecutó la venta", example = "29/04/2026 16:45")
    private LocalDateTime saleDate;

    @Schema(description = "El monto total de la venta (IVA e impuestos de consumidor final incluidos)", example = "100.00")
    private BigDecimal totalAmount;

    @Schema(description = "El método de pago utilizado por el cliente", example = "Efectivo")
    private String paymentMethod;

    @Schema(description = "El estado comercial de la venta", example = "COMPLETADA")
    private String status;

    @Schema(description = "El nombre completo del cliente que realiza la compra", example = "Ana María Cazzu")
    private String customerName;

    @Schema(description = "El nombre completo del empleado que opera la venta", example = "Martin Einsten Jaramillo")
    private String employeeName;

    @Schema(description = "Condición de la venta según catálogo MH (1: Contado, 2: Crédito, 3: Otro)", example = "1")
    private Integer condicionOperacion;

    @Schema(description = "Sumatoria del IVA extraído y desglosado de la venta total", example = "11.50")
    private BigDecimal ivaTotal;

    @Schema(description = "Monto total de la venta expresado formalmente en letras", example = "CIEN DÓLARES CON CERO CENTAVOS DE DÓLAR")
    private String totalLetras;

    @Schema(description = "Lista con todos los productos desglosados que componen esta venta")
    private List<SaleDetailsResponseDTO> details;
}