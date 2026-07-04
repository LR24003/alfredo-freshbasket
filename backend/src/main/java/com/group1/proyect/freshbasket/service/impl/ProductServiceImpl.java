package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.request.ProductRequestDTO;
import com.group1.proyect.freshbasket.dto.response.ProductResponseDTO;
import com.group1.proyect.freshbasket.entity.*;
import com.group1.proyect.freshbasket.repository.*;
import com.group1.proyect.freshbasket.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl extends GenericServiceImpl<Product, ProductRequestDTO, ProductResponseDTO, Long> implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final EntryRepository entryRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              SupplierRepository supplierRepository,
                              UserRepository userRepository,
                              EntryRepository entryRepository) {
        super(productRepository);
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
        this.entryRepository = entryRepository;
    }

    // DTO to Entity
    @Override
    protected ProductResponseDTO convertToResponseDto(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setCurrentStock(product.getCurrentStock());
        dto.setDescription(product.getDescription());
        dto.setImageUrl(product.getImageUrl());
        dto.setMinStock(product.getMinStock());
        dto.setDiscount(product.getDiscount());
        dto.setActive(product.isActive());
        dto.setTipoItem(product.getTipoItem());
        dto.setTipoImpuestoDefecto(product.getTipoImpuestoDefecto());
        dto.setUnidadMedidaDefecto(product.getUnidadMedidaDefecto());

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        } else {
            dto.setCategoryName("Sin categoría asignada");
        }

        if (product.getSupplier() != null) {
            dto.setSupplierId(product.getSupplier().getId());
            String sName = product.getSupplier().getName() != null ? product.getSupplier().getName() : "";
            String sLastName = product.getSupplier().getLastName() != null ? product.getSupplier().getLastName() : "";
            String sFullName = (sName + " " + sLastName).trim();
            dto.setSupplierName(!sFullName.isEmpty() ? sFullName : "Proveedor " + product.getSupplier().getId());
        } else {
            dto.setSupplierName("Sin proveedor asignado");
        }

        if (product.getUser() != null) {
            dto.setUserId(product.getUser().getId());
            String uName = product.getUser().getName() != null ? product.getUser().getName() : "";
            String uLastName = product.getUser().getLastName() != null ? product.getUser().getLastName() : "";
            String uFullName = (uName + " " + uLastName).trim();
            dto.setUserName(!uFullName.isEmpty() ? uFullName : "Usuario " + product.getUser().getId());
        } else {
            dto.setUserName("Sin usuario asignado");
        }
        return dto;
    }


    @Override
    protected Product convertToEntity(ProductRequestDTO dto) {
        Product product = new Product();
        product.setActive(true);
        mapDtoToEntityRelations(dto, product);
        return product;
    }

    @Override
    protected void updateEntityFromDto(ProductRequestDTO dto, Product product) {
        mapDtoToEntityRelations(dto, product);
    }

    // Entity a DTO
    private void mapDtoToEntityRelations(ProductRequestDTO dto, Product product) {
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setCurrentStock(dto.getCurrentStock());
        product.setDescription(dto.getDescription());
        product.setImageUrl(dto.getImageUrl());
        product.setMinStock(dto.getMinStock());
        product.setDiscount(dto.getDiscount());
        product.setTipoItem(dto.getTipoItem());
        product.setTipoImpuestoDefecto(dto.getTipoImpuestoDefecto());
        product.setUnidadMedidaDefecto(dto.getUnidadMedidaDefecto());

        String cleanCategoryName = dto.getCategoryName() != null ? dto.getCategoryName().trim() : "";
        Category category = categoryRepository.findByNameIgnoreCase(cleanCategoryName)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ese nombre: " + dto.getCategoryName()));

        String cleanSupplierName = dto.getSupplierName() != null ? dto.getSupplierName().trim() : "";
        Supplier supplier = supplierRepository.findByFullNameIgnoreCase(cleanSupplierName)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con el nombre completo: " + dto.getSupplierName()));

        String cleanUserName = dto.getUserName() != null ? dto.getUserName().trim() : "";
        User user = userRepository.findByFullNameIgnoreCase(cleanUserName)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el nombre completo: " + dto.getUserName()));

        product.setCategory(category);
        product.setSupplier(supplier);
        product.setUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAll() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getById(Long id) {
        return productRepository.findById(id)
                .filter(Product::isActive)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado o inactivo con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getProductsByCategory(String categoryName) {
        return productRepository.findByCategoryNameIgnoreCaseAndActiveTrue(categoryName)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public ProductResponseDTO create(ProductRequestDTO requestDTO) {
        Product product = convertToEntity(requestDTO);
        int stockInicial = requestDTO.getCurrentStock() != null ? requestDTO.getCurrentStock() : 0;
        product.setCurrentStock(stockInicial);

        Product savedProduct = productRepository.save(product);

        if (stockInicial > 0) {
            Entry entry = new Entry();
            entry.setProduct(savedProduct);
            entry.setSupplier(savedProduct.getSupplier());
            entry.setUser(savedProduct.getUser());
            entry.setQuantity(stockInicial);
            entry.setUnitCost(savedProduct.getPrice());
            entry.setEntryDate(LocalDateTime.now());
            entryRepository.save(entry);
        }
        return convertToResponseDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO requestDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ese ID: " + id));

        updateEntityFromDto(requestDTO, existingProduct);
        Product updatedProduct = productRepository.save(existingProduct);
        return convertToResponseDto(updatedProduct);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ese ID: " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .filter(Product::isActive)
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getLowStockAlerts() {
        return productRepository.findLowStockProducts()
                .stream()
                .filter(Product::isActive)
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
}