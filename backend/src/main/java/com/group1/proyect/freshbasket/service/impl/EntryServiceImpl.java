package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.request.EntryRequestDTO;
import com.group1.proyect.freshbasket.dto.response.EntryResponseDTO;
import com.group1.proyect.freshbasket.entity.*;
import com.group1.proyect.freshbasket.repository.EntryRepository;
import com.group1.proyect.freshbasket.repository.ProductRepository;
import com.group1.proyect.freshbasket.repository.SupplierRepository;
import com.group1.proyect.freshbasket.repository.UserRepository;
import com.group1.proyect.freshbasket.service.EntryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EntryServiceImpl extends GenericServiceImpl<Entry,
        EntryRequestDTO, EntryResponseDTO, Long> implements EntryService {

    private final EntryRepository entryRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;

    public EntryServiceImpl(
            EntryRepository entryRepository,
            ProductRepository productRepository,
            SupplierRepository supplierRepository,
            UserRepository userRepository) {
        super(entryRepository);
        this.entryRepository = entryRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
    }


    @Override
    protected EntryResponseDTO convertToResponseDto(Entry entry) {
        EntryResponseDTO dto = new EntryResponseDTO();
        dto.setId(entry.getId());
        dto.setEntryDate(entry.getEntryDate());
        dto.setUnitCost(entry.getUnitCost());
        dto.setQuantity(entry.getQuantity());

        if (entry.getProduct() != null) {
            dto.setProductId(entry.getProduct().getId());
            dto.setProductName(entry.getProduct().getName());
        } else {
            dto.setProductName("Sin producto asignado");
        }

        if (entry.getSupplier() != null) {
            dto.setSupplierId(entry.getSupplier().getId());
            String sName = entry.getSupplier().getName() != null ? entry.getSupplier().getName() : "";
            String sLastName = entry.getSupplier().getLastName() != null ? entry.getSupplier().getLastName() : "";
            String sFullName = (sName + " " + sLastName).trim();
            dto.setSupplierName(!sFullName.isEmpty() ? sFullName : "Proveedor " + entry.getSupplier().getId());
        } else {
            dto.setSupplierName("Sin proveedor asignado");
        }

        if (entry.getUser() != null) {
            dto.setUserId(entry.getUser().getId());
            String uName = entry.getUser().getName() != null ? entry.getUser().getName() : "";
            String uLastName = entry.getUser().getLastName() != null ? entry.getUser().getLastName() : "";
            String uFullName = (uName + " " + uLastName).trim();
            dto.setUserName(!uFullName.isEmpty() ? uFullName : "Usuario " + entry.getUser().getId());
        } else {
            dto.setUserName("Sin usuario asignado");
        }

        return dto;
    }

    @Override
    protected Entry convertToEntity(EntryRequestDTO dto) {
        Entry entry = new Entry();
        entry.setUnitCost(dto.getUnitCost());
        entry.setQuantity(dto.getQuantity());
        mapRelationsFromDto(dto, entry);
        return entry;
    }

    @Override
    protected void updateEntityFromDto(EntryRequestDTO dto, Entry entry) {
        mapRelationsFromDto(dto, entry);
    }

    private void mapRelationsFromDto(EntryRequestDTO dto, Entry entry) {
        String cleanProductName = dto.getProductName() != null ? dto.getProductName().trim() : "";
        Product product = productRepository.findByNameIgnoreCase(cleanProductName)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ese nombre: " + dto.getProductName()));

        String cleanSupplierName = dto.getSupplierName() != null ? dto.getSupplierName().trim() : "";
        Supplier supplier = supplierRepository.findByFullNameIgnoreCase(cleanSupplierName)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con el nombre completo: " + dto.getSupplierName()));

        String cleanUserName = dto.getUserName() != null ? dto.getUserName().trim() : "";
        User user = userRepository.findByFullNameIgnoreCase(cleanUserName)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el nombre completo: " + dto.getUserName()));

        entry.setProduct(product);
        entry.setSupplier(supplier);
        entry.setUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntryResponseDTO> getAll() {
        return entryRepository.findByActiveTrue()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EntryResponseDTO getById(Long id) {
        return entryRepository.findById(id)
                .filter(Entry::isActive)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada con ese ID: " + id));
    }

    @Override
    @Transactional
    public EntryResponseDTO create(EntryRequestDTO requestDTO) {
        Entry entry = convertToEntity(requestDTO);
        Product product = entry.getProduct();

        if (product != null) {
            if (!product.isActive()) {
                throw new IllegalStateException("No se pueden registrar entradas para un producto eliminado.");
            }

            product.setCurrentStock(product.getCurrentStock() + entry.getQuantity());
            product.setPrice(entry.getUnitCost());
            productRepository.save(product);
        }

        Entry savedEntry = entryRepository.save(entry);
        return convertToResponseDto(savedEntry);
    }

    @Override
    @Transactional
    public EntryResponseDTO update(Long id, EntryRequestDTO requestDTO) {
        Entry entry = entryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada con ese ID: " + id));

        updateEntityFromDto(requestDTO, entry);
        Product product = entry.getProduct();

        if (!product.isActive()) {
            throw new IllegalStateException("No se puede modificar esta entrada porque el producto asociado está eliminado.");
        }

        int cantidadAnterior = entry.getQuantity();
        int cantidadNueva = requestDTO.getQuantity();
        int diferencia = cantidadNueva - cantidadAnterior;

        product.setCurrentStock(product.getCurrentStock() + diferencia);
        product.setPrice(requestDTO.getUnitCost());
        productRepository.save(product);

        entry.setQuantity(cantidadNueva);
        entry.setUnitCost(requestDTO.getUnitCost());

        Entry updated = entryRepository.save(entry);
        return convertToResponseDto(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Entry entry = entryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada con ese ID: " + id));

        Product product = entry.getProduct();
        if (product != null) {
            if (!product.isActive()) {
                throw new IllegalStateException("No se puede eliminar esta entrada porque pertenece a un producto eliminado.");
            }

            product.setCurrentStock(product.getCurrentStock() - entry.getQuantity());
            productRepository.save(product);
        }

        entry.setActive(false);
        entryRepository.save(entry);
    }
}