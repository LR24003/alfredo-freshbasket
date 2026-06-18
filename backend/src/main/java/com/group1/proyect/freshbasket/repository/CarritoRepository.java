package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.Carrito;

import java.util.Optional;

public interface CarritoRepository extends GenericRepository<Carrito, Long> {

    Optional<Carrito> findByCartIdAndProductIdAndActiveTrue(Long cartId, Long productId);
}
