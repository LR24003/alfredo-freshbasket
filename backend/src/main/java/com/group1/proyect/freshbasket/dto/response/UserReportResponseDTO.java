package com.group1.proyect.freshbasket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar los datos del reporte de usuarios")
public class UserReportResponseDTO {

    @Id
    @Schema(description = "Id del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre completo del usuario", example = "Josè Alfredo Lòpez Rivera")
    private String fullName;

    @Schema(description = "Correo electrónico del usuario", example = "correo@mail.com")
    private String email;

    @Schema(description = "Rol del usuario", example = "EMPLEADO")
    private String role;

    @Schema(description = "Estado del usuario", example = "activo o inactivo")
    private String estado;

    @Schema(description = "Paìs de origen del usuario", example = "El Salvador")
    private String countryName;

}
