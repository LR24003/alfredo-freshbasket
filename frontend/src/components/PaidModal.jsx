import React from "react";

function PaidModal({ isOpen, onClose, onConfirm, totalAmount, isProcessing }) {
    if (!isOpen) return null;

    const handleSubmit = (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        const paymentData = Object.fromEntries(formData.entries());

        // Enviamos los datos recolectados al manejador del padre (POS o Carrito)
        onConfirm(paymentData);
    };

    return (
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.6)', zIndex: 1060 }} role="dialog">
            <div className="modal-dialog modal-dialog-centered" role="document">
                <div className="modal-content text-dark px-3 py-2" style={{ borderRadius: '16px', border: 'none' }}>

                    <div className="modal-header border-0 pb-0">
                        <h5 className="modal-title fw-bold text-success">
                            <i className="bi bi-credit-card-2-front-fill me-2" />
                            Procesar Pago Electrónico
                        </h5>
                        <button type="button" className="btn-close" onClick={onClose} disabled={isProcessing} />
                    </div>

                    <form onSubmit={handleSubmit}>
                        <div className="modal-body">
                            <div className="text-center bg-light p-3 rounded-3 mb-3">
                                <span className="text-muted small d-block">Monto a Liquidar:</span>
                                <span className="fs-3 fw-bold text-success">${Number(totalAmount).toFixed(2)}</span>
                            </div>

                            {/* Titular */}
                            <div className="mb-2">
                                <label className="form-label small fw-semibold text-secondary mb-1">Nombre del Titular</label>
                                <input type="text" name="cardName" className="form-control bg-light" placeholder="Ej: Juan Pérez" required disabled={isProcessing} />
                            </div>

                            {/* Número de Tarjeta */}
                            <div className="mb-2">
                                <label className="form-label small fw-semibold text-secondary mb-1">Número de Tarjeta</label>
                                <div className="input-group">
                                    <span className="input-group-text bg-light border-end-0 text-secondary"><i className="bi bi-credit-card" /></span>
                                    <input type="text" name="cardNumber" maxLength="16" className="form-control bg-light border-start-0" placeholder="0000 0000 0000 0000" required disabled={isProcessing} />
                                </div>
                            </div>

                            {/* Expiración y CVC */}
                            <div className="row g-2">
                                <div className="col-6 mb-2">
                                    <label className="form-label small fw-semibold text-secondary mb-1">Expiración</label>
                                    <input type="text" name="cardExpiry" maxLength="5" className="form-control bg-light" placeholder="MM/AA" required disabled={isProcessing} />
                                </div>
                                <div className="col-6 mb-2">
                                    <label className="form-label small fw-semibold text-secondary mb-1">Código de Seguridad (CVC)</label>
                                    <input type="password" name="cardCvc" maxLength="3" className="form-control bg-light" placeholder="123" required disabled={isProcessing} />
                                </div>
                            </div>
                        </div>

                        <div className="modal-footer border-0 pt-0">
                            <button type="button" className="btn btn-outline-secondary" onClick={onClose} disabled={isProcessing}>
                                Cancelar
                            </button>
                            <button type="submit" className="btn btn-success px-4 fw-medium" disabled={isProcessing}>
                                {isProcessing ? "Validando Transacción..." : "Autorizar Pago"}
                            </button>
                        </div>
                    </form>

                </div>
            </div>
        </div>
    );
}

export default PaidModal;