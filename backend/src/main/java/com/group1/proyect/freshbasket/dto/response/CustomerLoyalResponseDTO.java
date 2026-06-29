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
@Schema(description = "DTO para enviar los datos del reporte de clientes con mas compras (Vista)")
public class CustomerLoyalResponseDTO {

    @Schema(description = "ID del registro de reporte", example = "1")
    private Long id;

    @Schema(description = "Nombre del cliente que realiza la compra", example = "Ana María Cazzu")
    private String customerName;

    @Schema(description = "Email del cliente que realizo la compra", example = "ejemplo@mail.com")
    private String customerEmail;

    @Schema(description = "Total de compras realizadas", example = "4")
    private Integer totalPurchases;

    @Schema(description = "Total que gasto el cliente en la compra", example = "$100.00")
    private BigDecimal totalSpent;

}
