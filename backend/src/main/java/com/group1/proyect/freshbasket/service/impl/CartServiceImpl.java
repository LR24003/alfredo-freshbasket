package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.request.CarritoRequestDTO;
import com.group1.proyect.freshbasket.dto.response.CarritoResponseDTO;
import com.group1.proyect.freshbasket.dto.response.CartResponseDTO;
import com.group1.proyect.freshbasket.entity.Cart;
import com.group1.proyect.freshbasket.entity.Carrito;
import com.group1.proyect.freshbasket.entity.Product;
import com.group1.proyect.freshbasket.entity.User;
import com.group1.proyect.freshbasket.repository.CartRepository;
import com.group1.proyect.freshbasket.repository.CarritoRepository;
import com.group1.proyect.freshbasket.repository.ProductRepository;
import com.group1.proyect.freshbasket.repository.UserRepository;
import com.group1.proyect.freshbasket.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl extends GenericServiceImpl<Cart, CarritoRequestDTO, CartResponseDTO, Long> implements CartService {

    private final CartRepository cartRepository;
    private final CarritoRepository carritoRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CarritoRepository carritoRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository) {
        super(cartRepository);
        this.cartRepository = cartRepository;
        this.carritoRepository = carritoRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected CartResponseDTO convertToResponseDto(Cart cart) {
        if (cart == null) return null;

        List<CarritoResponseDTO> itemDTOs = cart.getItems().stream()
                .filter(Carrito::isActive)
                .map(item -> {
                    BigDecimal price = item.getProduct().getPrice();
                    BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
                    BigDecimal subtotal = price.multiply(quantity);

                    CarritoResponseDTO itemDto = new CarritoResponseDTO();
                    itemDto.setId(item.getId());
                    itemDto.setProductId(item.getProduct().getId());
                    itemDto.setProductName(item.getProduct().getName());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setUnitPrice(price);
                    itemDto.setSubtotal(subtotal);
                    itemDto.setActive(item.isActive());
                    return itemDto;
                })
                .collect(Collectors.toList());

        BigDecimal totalPurchase = itemDTOs.stream()
                .map(CarritoResponseDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartResponseDTO dto = new CartResponseDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser() != null ? cart.getUser().getId() : null);
        dto.setActive(cart.isActive());
        dto.setItems(itemDTOs);
        dto.setTotalPurchase(totalPurchase);
        return dto;
    }

    @Override
    protected Cart convertToEntity(CarritoRequestDTO dto) {
        Cart cart = new Cart();
        cart.setActive(true);
        cart.setItems(new ArrayList<>());
        return cart;
    }

    @Override
    protected void updateEntityFromDto(CarritoRequestDTO dto, Cart cart) {
    }

    @Override
    @Transactional
    public CartResponseDTO getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserIdAndActiveTrue(userId)
                .orElseGet(() -> safeRetrieveOrCreateCart(userId));
        return convertToResponseDto(cart);
    }

    @Override
    @Transactional
    public CartResponseDTO updateItemQuantity(Long userId, CarritoRequestDTO request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (product.getCurrentStock() < request.getQuantity()) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + product.getCurrentStock());
        }

        Cart cart = cartRepository.findByUserIdAndActiveTrue(userId)
                .orElseGet(() -> safeRetrieveOrCreateCart(userId));

        Carrito carritoItem = carritoRepository.findByCartIdAndProductIdAndActiveTrue(cart.getId(), product.getId())
                .orElse(null);

        if (carritoItem != null) {
            carritoItem.setQuantity(request.getQuantity());
            carritoRepository.save(carritoItem);
        } else {
            Carrito nuevoItem = new Carrito();
            nuevoItem.setCart(cart);
            nuevoItem.setProduct(product);
            nuevoItem.setQuantity(request.getQuantity());
            nuevoItem.setActive(true);
            carritoRepository.save(nuevoItem);
        }

        cartRepository.flush();
        Cart updatedCart = cartRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("Error al recuperar el carrito actualizado"));

        return convertToResponseDto(updatedCart);
    }

    @Override
    @Transactional
    public CartResponseDTO removeItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        carritoRepository.findByCartIdAndProductIdAndActiveTrue(cart.getId(), productId)
                .ifPresent(item -> {
                    item.setActive(false);
                    carritoRepository.save(item);
                });

        cartRepository.flush();
        Cart updatedCart = cartRepository.findByUserIdAndActiveTrue(userId).get();
        return convertToResponseDto(updatedCart);
    }

    @Override
    @Transactional
    public void checkout(Long userId) {
        Cart cart = cartRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("No tienes un carrito activo"));

        List<Carrito> itemsActivos = cart.getItems().stream()
                .filter(Carrito::isActive)
                .collect(Collectors.toList());

        if (itemsActivos.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        for (Carrito item : itemsActivos) {
            Product product = item.getProduct();

            if (product.getCurrentStock() < item.getQuantity()) {
                throw new RuntimeException("El producto " + product.getName() + " no tiene stock suficiente.");
            }


            product.setCurrentStock(product.getCurrentStock() - item.getQuantity());
            productRepository.save(product);

            item.setActive(false);
            carritoRepository.save(item);
        }
        
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Transactional
    private Cart safeRetrieveOrCreateCart(Long userId) {
        return cartRepository.findByUserIdAndActiveTrue(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setActive(true);
                    newCart.setItems(new ArrayList<>());

                    try {
                        return cartRepository.saveAndFlush(newCart);
                    } catch (Exception e) {
                        return cartRepository.findByUserIdAndActiveTrue(userId)
                                .orElseThrow(() -> new RuntimeException("Error crítico de unicidad en carrito"));
                    }
                });
    }
}