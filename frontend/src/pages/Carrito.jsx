import React, { useState } from 'react';
import { useCart } from '../hooks/useCart';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import '../styles/cart.css';

function Cart() {
    const { cart, isLoading, isError, updateQuantity, removeItem, checkoutAsync, isCheckingOut } = useCart();
    const navigate = useNavigate();

    // Estados para el Modal de Pago con Tarjeta
    const [showPaymentModal, setShowPaymentModal] = useState(false);
    const [cardForm, setCardForm] = useState({
        cardNumber: '',
        cardName: '',
        cardExpiry: '',
        cardCvv: ''
    });

    if (isLoading) {
        return (
            <div className="fb-cart-loading">
                <div className="fb-spinner"></div>
                <p>Cargando tu carrito de productos...</p>
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

    // 👇 CÁLCULO DEL DESCUENTO REAL CONVERTIDO DESDE EL PORCENTAJE
    const totalDescuento = itemsList.reduce((acc, item) => {
        const precioUnitario = Number(item.unitPrice || 0);
        const porcentajeDescuento = Number(item.discount || 0);

        // Calculamos cuánto dinero representa ese porcentaje por unidad
        const dineroDescontadoPorUnidad = (precioUnitario * porcentajeDescuento) / 100;

        // Multiplicamos por la cantidad de unidades de este producto
        return acc + (dineroDescontadoPorUnidad * item.quantity);
    }, 0);

    // El total bruto viene del backend, calculamos el total neto restando el ahorro real
    const totalBruto = Number(cart?.totalPurchase || 0);
    const totalNetoAPagar = totalBruto - totalDescuento;

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

    const handleOpenPaymentModal = () => {
        setShowPaymentModal(true);
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setCardForm(prev => ({ ...prev, [name]: value }));
    };

    const handleProcessCheckout = (e) => {
        e.preventDefault();

        if (!cardForm.cardNumber || !cardForm.cardName || !cardForm.cardExpiry || !cardForm.cardCvv) {
            return toast.error("Por favor, rellena todos los campos de tu tarjeta.");
        }

        setShowPaymentModal(false);

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
                    {itemsList.map((item) => {
                        const precioUnit = Number(item.unitPrice || 0);
                        const porcDesc = Number(item.discount || 0);
                        const ahorroPorUnidad = (precioUnit * porcDesc) / 100;
                        const subtotalItemOriginal = precioUnit * item.quantity;
                        const subtotalItemConDesc = subtotalItemOriginal - (ahorroPorUnidad * item.quantity);

                        return (
                            <div key={item.id || item.productId} className="fb-cart-item-card">
                                <div className="fb-cart-item-info">
                                    <h4 className="fb-cart-item-name">{item.productName || "Producto"}</h4>
                                    <p className="fb-cart-item-price">
                                        Precio unitario: <span>${precioUnit.toFixed(2)}</span>
                                    </p>

                                    {/* CORRECCIÓN DE ANIDACIÓN DUPLICADA DE ETIQUETAS */}
                                    {porcDesc > 0 && (
                                        <p className="text-success small fw-medium mb-0">
                                            Descuento: {porcDesc}%
                                        </p>
                                    )}

                                    <p className="fb-cart-item-subtotal">
                                        Subtotal: <span>
                                            ${porcDesc > 0 ? subtotalItemConDesc.toFixed(2) : Number(item.subtotal || 0).toFixed(2)}
                                        </span>
                                    </p>
                                </div>

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
                        );
                    })}
                </div>

                {/* RESUMEN DEL PEDIDO */}
                <div className="fb-cart-summary-column">
                    <div className="fb-cart-summary-card">
                        <h3 className="fb-summary-title">Resumen del pedido</h3>

                        <div className="fb-summary-row">
                            <span>Productos en el carrito:</span>
                            <span className="fb-summary-value">{totalArticulos}</span>
                        </div>

                        {totalDescuento > 0 && (
                            <div className="fb-summary-row text-success fw-medium">
                                <span>Descuento total:</span>
                                <span className="fb-summary-value">-${totalDescuento.toFixed(2)}</span>
                            </div>
                        )}

                        <hr className="fb-summary-divider" />

                        <div className="fb-summary-row fb-summary-total">
                            <span>Total a pagar:</span>
                            <span className="fb-total-price">${totalNetoAPagar.toFixed(2)}</span>
                        </div>

                        {/* ACCIONES DEL CARRITO */}
                        <div className="d-flex flex-column gap-2 mt-3">
                            <button
                                onClick={handleOpenPaymentModal}
                                disabled={isCheckingOut}
                                className={`fb-btn fb-btn-success fb-btn-block fb-btn-checkout ${isCheckingOut ? 'is-loading' : ''}`}
                            >
                                {isCheckingOut ? (
                                    <>
                                        <span className="fb-inline-spinner"></span>
                                        Procesando compra...
                                    </>
                                ) : "Proceder al pago"}
                            </button>

                            {/* 👇 BOTÓN AGREGADO: SEGUIR COMPRANDO */}
                            <button
                                type="button"
                                onClick={() => navigate('/freshbasket/productos')}
                                disabled={isCheckingOut}
                                className="btn btn-outline-secondary w-100 fw-medium py-2"
                                style={{ borderRadius: '10px' }}
                            >
                                <i className="bi bi-arrow-left me-2" />
                                Seguir comprando
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            {/* MODAL EMERGENTE PARA PAGO CON TARJETA */}
            {showPaymentModal && (
                <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.6)' }} role="dialog">
                    <div className="modal-dialog modal-dialog-centered" role="document">
                        <div className="modal-content text-dark px-2 py-1" style={{ borderRadius: '16px' }}>
                            <div className="modal-header border-0">
                                <h5 className="modal-title fw-bold">
                                    <i className="bi bi-credit-card-2-back text-success me-2" />
                                    Pago con tarjeta de Débito/Crédito
                                </h5>
                                <button
                                    type="button"
                                    className="btn-close"
                                    onClick={() => setShowPaymentModal(false)}
                                    disabled={isCheckingOut}
                                />
                            </div>

                            <form onSubmit={handleProcessCheckout}>
                                <div className="modal-body">
                                    <p className="text-muted small mb-4">
                                        Por favor, introduce los datos de tu tarjeta para autorizar el cobro inmediato de
                                        <strong> ${totalNetoAPagar.toFixed(2)}</strong>.
                                    </p>

                                    <div className="mb-3">
                                        <label className="form-label small fw-semibold">Número de Tarjeta</label>
                                        <input
                                            type="text"
                                            name="cardNumber"
                                            className="form-control"
                                            placeholder="4000 1234 5678 9010"
                                            maxLength="16"
                                            value={cardForm.cardNumber}
                                            onChange={handleInputChange}
                                            required
                                        />
                                    </div>

                                    <div className="mb-3">
                                        <label className="form-label small fw-semibold">Nombre del Titular</label>
                                        <input
                                            type="text"
                                            name="cardName"
                                            className="form-control"
                                            placeholder="Ej. Juan Pérez"
                                            value={cardForm.cardName}
                                            onChange={handleInputChange}
                                            required
                                        />
                                    </div>

                                    <div className="row">
                                        <div className="col-6 mb-3">
                                            <label className="form-label small fw-semibold">Vencimiento (MM/AA)</label>
                                            <input
                                                type="text"
                                                name="cardExpiry"
                                                className="form-control"
                                                placeholder="08/28"
                                                maxLength="5"
                                                value={cardForm.cardExpiry}
                                                onChange={handleInputChange}
                                                required
                                            />
                                        </div>
                                        <div className="col-6 mb-3">
                                            <label className="form-label small fw-semibold">CVV / CVC</label>
                                            <input
                                                type="password"
                                                name="cardCvv"
                                                className="form-control"
                                                placeholder="•••"
                                                maxLength="4"
                                                value={cardForm.cardCvv}
                                                onChange={handleInputChange}
                                                required
                                            />
                                        </div>
                                    </div>
                                </div>

                                <div className="modal-footer border-0">
                                    <button
                                        type="button"
                                        className="btn btn-outline-secondary"
                                        onClick={() => setShowPaymentModal(false)}
                                        disabled={isCheckingOut}
                                    >
                                        Cancelar
                                    </button>
                                    <button
                                        type="submit"
                                        className="btn btn-success px-4 fw-medium"
                                        disabled={isCheckingOut}
                                    >
                                        {isCheckingOut ? "Autorizando..." : "Confirmar Pago"}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Cart;