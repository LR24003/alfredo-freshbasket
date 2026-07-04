package com.group1.proyect.freshbasket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Respuesta del estado fiscal del DTE posterior a la validación con el Ministerio de Hacienda")
public class DteControlResponseDTO {

    @Schema(description = "ID único del registro de control fiscal en la BD", example = "1005")
    private Long dteId;

    @Schema(description = "ID de la transacción comercial de venta asociada", example = "5432")
    private Long saleId;

    @Schema(description = "Código de Generación (UUIDv4) universal del DTE", example = "f3b07384-d113-4ec6-a570-56e6d1ff69ec")
    private String codigoGeneracion;

    @Schema(description = "Número de control correlativo con estructura oficial del MH", example = "DTE-01-M001P001-000000000000231")
    private String numeroControl;

    @Schema(description = "Tipo de documento tributario procesado", example = "01")
    private String tipoDocumento;

    @Schema(description = "Estado actual del ciclo de procesamiento fiscal", example = "APROBADO")
    private String statusFiscal;

    @Schema(description = "Sello de recepción otorgado por el MH (Solo si el DTE fue APROBADO)", example = "2026DTE74635291817162534")
    private String selloRecepcion;

    @Schema(description = "Fecha y hora exacta de la transmisión del documento")
    private LocalDateTime fechaTransmision;

    @Schema(description = "Monto total de la venta liquidada (IVA e impuestos incluidos)", example = "10.20")
    private BigDecimal totalAmount;

    @Schema(description = "Desglose total del IVA extraído por el backend", example = "1.17")
    private BigDecimal ivaTotal;

    @Schema(description = "Representación formal del valor total en letras", example = "DIEZ DÓLARES CON VEINTE CENTAVOS DE DÓLAR")
    private String totalLetras;

    @Schema(description = "Enlace oficial de consulta pública para inyectar en el Código QR de React", example = "https://incp.mh.gob.sv/central/esquemas/pages/ce-consultadte.xhtml?pid=f3b07384-d113-4ec6-a570-56e6d1ff69ec")
    private String urlConsulta;

    @Schema(description = "Cuerpo JSON original de respuesta del MH (Contiene la pila de errores si es RECHAZADO)")
    private Object dteRespuestaRaw;

    @Schema(description = "Nombre completo del cliente asignado", example = "Juan Pérez")
    private String customerName;

    @Schema(description = "Método de pago final registrado", example = "Efectivo")
    private String paymentMethod;
}