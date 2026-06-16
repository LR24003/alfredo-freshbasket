package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.request.CountryRequestDTO;
import com.group1.proyect.freshbasket.dto.response.CountryResponseDTO;
import com.group1.proyect.freshbasket.entity.Country;
import com.group1.proyect.freshbasket.repository.CountryRepository;
import com.group1.proyect.freshbasket.service.CountryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CountryServiceImpl extends GenericServiceImpl<Country,
        CountryRequestDTO, CountryResponseDTO, Long> implements CountryService {

    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository) {
        super(countryRepository);
        this.countryRepository = countryRepository;
    }

    @Override
    protected CountryResponseDTO convertToResponseDto(Country country) {
        CountryResponseDTO dto = new CountryResponseDTO();
        dto.setId(country.getId());
        dto.setName(country.getName());
        dto.setDescription(country.getDescription());
        return dto;
    }

    @Override
    protected Country convertToEntity(CountryRequestDTO dto) {
        Country country = new Country();
        country.setName(dto.getName() != null ? dto.getName().trim() : null);
        country.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        return country;
    }

    @Override
    protected void updateEntityFromDto(CountryRequestDTO dto, Country country) {
        country.setName(dto.getName() != null ? dto.getName().trim() : country.getName());
        country.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : country.getDescription());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountryResponseDTO> getAll() {
        return countryRepository.findByActiveTrue()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CountryResponseDTO getById(Long id) {
        return countryRepository.findById(id)
                .filter(Country::isActive)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("País no encontrado con ese ID: " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("País no encontrado con ese ID: " + id));

        country.setActive(false);
        countryRepository.save(country);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountryResponseDTO> searchCountriesByName(String name) {
        String cleanName = name != null ? name.trim() : "";
        return countryRepository.findByNameContainingIgnoreCase(cleanName)
                .stream()
                .filter(Country::isActive) // Mantenemos tu filtro de seguridad operativo
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
}