package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.request.CarritoRequestDTO;
import com.group1.proyect.freshbasket.dto.response.CartResponseDTO;
import com.group1.proyect.freshbasket.entity.Cart;

public interface CartService extends GenericService<Cart, CarritoRequestDTO, CartResponseDTO, Long> {

    CartResponseDTO getCartByUserId(Long userId);

    CartResponseDTO updateItemQuantity(Long userId, CarritoRequestDTO request);


    CartResponseDTO removeItem(Long userId, Long productId);

    
    void checkout(Long userId);
}