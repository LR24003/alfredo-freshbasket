package com.group1.proyect.freshbasket.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DteControlRequestDTO {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long customerId;

    private Long saleId;

    @NotNull(message = "El ID del empleado es obligatorio")
    private Long employeeId;

    @NotBlank(message = "El método de pago es obligatorio")
    @Size(max = 50)
    private String paymentMethod;

    @NotNull(message = "La condición de operación es obligatoria")
    @Min(value = 1, message = "Condición inválida (1: Contado, 2: Crédito, 3: Otro)")
    @Max(value = 3, message = "Condición inválida (1: Contado, 2: Crédito, 3: Otro)")
    private Integer condicionOperacion;

    @NotBlank(message = "El tipo de documento fiscal es obligatorio")
    @Size(min = 2, max = 2)
    private String tipoDocumento;

    @NotEmpty(message = "La factura debe contener al menos un producto")
    @Valid
    private List<DteItemDetailRequestDTO> items;

}