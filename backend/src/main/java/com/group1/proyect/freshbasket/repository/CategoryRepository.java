package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.Category;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends GenericRepository<Category, Long> {

    List<Category> findByNameContainingIgnoreCase(String name);

    Optional<Category> findByNameIgnoreCase(String name);

    List<Category> findByActiveTrue();
}