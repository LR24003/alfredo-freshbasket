package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.request.EntryRequestDTO;
import com.group1.proyect.freshbasket.dto.response.EntryResponseDTO;
import com.group1.proyect.freshbasket.entity.Entry;
import com.group1.proyect.freshbasket.entity.Product;
import com.group1.proyect.freshbasket.entity.Supplier;
import com.group1.proyect.freshbasket.entity.User;
import com.group1.proyect.freshbasket.repository.EntryRepository;
import com.group1.proyect.freshbasket.repository.ProductRepository;
import com.group1.proyect.freshbasket.repository.SupplierRepository;
import com.group1.proyect.freshbasket.repository.UserRepository;
import com.group1.proyect.freshbasket.service.EntryService;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EntryServiceImpl implements EntryService {

    private final EntryRepository entryRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;

    public EntryServiceImpl(
            EntryRepository entryRepository,
            ProductRepository productRepository,
            SupplierRepository supplierRepository,
            UserRepository userRepository) {
        this.entryRepository = entryRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
    }

    // DTO → Entity
    private Entry convertToEntity(EntryRequestDTO dto) {
        Entry entry = new Entry();

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        entry.setUnitCost(dto.getUnitCost());
        entry.setQuantity(dto.getQuantity());
        entry.setProduct(product);
        entry.setSupplier(supplier);
        entry.setUser(user);

        return entry;
    }
    // Entity → DTO
    private EntryResponseDTO convertToDTO(Entry entry) {
        EntryResponseDTO dto = new EntryResponseDTO();

        dto.setId(entry.getId());
        dto.setUnitCost(entry.getUnitCost());
        dto.setQuantity(entry.getQuantity());
        dto.setProductId(entry.getProduct().getId());
        dto.setSupplierId(entry.getSupplier().getId());
        dto.setUserId(entry.getUser().getId());


        return dto;
    }

    @Override
    @Transactional (readOnly = true)
    public EntryResponseDTO getEntryById(Long id) {
        return entryRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada con ID: " + id));
    }

    @Override
    public List<EntryResponseDTO> getAllEntries() {
        return entryRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public EntryResponseDTO createEntry(EntryRequestDTO requestDTO) {
        Entry entry = convertToEntity(requestDTO);

        // Actualizar stock del producto
        Product product = entry.getProduct();
        if (product != null) {
            int nuevoStock = product.getCurrentStock() + entry.getQuantity();
            product.setCurrentStock(nuevoStock);

            // Actualizar precio con el costo unitario de la entrada
            product.setPrice(entry.getUnitCost());

            productRepository.save(product);
        }

        Entry savedEntry = entryRepository.save(entry);
        return convertToDTO(savedEntry);
    }


    @Override
    public EntryResponseDTO updateEntry(Long id, EntryRequestDTO requestDTO) {
        Entry entry = entryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada con ese ID"));

        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("ID del Producto no encontrado"));

        Supplier supplier = supplierRepository.findById(requestDTO.getSupplierId())
                .orElseThrow(() -> new RuntimeException("ID del Proveedor no encontrado"));

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("ID del Usuario no encontrado"));

        // Ajustamos el inventario al actualizar una entrada (si se actualiza el campo de cantidad)
        int cantidadAnterior = entry.getQuantity();
        int cantidadNueva = requestDTO.getQuantity();
        int diferencia = cantidadNueva - cantidadAnterior;

        int nuevoStock = product.getCurrentStock() + diferencia;
        product.setCurrentStock(nuevoStock);

        // Actualizar precio con el nuevo costo unitario
        product.setPrice(requestDTO.getUnitCost());

        productRepository.save(product);

        // Actualizar datos de la entrada
        entry.setQuantity(cantidadNueva);
        entry.setUnitCost(requestDTO.getUnitCost());
        entry.setProduct(product);
        entry.setSupplier(supplier);
        entry.setUser(user);

        Entry updated = entryRepository.save(entry);
        return convertToDTO(updated);
    }



    @Override
    public void deleteEntry(Long id) {
        Entry entry = entryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada con ese ID: " + id));

        //Al eliminar una entrada tambien debe de disminuir el inventario.
        Product product = entry.getProduct();
        if (product != null) {
            int nuevoStock = product.getCurrentStock() - entry.getQuantity();
            product.setCurrentStock(nuevoStock);
            productRepository.save(product);
        }
        //Borramos la entidad encontrada
        entryRepository.deleteById(id);

        //sincronización inmediata
        entryRepository.flush();
    }

}