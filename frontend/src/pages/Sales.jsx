import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSales } from "../hooks/useSales.js";
import { useRegister } from "../hooks/useRegister.js";
import PaidModal from "../components/PaidModal.jsx"; // 👇 Nombre actualizado aquí
import { toast } from "react-hot-toast";
import "../styles/sales.css";

function Sales() {
    const navigate = useNavigate();
    const {
        cart,
        search,
        setSearch,
        filteredProducts,
        paymentMethod,
        setPaymentMethod,
        selectedCustomer,
        setSelectedCustomer,
        customerSearch,
        setCustomerSearch,
        filteredCustomers,
        showCustomerDropdown,
        setShowCustomerDropdown,
        addToCart,
        updateQuantity,
        removeFromCart,
        discount,
        setDiscount,
        subtotalAmount,
        discountAmount,
        totalAmount,
        handleProcessSale,
        isLoadingProducts,
        isProcessingSale
    } = useSales();

    const [showRegisterModal, setShowRegisterModal] = useState(false);
    const [showPaidModal, setShowPaidModal] = useState(false); // 👇 Cambiado a PaidModal

    const { countriesList, loading: isRegistering, registerUser } = useRegister(null);

    // Interceptor del botón de cobro
    const handleCheckoutClick = () => {
        if (paymentMethod === "TARJETA") {
            setShowPaidModal(true); // Despliega la plantilla de cobro electrónico
        } else {
            handleProcessSale(); // Efectivo, Bitcoin, etc. procesan directo
        }
    };

    const handlePaidModalConfirm = (paymentData) => {
        setShowPaidModal(false);
        handleProcessSale(); // Ejecuta la orden en el servidor
    };

    const handleAgregarConDescuento = (product) => {
        const porcentajeDescuento = Number(product.discount || 0);
        if (porcentajeDescuento > 0) {
            const precioOriginal = Number(product.price || 0);
            const precioFinal = precioOriginal - ((precioOriginal * porcentajeDescuento) / 100);
            addToCart({ ...product, price: precioFinal });
            toast.success(`¡Aplicado -${porcentajeDescuento}% de oferta!`, { id: `pos-discount-${product.id}`, duration: 1000 });
        } else {
            addToCart(product);
        }
    };

    const handlePosRegisterSubmit = async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        const data = Object.fromEntries(formData.entries());
        data.role = "CLIENTE";

        await registerUser(data, (nuevoCliente) => {
            const fullName = `${nuevoCliente.name || ''} ${nuevoCliente.lastName || ''}`.trim();
            setSelectedCustomer({ id: nuevoCliente.id || "Nuevo", name: fullName });
            setCustomerSearch(fullName);
            setShowRegisterModal(false);
            toast.success(`Cliente ${fullName} registrado.`);
        });
    };

    return (
        <div className="fb-form-container" onClick={() => setShowCustomerDropdown(false)}>
            <div className="fb-pos-layout">

                {/* SECCIÓN IZQUIERDA: CATÁLOGO DE PRODUCTOS */}
                <div className="fb-pos-catalog-column">
                    <div className="fb-form-card fb-mb-1">
                        <h3 className="fb-form-title"><i className="bi bi-search"/> Módulo de Ventas (POS)</h3>
                        <div className="fb-search-input-wrap">
                            <i className="bi bi-fonts fb-search-icon"/>
                            <input
                                type="text"
                                className="fb-search-input"
                                placeholder="Buscar por artículo o ID..."
                                value={search}
                                onChange={(e) => setSearch(e.target.value)}
                                disabled={isLoadingProducts}
                            />
                        </div>
                    </div>

                    {isLoadingProducts ? (
                        <div className="fb-pos-loading">
                            <div className="spinner-border text-success" role="status"></div>
                            <p className="fb-mt-2">Cargando base de datos del POS...</p>
                        </div>
                    ) : (
                        <div className="fb-pos-grid">
                            {filteredProducts.map(product => {
                                const dcto = Number(product.discount || 0);
                                const precioOrig = Number(product.price || 0);
                                const precioConRebaja = precioOrig - ((precioOrig * dcto) / 100);

                                return (
                                    <div key={product.id} className="fb-form-card fb-product-card position-relative">
                                        {dcto > 0 && (
                                            <span className="badge bg-danger position-absolute top-0 end-0 m-2 p-1 small">
                                                -{dcto}%
                                            </span>
                                        )}
                                        <h4 className="fb-product-name">{product.name}</h4>
                                        <div className="fb-mb-05">
                                            {dcto > 0 ? (
                                                <>
                                                    <span className="fw-bold text-success me-2">${precioConRebaja.toFixed(2)}</span>
                                                    <span className="text-muted text-decoration-line-through small">${precioOrig.toFixed(2)}</span>
                                                </>
                                            ) : (
                                                <span className="fb-product-price">${precioOrig.toFixed(2)}</span>
                                            )}
                                        </div>
                                        <p className="fb-product-stock">Stock: {product.currentStock}</p>
                                        <button className="fb-search-btn fb-w-100 fb-p-04" onClick={() => handleAgregarConDescuento(product)}>
                                            <i className="bi bi-cart-plus-fill"/> Agregar
                                        </button>
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </div>

                {/* SECCIÓN DERECHA: RESUMEN DE COBRO */}
                <div className="fb-form-card fb-pos-summary-column" onClick={(e) => e.stopPropagation()}>
                    <h3 className="fb-form-title fb-summary-title">
                        <i className="bi bi-receipt-cutoff"/> Resumen de Caja
                    </h3>

                    <div className="fb-pos-cart-container">
                        {cart.length === 0 ? (
                            <p className="fb-pos-cart-empty">La orden de venta está vacía.</p>
                        ) : (
                            <table className="fb-pos-cart-table">
                                <tbody>
                                {cart.map(item => (
                                    <tr key={item.id}>
                                        <td className="fb-p-y-05">{item.name}</td>
                                        <td>
                                            <input
                                                type="number"
                                                value={item.quantity}
                                                className="fb-pos-cart-qty-input"
                                                onChange={(e) => updateQuantity(item.id, e.target.value, item.currentStock)}
                                                disabled={isProcessingSale}
                                            />
                                        </td>
                                        <td>${(item.price * item.quantity).toFixed(2)}</td>
                                        <td>
                                            <button className="fb-pos-cart-remove-btn" onClick={() => removeFromCart(item.id)} disabled={isProcessingSale}>
                                                <i className="bi bi-trash-fill"/>
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        )}
                    </div>

                    <div className="fb-pos-config-box">
                        <div className="fb-pos-customer-wrap">
                            <div className="fb-pos-customer-header">
                                <label><i className="bi bi-person-fill"/> Cliente de la venta:</label>
                                <button type="button" onClick={() => setShowRegisterModal(true)} className="fb-pos-add-customer-btn">
                                    <i className="bi bi-plus-circle-fill"/> [+ Registrar Cliente]
                                </button>
                            </div>
                            <div className="fb-pos-customer-search-row">
                                <input
                                    type="text"
                                    placeholder="Buscar por nombre o apellido..."
                                    value={customerSearch}
                                    onFocus={() => { setCustomerSearch(""); setShowCustomerDropdown(true); }}
                                    onChange={(e) => { setCustomerSearch(e.target.value); setShowCustomerDropdown(true); }}
                                    className={`fb-pos-customer-input ${selectedCustomer.id !== 1 ? 'active' : ''}`}
                                    disabled={isProcessingSale}
                                />
                                {selectedCustomer.id !== 1 && (
                                    <button type="button" onClick={() => { setSelectedCustomer({ id: 1, name: "Cliente General / Mostrador" }); setCustomerSearch("Cliente General / Mostrador"); setShowCustomerDropdown(false); }} className="fb-pos-customer-reset-btn">
                                        <i className="bi bi-arrow-counterclockwise"/>
                                    </button>
                                )}
                            </div>
                            <div className="fb-pos-customer-badge">Seleccionado: <strong>{selectedCustomer.name}</strong></div>

                            {showCustomerDropdown && (
                                <div className="fb-pos-dropdown">
                                    <div className="fb-pos-dropdown-item fb-pos-dropdown-default" onClick={() => { setSelectedCustomer({ id: 1, name: "Cliente General / Mostrador" }); setCustomerSearch("Cliente General / Mostrador"); setShowCustomerDropdown(false); }}><i className="bi bi-globe"/> Cliente General / Mostrador</div>
                                    {filteredCustomers.map(c => {
                                        const fullName = `${c.name || ''} ${c.lastName || ''}`.trim();
                                        return (
                                            <div key={c.id} className="fb-pos-dropdown-item" onClick={() => { setSelectedCustomer({ id: c.id, name: fullName }); setCustomerSearch(fullName); setShowCustomerDropdown(false); }}>
                                                <i className="bi bi-person"/> {fullName}
                                            </div>
                                        );
                                    })}
                                </div>
                            )}
                        </div>

                        <div className="fb-pos-payment-wrap">
                            <label><i className="bi bi-credit-card-fill"/> Canal de Pago:</label>
                            <select value={paymentMethod} onChange={(e) => setPaymentMethod(e.target.value)} className="fb-pos-select" disabled={isProcessingSale}>
                                <option value="EFECTIVO">💵 Efectivo</option>
                                <option value="TARJETA">💳 Tarjeta de Crédito o Débito</option>
                                <option value="TRANSFERENCIA">🏦 Transferencia Bancaria</option>
                                <option value="BITCOIN">🪙 Bitcoin (BTC)</option>
                            </select>
                        </div>

                        <div className="fb-pos-discount-wrap">
                            <label><i className="bi bi-tags-fill"/> Descuento General (%):</label>
                            <input type="number" min="0" max="100" placeholder="0" value={discount === 0 ? "" : discount} onChange={(e) => setDiscount(Math.max(0, Math.min(100, Number(e.target.value))))} className="fb-pos-discount-input" disabled={isProcessingSale || cart.length === 0} />
                        </div>
                    </div>

                    <div className="fb-pos-checkout-footer">
                        <div className="fb-pos-row-subtotal"><span>Subtotal:</span><span>${subtotalAmount.toFixed(2)}</span></div>
                        {discount > 0 && <div className="fb-pos-row-discount"><span>Descuento ({discount}%):</span><span>-${discountAmount.toFixed(2)}</span></div>}
                        <div className="fb-pos-row-total"><span>Importe Total:</span><span className="fb-total-green">${totalAmount.toFixed(2)}</span></div>

                        <button className="fb-action-btn fb-pos-submit-btn" onClick={handleCheckoutClick} disabled={isProcessingSale || cart.length === 0}>
                            <i className="bi bi-cash-coin"/> {isProcessingSale ? "Liquidando Caja..." : "Ejecutar Transacción"}
                        </button>
                    </div>
                </div>
            </div>

            {/* 💳 MODAL SEPARADO GLOBAL RENOMBRADO */}
            <PaidModal
                isOpen={showPaidModal}
                onClose={() => setShowPaidModal(false)}
                onConfirm={handlePaidModalConfirm}
                totalAmount={totalAmount}
                isProcessing={isProcessingSale}
            />

            {/* MODAL DE ALTA DE CLIENTES */}
            {showRegisterModal && (
                <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.6)', zIndex: 1050 }} role="dialog">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content text-dark px-3 py-2" style={{ borderRadius: '16px' }}>
                            <div className="modal-header border-0 pb-0">
                                <h5 className="modal-title fw-bold text-success"><i className="bi bi-person-plus-fill me-2" /> Registrar Nuevo Cliente</h5>
                                <button type="button" className="btn-close" onClick={() => setShowRegisterModal(false)} />
                            </div>
                            <form onSubmit={handlePosRegisterSubmit}>
                                <div className="modal-body">
                                    <div className="row g-2">
                                        <div className="col-6 mb-2"><label className="form-label small fw-semibold text-secondary mb-1">Nombre</label><input type="text" name="name" className="form-control bg-light" required /></div>
                                        <div className="col-6 mb-2"><label className="form-label small fw-semibold text-secondary mb-1">Apellido</label><input type="text" name="lastName" className="form-control bg-light" required /></div>
                                    </div>
                                    <div className="row g-2">
                                        <div className="col-6 mb-2"><label className="form-label small fw-semibold text-secondary mb-1">Teléfono</label><input type="text" name="phone" className="form-control bg-light" required /></div>
                                        <div className="col-6 mb-2">
                                            <label className="form-label small fw-semibold text-secondary mb-1">País</label>
                                            <input type="text" name="countryName" list="modal-countries" className="form-control bg-light" required />
                                            <datalist id="modal-countries">{countriesList.map((c, i) => <option key={i} value={c.name || c.countryName} />)}</datalist>
                                        </div>
                                    </div>
                                    <div className="mb-2"><label className="form-label small fw-semibold text-secondary mb-1">Correo</label><input type="email" name="email" className="form-control bg-light" required /></div>
                                    <div className="mb-2"><label className="form-label small fw-semibold text-secondary mb-1">Clave Temporal</label><input type="password" name="password" className="form-control bg-light" required /></div>
                                </div>
                                <div className="modal-footer border-0 pt-0">
                                    <button type="button" className="btn btn-outline-secondary" onClick={() => setShowRegisterModal(false)}>Cancelar</button>
                                    <button type="submit" className="btn btn-success px-4">Confirmar y Seleccionar</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Sales;