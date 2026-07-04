package com.group1.proyect.freshbasket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar datos del USUARIO (con ID, relaciones y campos fiscales)")
public class UserResponseDTO {

    @Schema(description = "ID del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre del usuario", example = "Martin Antonio")
    private String name;

    @Schema(description = "Apellidos del usuario", example = "Hernandez Verdugo")
    private String lastName;

    @Schema(description = "E-mail del usuario", example = "martin.hernandez@mail.com")
    private String email;

    @Schema(description = "Teléfono de contacto del usuario", example = "22558888")
    private String phone;

    @Schema(description = "Rol del usuario", example = "CLIENTE")
    private String role;

    @Schema(description = "ID del país", example = "1")
    private Long countryId;

    @Schema(description = "Nombre del país", example = "El Salvador")
    private String countryName;

    @Schema(description = "Número de DUI o NIT sanitizado (sin guiones) del usuario", example = "003405679")
    private String numeroDocumento;

    @Schema(description = "Tipo de documento según catálogo MH: 13=DUI, 36=NIT, 02=Pasaporte", example = "13")
    private String tipoDocumentoMh;

    @Schema(description = "Número de Registro de Contribuyente (Solo aplica si es Crédito Fiscal)", example = "1234567")
    private String nrcCustomer;

    @Schema(description = "Código de la actividad económica del cliente registrado ante el MH", example = "47110")
    private String actividadEconomicaCodigo;

    @Schema(description = "Código de Departamento según catálogo de Hacienda (Ej: '06')", example = "06")
    private String departamentoCodigo;

    @Schema(description = "Código de Municipio según catálogo de Hacienda (Ej: '14')", example = "14")
    private String municipioCodigo;

    @Schema(description = "Dirección residencial o de facturación completa", example = "Alameda Roosevelt, Condominio El Ángel, Edificio B, Apto 4")
    private String direccionDetallada;
}