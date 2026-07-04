package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.request.ExitRequestDTO;
import com.group1.proyect.freshbasket.dto.request.SaleRequestDTO;
import com.group1.proyect.freshbasket.dto.request.SaleDetailsRequestDTO;
import com.group1.proyect.freshbasket.dto.response.SaleResponseDTO;
import com.group1.proyect.freshbasket.dto.response.SaleDetailsResponseDTO;
import com.group1.proyect.freshbasket.entity.Sale;
import com.group1.proyect.freshbasket.entity.SaleDetails;
import com.group1.proyect.freshbasket.entity.Product;
import com.group1.proyect.freshbasket.entity.User;
import com.group1.proyect.freshbasket.repository.SaleDetailsRepository;
import com.group1.proyect.freshbasket.repository.SaleRepository;
import com.group1.proyect.freshbasket.repository.UserRepository;
import com.group1.proyect.freshbasket.repository.ProductRepository;
import com.group1.proyect.freshbasket.service.ExitService;
import com.group1.proyect.freshbasket.service.SaleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SaleServiceImpl extends GenericServiceImpl<Sale, SaleRequestDTO, SaleResponseDTO, Long> implements SaleService {

    private final SaleRepository saleRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ExitService exitService;
    private final SaleDetailsRepository saleDetailsRepository;

    public SaleServiceImpl(
            SaleRepository saleRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            ExitService exitService,
            SaleDetailsRepository saleDetailsRepository) {
        super(saleRepository);
        this.saleRepository = saleRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.exitService = exitService;
        this.saleDetailsRepository = saleDetailsRepository;
    }

    @Override
    @Transactional
    public SaleResponseDTO create(SaleRequestDTO dto) {
        Sale sale = convertToEntity(dto);
        sale.setActive(true);

        if (dto.getDetails() == null || dto.getDetails().isEmpty()) {
            throw new IllegalArgumentException("No se puede registrar una venta sin productos en el carrito.");
        }

        List<SaleDetails> entitiesDetails = new ArrayList<>();

        for (SaleDetailsRequestDTO detailDto : dto.getDetails()) {
            Product product = productRepository.findByNameIgnoreCase(detailDto.getProductName().trim())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con el nombre: " + detailDto.getProductName()));

            SaleDetails details = new SaleDetails();
            details.setSale(sale);
            details.setProduct(product);
            details.setQuantity(detailDto.getQuantity());
            details.setUnitCost(detailDto.getUnitCost());
            details.setDiscount(detailDto.getDiscount());
            details.setTipoItemExento(detailDto.getTipoItemExento());
            details.setUnidadMedidaCodigo(detailDto.getUnidadMedidaCodigo());
            details.setIvaItem(detailDto.getIvaItem());
            details.setActive(true);

            entitiesDetails.add(details);
        }

        sale.setDetails(entitiesDetails);
        Sale savedSale = saleRepository.save(sale);

        for (SaleDetails savedDetails : savedSale.getDetails()) {
            ExitRequestDTO exitDTO = new ExitRequestDTO();
            exitDTO.setExitReason("VENTA");
            exitDTO.setSaleId(savedSale.getId());
            exitDTO.setQuantity(savedDetails.getQuantity());
            exitDTO.setProductName(savedDetails.getProduct().getName());

            String employeeName = savedSale.getEmployee() != null
                    ? (savedSale.getEmployee().getName() + " " + savedSale.getEmployee().getLastName()).trim()
                    : "Sistema / Autoventa";
            exitDTO.setUserName(employeeName);

            exitService.create(exitDTO);
        }

        return convertToResponseDto(savedSale);
    }

    @Override
    protected SaleResponseDTO convertToResponseDto(Sale sale) {
        SaleResponseDTO dto = new SaleResponseDTO();
        dto.setId(sale.getId());
        dto.setSaleDate(sale.getSaleDate());
        dto.setTotalAmount(sale.getTotalAmount());
        dto.setPaymentMethod(sale.getPaymentMethod());
        dto.setStatus(sale.getStatus());
        dto.setCondicionOperacion(sale.getCondicionOperacion());
        dto.setIvaTotal(sale.getIvaTotal());
        dto.setTotalLetras(sale.getTotalLetras());

        if (sale.getCustomer() != null) {
            String cName = sale.getCustomer().getName() != null ? sale.getCustomer().getName() : "";
            String cLastName = sale.getCustomer().getLastName() != null ? sale.getCustomer().getLastName() : "";
            String cFullName = (cName + " " + cLastName).trim();
            dto.setCustomerName(!cFullName.isEmpty() ? cFullName : "Cliente #" + sale.getCustomer().getId());
        } else {
            dto.setCustomerName("Público General / No asignado");
        }

        if (sale.getEmployee() != null) {
            String eName = sale.getEmployee().getName() != null ? sale.getEmployee().getName() : "";
            String eLastName = sale.getEmployee().getLastName() != null ? sale.getEmployee().getLastName() : "";
            String eFullName = (eName + " " + eLastName).trim();
            dto.setEmployeeName(!eFullName.isEmpty() ? eFullName : "Empleado " + sale.getEmployee().getId());
        } else {
            dto.setEmployeeName("Carrito Virtual");
        }

        if (sale.getDetails() != null) {
            List<SaleDetailsResponseDTO> detailsDtos = sale.getDetails().stream()
                    .map(detail -> {
                        SaleDetailsResponseDTO d = new SaleDetailsResponseDTO();
                        d.setId(detail.getId());
                        d.setSaleId(sale.getId());
                        if (detail.getProduct() != null) {
                            d.setProductId(detail.getProduct().getId());
                            d.setProductName(detail.getProduct().getName());
                        } else {
                            d.setProductName("Producto no disponible");
                        }
                        d.setQuantity(detail.getQuantity());
                        d.setUnitCost(detail.getUnitCost());
                        d.setDiscount(detail.getDiscount());

                        d.setTipoItemExento(detail.getTipoItemExento());
                        d.setUnidadMedidaCodigo(detail.getUnidadMedidaCodigo());
                        d.setIvaItem(detail.getIvaItem());
                        return d;
                    })
                    .collect(Collectors.toList());
            dto.setDetails(detailsDtos);
        }

        return dto;
    }

    @Override
    protected Sale convertToEntity(SaleRequestDTO dto) {
        Sale sale = new Sale();
        sale.setTotalAmount(dto.getTotalAmount());
        sale.setPaymentMethod(dto.getPaymentMethod());
        sale.setStatus(dto.getStatus());
        sale.setCondicionOperacion(dto.getCondicionOperacion());
        sale.setIvaTotal(dto.getIvaTotal());
        sale.setTotalLetras(dto.getTotalLetras());

        mapRelationsFromDto(dto, sale);
        return sale;
    }

    @Override
    protected void updateEntityFromDto(SaleRequestDTO dto, Sale sale) {
        sale.setTotalAmount(dto.getTotalAmount());
        sale.setPaymentMethod(dto.getPaymentMethod());
        sale.setStatus(dto.getStatus());
        sale.setCondicionOperacion(dto.getCondicionOperacion());
        sale.setIvaTotal(dto.getIvaTotal());
        sale.setTotalLetras(dto.getTotalLetras());

        mapRelationsFromDto(dto, sale);
    }

    private void mapRelationsFromDto(SaleRequestDTO dto, Sale sale) {
        User customer = userRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con el ID: " + dto.getCustomerId()));

        User employee = userRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con el ID: " + dto.getEmployeeId()));

        sale.setCustomer(customer);
        sale.setEmployee(employee);
    }

    @Override
    public List<SaleResponseDTO> getAll() {
        return saleRepository.findByActiveTrue()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public SaleResponseDTO getById(Long id) {
        return saleRepository.findById(id)
                .filter(Sale::isActive)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ese ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDTO> getSalesByStatus(String status) {
        return saleRepository.findByStatusAndActiveTrue(status)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDTO> getSalesByCustomerId(Long customerId) {
        return saleRepository.findByCustomerIdAndActiveTrue(customerId)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SaleDetailsResponseDTO> getDetailsBySaleId(Long saleId) {
        return saleDetailsRepository.findBySaleId(saleId)
                .stream()
                .filter(SaleDetails::isActive)
                .map(detail -> {
                    SaleDetailsResponseDTO d = new SaleDetailsResponseDTO();
                    d.setId(detail.getId());
                    d.setSaleId(saleId);
                    if (detail.getProduct() != null) {
                        d.setProductId(detail.getProduct().getId());
                        d.setProductName(detail.getProduct().getName());
                    } else {
                        d.setProductName("Producto no disponible");
                    }
                    d.setQuantity(detail.getQuantity());
                    d.setUnitCost(detail.getUnitCost());
                    d.setDiscount(detail.getDiscount());

                    d.setTipoItemExento(detail.getTipoItemExento());
                    d.setUnidadMedidaCodigo(detail.getUnidadMedidaCodigo());
                    d.setIvaItem(detail.getIvaItem());
                    return d;
                })
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDTO> getSalesByDateRange(LocalDateTime start, LocalDateTime end) {
        return saleRepository.findBySaleDateBetweenAndActiveTrue(start, end)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getDailyTotalSales() {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay(); // 2026-XX-XX 00:00:00
        return saleRepository.calculateDailyTotal(inicioDia).orElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ese ID: " + id));
        sale.setActive(false);

        if (sale.getDetails() != null) {
            sale.getDetails().forEach(detail -> detail.setActive(false));
        }

        saleRepository.save(sale);
    }
}