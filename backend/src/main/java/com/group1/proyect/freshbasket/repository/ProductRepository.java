package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends GenericRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Product p WHERE LOWER(p.category.name) LIKE LOWER(CONCAT('%', :categoryName, '%')) AND p.active = true")
    List<Product> findByCategoryNameIgnoreCaseAndActiveTrue(@Param("categoryName") String categoryName);

    Optional<Product> findByNameIgnoreCase(String name);

    List<Product> findByActiveTrue();

    @Modifying(clearAutomatically = true)
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.currentStock <= p.minStock")
    List<Product> findLowStockProducts();
}