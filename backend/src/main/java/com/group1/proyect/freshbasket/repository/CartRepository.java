package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.Cart;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartRepository extends GenericRepository<Cart, Long> {

    Optional<Cart> findByUserIdAndActiveTrue(Long userId);
}