package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.entity.Product;
import com.group1.proyect.freshbasket.dto.request.ProductRequestDTO;
import com.group1.proyect.freshbasket.dto.response.ProductResponseDTO;

import java.util.List;

public interface ProductService extends GenericService<Product, ProductRequestDTO, ProductResponseDTO, Long> {

    List<ProductResponseDTO> searchProductsByName(String name);

    List<ProductResponseDTO> getLowStockAlerts();
}