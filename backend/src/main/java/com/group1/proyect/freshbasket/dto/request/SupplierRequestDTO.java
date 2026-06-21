package com.group1.proyect.freshbasket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para recibir datos de un proveedor (sin ID)")
public class SupplierRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    @Schema(description = "Nombre del proveedor", example = "Jurídico o natural = Importadora Del Campo")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres")
    @Schema(description = "Apellidos del proveedor", example = "SA de CV o apellidos de persona natural")
    private String lastName;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 15, message = "El teléfono no puede exceder los 15 caracteres")
    @Schema(description = "Teléfono de contacto del proveedor", example = "2300-3476")
    private String phone;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    @Schema(description = "E-mail del proveedor", example = "distribuidora.delcampo@mail.com")
    private String email;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 150, message = "La dirección no puede exceder los 150 caracteres")
    @Schema(description = "Dirección del proveedor", example = "Av. las Amapolas #102, San Salvador")
    private String address;

    @Schema(description = "nombre del país", example = " El Salvador")
    private String countryName;

}