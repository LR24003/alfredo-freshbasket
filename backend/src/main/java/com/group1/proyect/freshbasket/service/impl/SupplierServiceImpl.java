package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.request.SupplierRequestDTO;
import com.group1.proyect.freshbasket.dto.response.SupplierResponseDTO;
import com.group1.proyect.freshbasket.entity.Country;
import com.group1.proyect.freshbasket.entity.Supplier;
import com.group1.proyect.freshbasket.repository.CountryRepository;
import com.group1.proyect.freshbasket.repository.SupplierRepository;
import com.group1.proyect.freshbasket.service.SupplierService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupplierServiceImpl extends GenericServiceImpl<Supplier, SupplierRequestDTO, SupplierResponseDTO, Long> implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final CountryRepository countryRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository,
                               CountryRepository countryRepository) {
        super(supplierRepository);
        this.supplierRepository = supplierRepository;
        this.countryRepository = countryRepository;
    }

    @Override
    protected SupplierResponseDTO convertToResponseDto(Supplier supplier) {
        SupplierResponseDTO dto = new SupplierResponseDTO();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setLastName(supplier.getLastName());
        dto.setEmail(supplier.getEmail());
        dto.setPhone(supplier.getPhone());
        dto.setAddress(supplier.getAddress());
        dto.setCountryId(supplier.getCountry().getId());

        if (supplier.getCountry() != null) {
            dto.setCountryName(supplier.getCountry().getName());
        }

        return dto;
    }

    @Override
    protected Supplier convertToEntity(SupplierRequestDTO dto) {
        Supplier supplier = new Supplier();
        mapDtoToEntityRelations(dto, supplier);
        return supplier;
    }

    @Override
    protected void updateEntityFromDto(SupplierRequestDTO dto, Supplier supplier) {
        mapDtoToEntityRelations(dto, supplier);
    }

    private void mapDtoToEntityRelations(SupplierRequestDTO dto, Supplier supplier) {
        supplier.setName(dto.getName());
        supplier.setLastName(dto.getLastName());
        supplier.setEmail(dto.getEmail());
        supplier.setPhone(dto.getPhone());
        supplier.setAddress(dto.getAddress());

        Country country = countryRepository.findByNameIgnoreCase(dto.getCountryName())
                .orElseThrow(() -> new RuntimeException("País no encontrado con ese ID: " + dto.getCountryName()));

        supplier.setCountry(country);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> getAll() {
        return supplierRepository.findByActiveTrue()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponseDTO getById(Long id) {
        return supplierRepository.findById(id)
                .filter(Supplier::isActive)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ese ID: " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ese ID: " + id));

        supplier.setActive(false);
        supplierRepository.save(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> searchSuppliersByName(String name) {
        return supplierRepository.findByNameIgnoreCase(name)
                .stream()
                .filter(Supplier::isActive)
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
}
