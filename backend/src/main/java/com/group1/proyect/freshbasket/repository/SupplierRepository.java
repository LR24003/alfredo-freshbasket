package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.Supplier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends GenericRepository<Supplier, Long> {

    List<Supplier> findByNameContainingIgnoreCase(String name);

    @Query("SELECT s FROM Supplier s WHERE LOWER(TRIM(CONCAT(s.name, ' ', COALESCE(s.lastName, '')))) = LOWER(TRIM(:fullName))")
    Optional<Supplier> findByFullNameIgnoreCase(@Param("fullName") String fullName);

    List<Supplier> findByActiveTrue();
}