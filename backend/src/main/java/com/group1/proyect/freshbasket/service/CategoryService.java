package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.request.CategoryRequestDTO;
import com.group1.proyect.freshbasket.dto.response.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {

    List<CategoryResponseDTO> getAllCategories();

    CategoryResponseDTO getCategoryById(Long id);

    CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO);

    CategoryResponseDTO updateCategory (Long id, CategoryRequestDTO requestDTO);

    void deleteCategory(Long id);

    List<CategoryResponseDTO> searchCategoriesByName(String name);
}
