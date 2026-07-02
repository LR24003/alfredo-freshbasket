package com.group1.proyect.freshbasket.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar los datos del reporte de logs de auditoría (Vista)")
public class AuditLogReportResponseDTO {

    @Id
    @Schema(description = "ID del registro de auditoría", example = "1")
    private Long id;

    @Schema(description = "Nombre de la entidad o tabla afectada", example = "Product")
    private String entity;

    @Schema(description = "ID del registro específico que fue afectado en esa entidad", example = "45")
    private Long entityId;

    @Schema(description = "Nombre del usuario o empleado que realizó la acción", example = "Martin Einstein Jaramillo")
    private String userName;

    @Schema(description = "Acción realizada sobre el registro", example = "INSERT, UPDATE, DELETE")
    private String action;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    @Schema(description = "Fecha y hora en la que se registró el evento de auditoría", example = "27/06/2026 10:55")
    private LocalDateTime createdAt;

}
