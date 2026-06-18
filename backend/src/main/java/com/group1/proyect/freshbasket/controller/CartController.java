package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.dto.request.CarritoRequestDTO;
import com.group1.proyect.freshbasket.dto.response.CartResponseDTO;
import com.group1.proyect.freshbasket.entity.Cart;
import com.group1.proyect.freshbasket.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "API optimizada para la gestión del carrito de compras y la tabla Carrito")
public class CartController extends GenericController<Cart, CarritoRequestDTO, CartResponseDTO, Long> {

    private final CartService cartService;

    public CartController(CartService cartService) {
        super(cartService, "carrito");
        this.cartService = cartService;
    }

    @Operation(
            summary = "Obtener el carrito activo de un usuario",
            description = "Recupera la cabecera del carrito de un usuario específico junto con sus filas de 'Carrito' activas y totales calculados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito obtenido con éxito"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponseDTO> getCartByUserId(
            @Parameter(description = "ID del usuario dueño del carrito", example = "1", required = true)
            @PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @Operation(
            summary = "Agregar producto o actualizar su cantidad",
            description = "Inserta una fila o modifica las unidades de un producto en la tabla 'Carrito'. Valida stock físico real."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tabla Carrito actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente o datos inválidos")
    })
    @PostMapping("/user/{userId}/items")
    public ResponseEntity<CartResponseDTO> updateItemQuantity(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Long userId,
            @Valid @RequestBody CarritoRequestDTO request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, request));
    }

    @Operation(
            summary = "Remover un producto del carrito (Borrado lógico)",
            description = "Desactiva lógicamente (active = false) una fila específica de la tabla 'Carrito' perteneciente al usuario."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto removido exitosamente del carrito"),
            @ApiResponse(responseCode = "404", description = "Carrito o producto no encontrado")
    })
    @DeleteMapping("/user/{userId}/products/{productId}")
    public ResponseEntity<CartResponseDTO> removeItem(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Long userId,
            @Parameter(description = "ID del producto a remover", example = "5", required = true)
            @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItem(userId, productId));
    }

    @Operation(
            summary = "Finalizar la compra (Checkout)",
            description = "Confirma el carrito de compras actual: descuenta de forma definitiva el inventario físico y deshabilita el carrito de manera lógica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compra procesada con éxito e inventarios actualizados"),
            @ApiResponse(responseCode = "400", description = "El carrito está vacío o algún producto se quedó sin stock de último minuto")
    })
    @PostMapping("/user/{userId}/checkout")
    public ResponseEntity<Void> checkout(
            @Parameter(description = "ID del usuario que realiza el pago", example = "1", required = true)
            @PathVariable Long userId) {
        cartService.checkout(userId);
        return ResponseEntity.ok().build();
    }
}