import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSales } from "../hooks/useSales.js";
import { useRegister } from "../hooks/useRegister.js";
import PaidModal from "../components/PaidModal.jsx";
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
        subtotalAmount,
        totalAmount,
        handleProcessSale,
        isLoadingProducts,
        isProcessingSale
    } = useSales();

    const [showRegisterModal, setShowRegisterModal] = useState(false);
    const [showPaidModal, setShowPaidModal] = useState(false);
    const [showProductDropdown, setShowProductDropdown] = useState(false);

    const { countriesList, loading: isRegistering, registerUser } = useRegister(null);

    // ─── CÁLCULO DINÁMICO DEL DESCUENTO ACUMULADO EN DÓLARES ───
    // Recorre cada producto en el carrito sumando: (Precio Original - Precio con Rebaja) * Cantidad
    const totalDescuentoDolares = cart ? cart.reduce((acumulado, item) => {
        const dctoPorcentaje = Number(item.discount || 0);
        if (dctoPorcentaje > 0) {
            // Calculamos el precio original base antes de la rebaja
            const precioOriginal = item.price / (1 - (dctoPorcentaje / 100));
            const ahorroPorUnidad = precioOriginal - item.price;
            return acumulado + (ahorroPorUnidad * item.quantity);
        }
        return acumulado;
    }, 0) : 0;

    const handleCheckoutClick = () => {
        if (paymentMethod === "TARJETA") {
            setShowPaidModal(true);
        } else {
            handleProcessSale();
        }
    };

    const handlePaidModalConfirm = (paymentData) => {
        setShowPaidModal(false);
        handleProcessSale();
    };

    const handleAgregarConDescuento = (product) => {
        const porcentajeDescuento = Number(product.discount || 0);
        if (porcentajeDescuento > 0) {
            const precioOriginal = Number(product.price || 0);
            const precioFinal = precioOriginal - ((precioOriginal * porcentajeDescuento) / 100);
            // Pasamos el descuento en las propiedades para que se mantenga en el estado
            addToCart({ ...product, price: precioFinal, discount: porcentajeDescuento });
            toast.success(`¡Aplicado -${porcentajeDescuento}% de oferta!`, { id: `pos-discount-${product.id}`, duration: 1000 });
        } else {
            addToCart(product);
        }
        setSearch("");
        setShowProductDropdown(false);
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
        <div className="fb-form-container" onClick={() => { setShowCustomerDropdown(false); setShowProductDropdown(false); }}>
            <div className="fb-pos-layout">

                {/* ─── SECCIÓN IZQUIERDA: BUSCADOR + PRODUCTOS AGREGADOS (COMPACTO) ─── */}
                <div className="fb-pos-catalog-column">
                    <div className="fb-form-card fb-mb-1 position-relative" onClick={(e) => e.stopPropagation()}>
                        <h3 className="fb-form-title" style={{ fontSize: "0.95rem" }}><i className="bi bi-search"/> Buscar Producto:</h3>
                        <div className="fb-search-input-wrap">
                            <i className="bi bi-fonts fb-search-icon"/>
                            <input
                                type="text"
                                className="fb-search-input"
                                placeholder="Escribe el nombre del producto..."
                                value={search}
                                onFocus={() => setShowProductDropdown(true)}
                                onChange={(e) => { setSearch(e.target.value); setShowProductDropdown(true); }}
                                disabled={isLoadingProducts}
                                style={{ fontSize: "0.85rem", height: "38px" }}
                            />
                        </div>

                        {/* Desplegable de coincidencias */}
                        {showProductDropdown && search.trim().length > 0 && (
                            <div className="fb-pos-dropdown" style={{ width: "100%", top: "100%", zIndex: 10 }}>
                                {filteredProducts.length > 0 ? (
                                    filteredProducts.map(product => {
                                        const dcto = Number(product.discount || 0);
                                        return (
                                            <div
                                                key={product.id}
                                                className="fb-pos-dropdown-item d-flex justify-content-between align-items-center"
                                                onClick={() => handleAgregarConDescuento(product)}
                                                style={{ fontSize: "0.85rem", padding: "0.5rem 0.75rem" }}
                                            >
                                                <span><i className="bi bi-box-seam me-2"/>{product.name}</span>
                                                <span className="badge bg-success" style={{ fontSize: "0.75rem" }}>
                                                    ${Number(product.price).toFixed(2)} {dcto > 0 && `(-${dcto}%)`}
                                                </span>
                                            </div>
                                        );
                                    })
                                ) : (
                                    <div className="fb-pos-dropdown-item text-muted text-center" style={{ fontSize: "0.85rem" }}>No se encontraron productos</div>
                                )}
                            </div>
                        )}
                    </div>

                    {/* Lista dinámica por debajo del buscador */}
                    <div className="fb-form-card">
                        <h4 className="fb-form-title mb-3" style={{ fontSize: "0.95rem" }}><i className="bi bi-cart-check-fill"/> Productos en la Orden Actual</h4>
                        {cart.length === 0 ? (
                            <div className="text-center text-muted py-4" style={{ fontSize: "0.85rem" }}>
                                <i className="bi bi-box-seam display-6 d-block mb-2" style={{ opacity: 0.5 }}/>
                                <span>Usa la barra superior para buscar y añadir artículos</span>
                            </div>
                        ) : (
                            <div className="table-responsive">
                                <table className="table align-middle text-dark table-sm" style={{ fontSize: "0.82rem" }}>
                                    <thead className="table-light">
                                    <tr>
                                        <th style={{ padding: "0.5rem" }}>Nombre del Producto</th>
                                        <th className="text-center" style={{ width: "90px", padding: "0.5rem" }}>Cantidad</th>
                                        <th className="text-end" style={{ padding: "0.5rem" }}>Precio Unitario</th>
                                        <th className="text-end" style={{ padding: "0.5rem" }}>Descuento</th>
                                        <th className="text-center" style={{ width: "40px", padding: "0.5rem" }}>Acción</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {cart.map(item => {
                                        const dctoOriginal = Number(item.discount || 0);
                                        return (
                                            <tr key={item.id}>
                                                <td className="fw-semibold text-secondary" style={{ padding: "0.4rem" }}>{item.name}</td>
                                                <td className="text-center" style={{ padding: "0.4rem" }}>
                                                    <input
                                                        type="number"
                                                        value={item.quantity}
                                                        className="fb-pos-cart-qty-input text-center w-100"
                                                        onChange={(e) => updateQuantity(item.id, e.target.value, item.currentStock)}
                                                        disabled={isProcessingSale}
                                                        style={{ height: "28px", borderRadius: "4px", fontSize: "0.8rem", padding: "2px" }}
                                                    />
                                                </td>
                                                <td className="text-end fw-bold text-success" style={{ padding: "0.4rem" }}>
                                                    ${Number(item.price).toFixed(2)}
                                                </td>
                                                <td className="text-end" style={{ padding: "0.4rem" }}>
                                                    {dctoOriginal > 0 ? (
                                                        <span className="badge bg-danger" style={{ fontSize: "0.7rem", padding: "0.2rem 0.4rem" }}>-{dctoOriginal}%</span>
                                                    ) : (
                                                        <span className="text-muted small">-</span>
                                                    )}
                                                </td>
                                                <td className="text-center" style={{ padding: "0.4rem" }}>
                                                    <button
                                                        className="btn btn-sm btn-link text-danger p-0"
                                                        onClick={() => removeFromCart(item.id)}
                                                        disabled={isProcessingSale}
                                                    >
                                                        <i className="bi bi-trash3-fill fs-6"/>
                                                    </button>
                                                </td>
                                            </tr>
                                        );
                                    })}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </div>
                </div>

                {/* ─── SECCIÓN DERECHA: CONFIGURACIÓN Y TOTALES ACUMULADOS ─── */}
                <div className="fb-form-card fb-pos-summary-column" onClick={(e) => e.stopPropagation()}>
                    <h3 className="fb-form-title fb-summary-title" style={{ fontSize: "1rem" }}>
                        <i className="bi bi-receipt-cutoff"/> Resumen de Caja
                    </h3>

                    <div className="fb-pos-config-box mt-2" style={{ fontSize: "0.85rem" }}>
                        <div className="fb-pos-customer-wrap">
                            <div className="fb-pos-customer-header">
                                <label className="fw-semibold"><i className="bi bi-person-fill"/> Cliente de la venta:</label>
                                <button type="button" onClick={() => setShowRegisterModal(true)} className="fb-pos-add-customer-btn" style={{ fontSize: "0.75rem" }}>
                                    <i className="bi bi-plus-circle-fill"/> [+ Registrar]
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
                                    style={{ fontSize: "0.82rem", height: "36px" }}
                                />
                                {selectedCustomer.id !== 1 && (
                                    <button type="button" onClick={() => { setSelectedCustomer({ id: 1, name: "Cliente General / Mostrador" }); setCustomerSearch("Cliente General / Mostrador"); setShowCustomerDropdown(false); }} className="fb-pos-customer-reset-btn">
                                        <i className="bi bi-arrow-counterclockwise"/>
                                    </button>
                                )}
                            </div>
                            <div className="fb-pos-customer-badge" style={{ fontSize: "0.75rem" }}>Seleccionado: <strong>{selectedCustomer.name}</strong></div>

                            {showCustomerDropdown && (
                                <div className="fb-pos-dropdown">
                                    <div className="fb-pos-dropdown-item fb-pos-dropdown-default" style={{ fontSize: "0.82rem" }} onClick={() => { setSelectedCustomer({ id: 1, name: "Cliente General / Mostrador" }); setCustomerSearch("Cliente General / Mostrador"); setShowCustomerDropdown(false); }}><i className="bi bi-globe"/> Cliente General / Mostrador</div>
                                    {filteredCustomers.map(c => {
                                        const fullName = `${c.name || ''} ${c.lastName || ''}`.trim();
                                        return (
                                            <div key={c.id} className="fb-pos-dropdown-item" style={{ fontSize: "0.82rem" }} onClick={() => { setSelectedCustomer({ id: c.id, name: fullName }); setCustomerSearch(fullName); setShowCustomerDropdown(false); }}>
                                                <i className="bi bi-person"/> {fullName}
                                            </div>
                                        );
                                    })}
                                </div>
                            )}
                        </div>

                        <div className="fb-pos-payment-wrap mt-2">
                            <label className="fw-semibold"><i className="bi bi-credit-card-fill"/> Canal de Pago:</label>
                            <select value={paymentMethod} onChange={(e) => setPaymentMethod(e.target.value)} className="fb-pos-select" disabled={isProcessingSale} style={{ fontSize: "0.82rem", height: "36px" }}>
                                <option value="EFECTIVO">💵 Efectivo</option>
                                <option value="TARJETA">💳 Tarjeta de Crédito o Débito</option>
                                <option value="TRANSFERENCIA">🏦 Transferencia Bancaria</option>
                                <option value="BITCOIN">🪙 Bitcoin (BTC)</option>
                            </select>
                        </div>

                        {/* Visualizador Informativo de Ahorro en Dólares */}
                        <div className="fb-pos-discount-wrap mt-2">
                            <label className="fw-semibold text-danger"><i className="bi bi-tags-fill"/> Descuento Aplicado en esta Venta:</label>
                            <div className="form-control d-flex align-items-center bg-light fw-bold text-danger" style={{ fontSize: "0.85rem", height: "36px", border: "1px dashed #dc3545" }}>
                                <i className="bi bi-currency-dollar me-1"/> {totalDescuentoDolares.toFixed(2)} USD ahorrados
                            </div>
                        </div>
                    </div>

                    <div className="fb-pos-checkout-footer" style={{ fontSize: "0.85rem" }}>
                        <div className="fb-pos-row-subtotal"><span>Subtotal (Sin Rebajas):</span><span>${(subtotalAmount + totalDescuentoDolares).toFixed(2)}</span></div>
                        {totalDescuentoDolares > 0 && <div className="fb-pos-row-discount text-danger fw-semibold"><span>Total Descontado:</span><span>-${totalDescuentoDolares.toFixed(2)}</span></div>}
                        <div className="fb-pos-row-total" style={{ fontSize: "1.1rem" }}><span>Monto Neto a Pagar:</span><span className="fb-total-green">${totalAmount.toFixed(2)}</span></div>

                        <button className="fb-action-btn fb-pos-submit-btn w-100 mt-2" onClick={handleCheckoutClick} disabled={isProcessingSale || cart.length === 0} style={{ fontSize: "0.9rem", padding: "0.6rem" }}>
                            <i className="bi bi-cash-coin"/> {isProcessingSale ? "Liquidando Caja..." : "Ejecutar Transacción"}
                        </button>
                    </div>
                </div>
            </div>

            <PaidModal
                isOpen={showPaidModal}
                onClose={() => setShowPaidModal(false)}
                onConfirm={handlePaidModalConfirm}
                totalAmount={totalAmount}
                isProcessing={isProcessingSale}
            />

            {/* MODAL DE ALTA DE CLIENTES */}
            {/* ─── MODAL DE ALTA DE CLIENTES REESTABLECIDO ─── */}
            {showRegisterModal && (
                <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.6)', zIndex: 1050 }} role="dialog">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content text-dark px-3 py-2" style={{ borderRadius: '16px', border: 'none' }}>
                            <div className="modal-header border-0 pb-0">
                                <h5 className="modal-title fw-bold text-success" style={{ fontSize: "1.1rem" }}>
                                    <i className="bi bi-person-plus-fill me-2" /> Registrar cliente
                                </h5>
                                <button type="button" className="btn-close" onClick={() => setShowRegisterModal(false)} />
                            </div>
                            <div className="modal-body fb-pos-modal-fix">
                                <form onSubmit={handlePosRegisterSubmit}>
                                    <div className="fb-pos-modal-row">
                                        <div className="fb-pos-modal-group">
                                            <label>Nombre</label>
                                            <input type="text" name="name" required />
                                        </div>
                                        <div className="fb-pos-modal-group">
                                            <label>Apellido</label>
                                            <input type="text" name="lastName" required />
                                        </div>
                                    </div>
                                    <div className="fb-pos-modal-row">
                                        <div className="fb-pos-modal-group">
                                            <label>Teléfono</label>
                                            <input type="text" name="phone" required />
                                        </div>
                                        <div className="fb-pos-modal-group">
                                            <label>País</label>
                                            <input type="text" name="countryName" list="modal-countries" required />
                                            <datalist id="modal-countries">
                                                {countriesList.map((c, i) => <option key={i} value={c.name || c.countryName} />)}
                                            </datalist>
                                        </div>
                                    </div>
                                    <div className="fb-pos-modal-group full-width">
                                        <label>Correo</label>
                                        <input type="email" name="email" autoComplete="username" required />
                                    </div>
                                    <div className="fb-pos-modal-group full-width mb-4">
                                        <label>Clave temporal</label>
                                        <input type="password" name="password" autoComplete="new-password" required />
                                    </div>
                                    <div className="modal-footer border-0 pt-0 px-0 d-flex justify-content-end gap-2">
                                        <button type="button" className="btn btn-sm btn-outline-secondary" style={{ borderRadius: '6px', padding: '0.4rem 1rem' }} onClick={() => setShowRegisterModal(false)}>Cancelar</button>
                                        <button type="submit" className="btn btn-sm btn-success px-4" style={{ borderRadius: '6px', padding: '0.4rem 1.2rem', backgroundColor: '#198754' }} disabled={isRegistering}>
                                            {isRegistering ? "Guardando..." : "Guardar y seleccionar"}
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Sales;