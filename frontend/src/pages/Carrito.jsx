import React from 'react';
import { useCart } from '../hooks/useCart';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import '../styles/cart.css';

function Cart() {

    const { cart, isLoading, isError, updateQuantity, removeItem, checkoutAsync, isCheckingOut } = useCart();
    const navigate = useNavigate();

    if (isLoading) {
        return (
            <div className="fb-cart-loading">
                <div className="fb-spinner"></div>
                <p>Cargando tu canasta fresca...</p>
            </div>
        );
    }

    const itemsList = cart?.items || [];
    const totalArticulos = itemsList.length;
    if (isError || !cart || totalArticulos === 0) {
        return (
            <div className="fb-cart-empty-container">
                <div className="fb-cart-empty-card">
                    <i className="bi bi-cart-x fb-cart-empty-icon" />
                    <h2 className="fb-cart-empty-title">Tu carrito está vacío</h2>
                    <p className="fb-cart-empty-text">¡Date una vuelta por nuestra sección de productos frescos!</p>
                    <button
                        onClick={() => navigate('/freshbasket/productos')}
                        className="fb-btn fb-btn-success fb-btn-lg"
                    >
                        Ir a productos
                    </button>
                </div>
            </div>
        );
    }

    const handleRestarCantidad = (item) => {
        if (item.quantity <= 1) {
            removeItem(item.productId);
        } else {
            updateQuantity({ productId: item.productId, quantity: item.quantity - 1 });
            toast.loading('Actualizando...', { id: 'qty-toast', duration: 800 });
        }
    };

    const handleSumarCantidad = (item) => {
        updateQuantity({ productId: item.productId, quantity: item.quantity + 1 });
        toast.loading('Actualizando...', { id: 'qty-toast', duration: 800 });
    };

    const handleQuitarItem = (item) => {
        removeItem(item.productId);
    };

    const handleCheckout = () => {
        toast.promise(
            checkoutAsync(),
            {
                loading: 'Procesando tu pago seguro...',
                success: '¡Compra realizada con éxito! Tu orden está en camino...',
                error: (err) => err.response?.data?.message || 'Error al procesar el pago. Inténtalo de nuevo.'
            },
            {
                style: {
                    minWidth: '350px',
                    borderRadius: '10px',
                    background: '#333',
                    color: '#fff',
                }
            }
        );
    };

    return (
        <div className="fb-cart-wrapper">
            <h1 className="fb-cart-main-title">
                <i className="bi bi-basket3-fill" /> Resumen de los productos
            </h1>

            <div className="fb-cart-grid">
                {/* LISTA DE ARTÍCULOS */}
                <div className="fb-cart-items-column">
                    {itemsList.map((item) => (
                        <div key={item.id || item.productId} className="fb-cart-item-card">
                            <div className="fb-cart-item-info">
                                <h4 className="fb-cart-item-name">{item.productName || "Producto"}</h4>
                                <p className="fb-cart-item-price">Precio unitario: <span>${Number(item.unitPrice || 0).toFixed(2)}</span></p>
                                <p className="fb-cart-item-subtotal">Subtotal: <span>${Number(item.subtotal || 0).toFixed(2)}</span></p>
                            </div>

                            {/* CONTROLES DE CANTIDAD Y ELIMINACIÓN */}
                            <div className="fb-cart-item-actions">
                                <div className="fb-quantity-controls">
                                    <button
                                        onClick={() => handleRestarCantidad(item)}
                                        className="fb-btn-qty"
                                        title="Restar cantidad"
                                        disabled={isCheckingOut}
                                    >
                                        <i className="bi bi-dash" />
                                    </button>
                                    <span className="fb-quantity-display">{item.quantity}</span>
                                    <button
                                        onClick={() => handleSumarCantidad(item)}
                                        className="fb-btn-qty"
                                        title="Sumar cantidad"
                                        disabled={isCheckingOut}
                                    >
                                        <i className="bi bi-plus" />
                                    </button>
                                </div>

                                <button
                                    onClick={() => handleQuitarItem(item)}
                                    className="fb-btn-remove"
                                    title="Quitar producto"
                                    disabled={isCheckingOut}
                                >
                                    <i className="bi bi-trash3" /> <span>Quitar</span>
                                </button>
                            </div>
                        </div>
                    ))}
                </div>

                {/* RESUMEN DEL PEDIDO */}
                <div className="fb-cart-summary-column">
                    <div className="fb-cart-summary-card">
                        <h3 className="fb-summary-title">Resumen del pedido</h3>

                        <div className="fb-summary-row">
                            <span>Productos en el carrito:</span>
                            <span className="fb-summary-value">{totalArticulos}</span>
                        </div>

                        <hr className="fb-summary-divider" />

                        <div className="fb-summary-row fb-summary-total">
                            <span>Total a pagar:</span>
                            <span className="fb-total-price">${Number(cart?.totalPurchase || 0).toFixed(2)}</span>
                        </div>

                        <button
                            onClick={handleCheckout}
                            disabled={isCheckingOut}
                            className={`fb-btn fb-btn-success fb-btn-block fb-btn-checkout ${isCheckingOut ? 'is-loading' : ''}`}
                        >
                            {isCheckingOut ? (
                                <>
                                    <span className="fb-inline-spinner"></span>
                                    Procesando compra...
                                </>
                            ) : "Finalizar compra"}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Cart;