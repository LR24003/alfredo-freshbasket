package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.request.CategoryRequestDTO;
import com.group1.proyect.freshbasket.dto.response.CategoryResponseDTO;
import com.group1.proyect.freshbasket.entity.Category;
import java.util.List;

public interface CategoryService extends GenericService<Category,
        CategoryRequestDTO, CategoryResponseDTO, Long> {

    List<CategoryResponseDTO> searchCategoriesByName(String name);
}