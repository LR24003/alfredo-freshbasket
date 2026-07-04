package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.request.DteControlRequestDTO;
import com.group1.proyect.freshbasket.dto.response.DteControlResponseDTO;
import com.group1.proyect.freshbasket.entity.DteControl;
import com.group1.proyect.freshbasket.entity.Sale;
import com.group1.proyect.freshbasket.entity.User;
import com.group1.proyect.freshbasket.repository.DteControlRepository;
import com.group1.proyect.freshbasket.repository.SaleRepository;
import com.group1.proyect.freshbasket.repository.UserRepository;
import com.group1.proyect.freshbasket.service.DteControlService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DteControlServiceImpl extends GenericServiceImpl<DteControl, DteControlRequestDTO, DteControlResponseDTO, Long>
        implements DteControlService {

    private final DteControlRepository dteControlRepository;
    private final SaleRepository saleRepository;
    private final UserRepository userRepository;

    public DteControlServiceImpl(
            DteControlRepository dteControlRepository,
            SaleRepository saleRepository,
            UserRepository userRepository) {
        super(dteControlRepository);
        this.dteControlRepository = dteControlRepository;
        this.saleRepository = saleRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public DteControlResponseDTO emitirDte(DteControlRequestDTO request) {
        Sale sale = saleRepository.findById(request.getSaleId())
                .orElseThrow(() -> new RuntimeException("No se puede emitir DTE: Venta no encontrada con ID: " + request.getSaleId()));

        dteControlRepository.findBySaleId(sale.getId()).ifPresent(d -> {
            throw new IllegalStateException("Esta venta ya posee un documento tributario emitido: " + d.getNumeroControl());
        });

        UUID codigoGeneracion = UUID.randomUUID();
        String numeroControl = generarSiguienteNumeroControl(request.getTipoDocumento());
        String selloRecepcion = "2026DTE" + System.currentTimeMillis();

        DteControl dteControl = DteControl.builder()
                .sale(sale)
                .codigoGeneracion(codigoGeneracion)
                .numeroControl(numeroControl)
                .tipoDocumento(request.getTipoDocumento())
                .statusFiscal("APROBADO")
                .selloRecepcion(selloRecepcion)
                .fechaTransmision(LocalDateTime.now())
                .active(true)
                .build();

        DteControl savedDte = dteControlRepository.save(dteControl);

        sale.setStatus("FACTURADA");
        saleRepository.save(sale);

        return convertToResponseDto(savedDte);
    }

    @Override
    @Transactional(readOnly = true)
    public String generarSiguienteNumeroControl(String tipoDocumento) {
        String prefix = "DTE-" + tipoDocumento + "-M001P001-";

        Optional<String> lastControlOpt = dteControlRepository.findLastNumeroControlByTipoDocumento(tipoDocumento);

        long siguienteCorrelativo = 1;
        if (lastControlOpt.isPresent()) {
            String lastControl = lastControlOpt.get();
            try {
                String lastSequenceStr = lastControl.substring(lastControl.length() - 15);
                siguienteCorrelativo = Long.parseLong(lastSequenceStr) + 1;
            } catch (Exception e) {
                siguienteCorrelativo = 1;
            }
        }

        return prefix + String.format("%015d", siguienteCorrelativo);
    }

    @Override
    @Transactional(readOnly = true)
    protected DteControlResponseDTO convertToResponseDto(DteControl entity) {
        String customerName = "Público General";
        String paymentMethod = "Efectivo";
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal ivaTotal = BigDecimal.ZERO;
        String totalLetras = "CERO DÓLARES CON CERO CENTAVOS";

        if (entity.getSale() != null) {
            Sale sale = entity.getSale();
            totalAmount = sale.getTotalAmount();
            ivaTotal = sale.getIvaTotal();
            totalLetras = sale.getTotalLetras();
            paymentMethod = sale.getPaymentMethod();

            if (sale.getCustomer() != null) {
                User c = sale.getCustomer();
                customerName = ((c.getName() != null ? c.getName() : "") + " " +
                        (c.getLastName() != null ? c.getLastName() : "")).trim();
            }
        }

       String urlConsultaCalculada = "https://incp.mh.gob.sv/central/esquemas/pages/ce-consultadte.xhtml?pid=" + entity.getCodigoGeneracion();

        Map<String, Object> mhRawResponseSimulado = new HashMap<>();
        mhRawResponseSimulado.put("version", 1);
        mhRawResponseSimulado.put("ambiente", "PRUEBAS");
        mhRawResponseSimulado.put("clasificacion", "DTE");
        mhRawResponseSimulado.put("estado", entity.getStatusFiscal());
        mhRawResponseSimulado.put("descripcion", "Documento recibido y validado de forma exitosa por los servidores del MH.");
        mhRawResponseSimulado.put("selloRecepcion", entity.getSelloRecepcion());

        return DteControlResponseDTO.builder()
                .dteId(entity.getId())
                .saleId(entity.getSale() != null ? entity.getSale().getId() : null)
                .codigoGeneracion(entity.getCodigoGeneracion().toString())
                .numeroControl(entity.getNumeroControl())
                .tipoDocumento(entity.getTipoDocumento())
                .statusFiscal(entity.getStatusFiscal())
                .selloRecepcion(entity.getSelloRecepcion())
                .fechaTransmision(entity.getFechaTransmision())
                .totalAmount(totalAmount)
                .ivaTotal(ivaTotal)
                .totalLetras(totalLetras)
                .customerName(customerName.isEmpty() ? "Cliente Anonimizado" : customerName)
                .paymentMethod(paymentMethod)
                .urlConsulta(urlConsultaCalculada)
                .dteRespuestaRaw(mhRawResponseSimulado)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DteControlResponseDTO getByCodigoGeneracion(UUID codigoGeneracion) {
        return dteControlRepository.findByCodigoGeneracion(codigoGeneracion)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Documento fiscal no encontrado para el código UUID proporcionado."));
    }

    @Override
    @Transactional(readOnly = true)
    public DteControlResponseDTO getByNumeroControl(String numeroControl) {
        return dteControlRepository.findByNumeroControl(numeroControl)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Documento fiscal no encontrado para el número de control: " + numeroControl));
    }

    @Override
    @Transactional(readOnly = true)
    public DteControlResponseDTO getBySaleId(Long saleId) {
        return dteControlRepository.findBySaleId(saleId)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("No existe ningún registro fiscal asociado a la venta comercial ID: " + saleId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DteControlResponseDTO> getByStatusFiscal(String statusFiscal) {
        return dteControlRepository.findByStatusFiscalAndActiveTrue(statusFiscal)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DteControlResponseDTO> getAll() {
        return dteControlRepository.findAll()
                .stream()
                .filter(DteControl::isActive)
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DteControlResponseDTO getById(Long id) {
        return dteControlRepository.findById(id)
                .filter(DteControl::isActive)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Registro DTE no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        DteControl control = dteControlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro DTE no encontrado con ID: " + id));
        control.setActive(false);
        dteControlRepository.save(control);
    }

    @Override
    protected DteControl convertToEntity(DteControlRequestDTO dto) { return new DteControl(); }

    @Override
    @Transactional
    protected void updateEntityFromDto(DteControlRequestDTO dto, DteControl entity) {}
}