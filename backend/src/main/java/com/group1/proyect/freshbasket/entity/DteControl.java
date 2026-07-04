package com.group1.proyect.freshbasket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dte_control")
public class DteControl implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dte_id")
    private Long id;

    @NotNull(message = "La venta asociada es obligatoria")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    @JsonIgnore
    private Sale sale;

    @NotNull
    @Column(name = "codigo_generacion", nullable = false, updatable = false, unique = true)
    private java.util.UUID codigoGeneracion;

    @NotBlank(message = "El número de control es obligatorio")
    @Size(max = 31)
    @Column(name = "numero_control", length = 31, nullable = false, unique = true)
    private String numeroControl;

    @NotBlank
    @Size(max = 2)
    @Column(name = "tipo_documento", length = 2, nullable = false)
    @Builder.Default
    private String tipoDocumento = "01";

    @NotNull
    @Column(name = "modelo_facturacion", nullable = false)
    @Builder.Default
    private Integer modeloFacturacion = 1;

    @NotNull
    @Column(name = "tipo_transmision", nullable = false)
    @Builder.Default
    private Integer tipoTransmision = 1;

    @NotBlank
    @Size(max = 50)
    @Column(name = "status_fiscal", length = 50, nullable = false)
    @Builder.Default
    private String statusFiscal = "PENDIENTE";

    @Size(max = 40)
    @Column(name = "sello_recepcion", length = 40)
    private String selloRecepcion;

    @Column(name = "fecha_generacion", updatable = false)
    private LocalDateTime fechaGeneracion;

    @Column(name = "fecha_transmision")
    private LocalDateTime fechaTransmision;

    @Size(max = 20)
    @Column(name = "codigo_contingencia", length = 20)
    private String codigoContingencia;

    @Size(max = 250)
    @Column(name = "motivo_contingencia", length = 250)
    private String motivoContingencia;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dte_firmado", columnDefinition = "jsonb")
    private Object dteFirmado;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dte_respuesta", columnDefinition = "jsonb")
    private Object dteRespuesta;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.codigoGeneracion == null) {
            this.codigoGeneracion = java.util.UUID.randomUUID();
        }
        this.fechaGeneracion = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}