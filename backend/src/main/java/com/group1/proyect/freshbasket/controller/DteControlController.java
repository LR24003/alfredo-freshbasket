package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.dto.request.DteControlRequestDTO;
import com.group1.proyect.freshbasket.dto.response.DteControlResponseDTO;
import com.group1.proyect.freshbasket.entity.DteControl;
import com.group1.proyect.freshbasket.service.DteControlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dte-control")
@Tag(name = "DTE Control", description = "API para la consulta y auditoría de Documentos Tributarios Electrónicos (DTE)")
public class DteControlController extends GenericController<DteControl, DteControlRequestDTO, DteControlResponseDTO, Long> {

    private final DteControlService dteControlService;

    public DteControlController(DteControlService dteControlService) {
        super(dteControlService, "Control DTE");
        this.dteControlService = dteControlService;
    }

    @Operation(
            summary = "Buscar DTE por Código de Generación (UUID)",
            description = "Retorna el documento tributario utilizando el UUID universal exigido por el Ministerio de Hacienda"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento fiscal localizado con éxito"),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún DTE con el UUID proporcionado")
    })
    @GetMapping("/codigo-generacion/{codigoGeneracion}")
    public ResponseEntity<DteControlResponseDTO> getByCodigoGeneracion(@PathVariable UUID codigoGeneracion) {
        DteControlResponseDTO dte = dteControlService.getByCodigoGeneracion(codigoGeneracion);
        return ResponseEntity.ok(dte);
    }

    @Operation(
            summary = "Buscar DTE por Número de Control",
            description = "Retorna el documento fiscal utilizando el formato secuencial interno (Ej: DTE-01-M001P001-000000000000001)"
    )
    @GetMapping("/numero-control/{numeroControl}")
    public ResponseEntity<DteControlResponseDTO> getByNumeroControl(@PathVariable String numeroControl) {
        DteControlResponseDTO dte = dteControlService.getByNumeroControl(numeroControl);
        return ResponseEntity.ok(dte);
    }

    @Operation(
            summary = "Buscar DTE asociado a una venta comercial",
            description = "Permite recuperar la información de facturación electrónica vinculada a una transacción comercial por su ID de venta"
    )
    @GetMapping("/sale/{saleId}")
    public ResponseEntity<DteControlResponseDTO> getBySaleId(@PathVariable Long saleId) {
        DteControlResponseDTO dte = dteControlService.getBySaleId(saleId);
        return ResponseEntity.ok(dte);
    }

    @Operation(
            summary = "Listar DTEs por Estado Fiscal",
            description = "Filtra el historial de documentos según su estatus en el Ministerio de Hacienda (Ej: APROBADO, PENDIENTE, RECHAZADO)"
    )
    @GetMapping("/status/{statusFiscal}")
    public ResponseEntity<List<DteControlResponseDTO>> getByStatusFiscal(@PathVariable String statusFiscal) {
        List<DteControlResponseDTO> dtes = dteControlService.getByStatusFiscal(statusFiscal.toUpperCase());
        return ResponseEntity.ok(dtes);
    }
}