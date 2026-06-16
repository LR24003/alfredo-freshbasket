package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.request.CountryRequestDTO;
import com.group1.proyect.freshbasket.dto.response.CountryResponseDTO;
import com.group1.proyect.freshbasket.entity.Country;
import java.util.List;

public interface CountryService extends GenericService<Country, CountryRequestDTO, CountryResponseDTO, Long> {

    List<CountryResponseDTO> searchCountriesByName(String name);
}