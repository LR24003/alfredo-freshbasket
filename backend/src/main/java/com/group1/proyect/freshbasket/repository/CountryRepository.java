package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.Country;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends GenericRepository<Country, Long> {

    Optional<Country> findByNameIgnoreCase(String name);

    List<Country> findByNameContainingIgnoreCase(String name);

    List<Country> findByActiveTrue();
}