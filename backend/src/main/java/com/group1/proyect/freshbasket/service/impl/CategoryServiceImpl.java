package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.request.CategoryRequestDTO;
import com.group1.proyect.freshbasket.dto.response.CategoryResponseDTO;
import com.group1.proyect.freshbasket.entity.Category;
import com.group1.proyect.freshbasket.repository.CategoryRepository;
import com.group1.proyect.freshbasket.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl extends GenericServiceImpl<Category,
        CategoryRequestDTO, CategoryResponseDTO, Long> implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        super(categoryRepository);
        this.categoryRepository = categoryRepository;
    }

    @Override
    protected CategoryResponseDTO convertToResponseDto(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    @Override
    protected Category convertToEntity(CategoryRequestDTO dto) {
        Category category = new Category();
        category.setName(dto.getName() != null ? dto.getName().trim() : null);
        category.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        return category;
    }

    @Override
    protected void updateEntityFromDto(CategoryRequestDTO dto, Category category) {
        category.setName(dto.getName() != null ? dto.getName().trim() : category.getName());
        category.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : category.getDescription());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAll() {
        return categoryRepository.findByActiveTrue()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO getById(Long id) {
        return categoryRepository.findById(id)
                .filter(Category::isActive)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ese ID: " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ese ID: " + id));

        category.setActive(false);
        categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> searchCategoriesByName(String name) {
        String cleanName = name != null ? name.trim() : "";
        return categoryRepository.findByNameContainingIgnoreCase(cleanName)
                .stream()
                .filter(Category::isActive)
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
}