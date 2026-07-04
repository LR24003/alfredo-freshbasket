package com.group1.proyect.freshbasket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para recibir datos de un USUARIO (sin ID) adaptado con campos fiscales")
public class UserRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    @Schema(description = "Nombre del usuario", example = "Martin Antonio")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    @Schema(description = "Apellidos del usuario", example = "Hernandez Verdugo")
    private String lastName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email invalido")
    @Schema(description = "E-mail del usuario", example = "martin.hernandez@mail.com")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20)
    @Schema(description = "Teléfono de contacto del usuario", example = "22558888")
    private String phone;

    @NotBlank(message = "El rol es obligatorio")
    @Schema(description = "Rol del usuario", example = "CLIENTE")
    private String role;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña del usuario", example = "JDPEOD34#&TEmxr")
    private String password;

    @NotNull(message = "El nombre del país es obligatorio")
    @Schema(description = "Nombre del país", example = "El Salvador")
    private String countryName;

    @NotBlank(message = "El número de documento legal es obligatorio")
    @Size(max = 20)
    @Schema(description = "Número de DUI o NIT sanitizado (sin guiones) del usuario", example = "003405679")
    private String numeroDocumento;

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Size(max = 2)
    @Schema(description = "Catálogo oficial MH: 13=DUI, 36=NIT, 02=Pasaporte, 37=Extranjero", example = "13")
    private String tipoDocumento;

    @Size(max = 10)
    @Schema(description = "Número de Registro de Contribuyente (Obligatorio solo si el cliente requiere Crédito Fiscal - CCF)", example = "1234567")
    private String nrcCustomer;

    @Size(max = 8)
    @Schema(description = "Código de actividad económica asignado por el MH (Solo empresas/contribuyentes)", example = "47110")
    private String actividadEconomicaCodigo;

    @NotBlank(message = "El código de departamento es obligatorio")
    @Size(max = 2)
    @Schema(description = "Código de Departamento según catálogo del MH (Ej: '06' para San Salvador)", example = "06")
    private String departamentoCodigo;

    @NotBlank(message = "El código de municipio es obligatorio")
    @Size(max = 2)
    @Schema(description = "Código de Municipio según catálogo del MH (Ej: '14' para San Salvador municipio)", example = "14")
    private String municipioCodigo;

    @NotBlank(message = "La dirección detallada es obligatoria")
    @Size(max = 250)
    @Schema(description = "Dirección residencial o comercial completa (Calle, Pasaje, Block, etc.)", example = "Alameda Roosevelt, Condominio El Ángel, Edificio B, Apto 4")
    private String direccionDetallada;
}