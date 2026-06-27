import React from "react";
import FormLayout from "../components/FormLayout.jsx";
import { useEntity } from "../hooks/useEntity.js";

// Campos del formulario
export const getExitFields = (isEditMode, activeProducts = [], userLogin = "") => [
    {
        label: "Producto",
        name: "productName",
        icon: "bi-tag",
        placeholder: "Selecciona o escribe un producto",
        list: "exits-products-list",
        options: activeProducts.map((p) => p.name || p.productName)
    },
    {
        label: "Cantidad a retirar",
        name: "quantity",
        icon: "bi-layers",
        type: "number",
        placeholder: "Ej: 10",
        step: "1"
    },
    {
        label: "Motivo de la salida",
        name: "exitReason",
        icon: "bi-box-arrow-down-left",
        type: "select",
        placeholder: "Seleccione el motivo",
        required: true,
        options: [
            { value: "VENTA", label: "Venta" },
            { value: "MERMA", label: "Merma (Pérdida/Daño)" }
        ]
    },
    {
        label: "ID de venta",
        name: "saleId",
        icon: "bi-receipt",
        placeholder: "Salida manual (No asociada a venta)",
        disabled: true
    },
    {
        label: isEditMode ? "Usuario que actualiza:" : "Usuario que registra:",
        name: "userName",
        icon: "bi-person",
        defaultValue: userLogin,
        disabled: true
    }
];

function Exits() {
    const userLogin = localStorage.getItem("userName") || localStorage.getItem("userEmail") || "";

    // Carga el catálogo global de productos mediante TanStack Query
    const products = useEntity("products");

    // Filtrado de los productos activos
    const activeProducts = (products.list.data || []).filter(
        (p) => p.active === true || p.active === undefined
    );

    const handleGetFields = (isEditMode) => {
        return getExitFields(isEditMode, activeProducts, userLogin);
    };

    // Renderizador estético de tarjetas unificado
    const renderExitCard = (exit) => {
        const exitId = exit.id ?? exit.exitId ?? exit.exits_id;

        const formattedDate = exit.exitDate
            ? String(exit.exitDate).split("T")[0]
            : "No disponible";

        return (
            <div key={exitId} className="d-flex flex-column justify-content-between h-100 w-100" style={{ minHeight: "100%" }}>
                <div>
                    <div className="d-flex justify-content-between align-items-start gap-2 mb-2">
                        <h6 className="fw-bold text-dark m-0 small lh-sm text-wrap text-truncate"
                            style={{ display: "-webkit-box", WebkitLineClamp: "2", WebkitBoxOrient: "vertical", overflow: "hidden", height: "2.4rem" }}>
                            {exit.productName || "Producto desconocido"}
                        </h6>
                        <span className="badge bg-secondary-subtle text-secondary flex-shrink-0"
                              style={{ fontSize: "0.7rem", marginTop: "0.1rem" }}>
                    ID: {exitId}
                </span>
                    </div>
                </div>
                <div className="flex-grow-1 mb-2 d-flex flex-column justify-content-start text-muted" style={{ fontSize: "0.85rem" }}>
                    <p className="mb-2 text-dark">
                        <i className="bi bi-calendar-event text-muted me-2" />
                        <strong>Fecha registro:</strong> {formattedDate}
                    </p>
                    <p className="mb-2 text-dark">
                        <i className="bi bi-layers text-muted me-2" />
                        <strong>Cantidad:</strong> {exit.quantity || 0}
                    </p>
                    <p className="mb-3 text-dark d-flex align-items-center">
                        <i className="bi bi-box-arrow-down-left text-danger me-2" />
                        <strong>Motivo:</strong>
                        <span className={`badge ms-2 text-capitalize ${
                            exit.exitReason?.toUpperCase() === 'VENTA' ? 'bg-success-subtle text-success border border-success' :
                                exit.exitReason?.toUpperCase() === 'MERMA' ? 'bg-danger text-white fw-bold shadow-sm' : 'bg-secondary-subtle text-secondary'
                        }`} style={{ fontSize: '0.7rem' }}>
                       {exit.exitReason ? exit.exitReason.replace('_', ' ').toLowerCase() : "No especificado"}
                        </span>
                    </p>
                    {exit.saleId && (
                        <p className="mb-2 text-dark fw-bold" style={{ fontSize: "0.8rem" }}>
                            <i className="bi bi-receipt text-primary me-2" />
                            <strong>Venta vinculada:</strong>
                            <span className="badge bg-light text-primary border border-primary ms-2" style={{ fontSize: "0.7rem" }}>
                        #{exit.saleId}
                    </span>
                        </p>
                    )}
                    <div className="d-flex gap-2 align-items-start pt-2 border-top mt-auto mb-1">
                        <i className="bi bi-person text-muted mt-1" style={{ fontSize: "0.75rem" }} />
                        <div>
                            <span className="mb-2 text-dark fw-bold" style={{ fontSize: "0.75rem" }}>Registrado por:</span>
                            <span className="text-dark d-block lh-sm">{exit.userName || "Sin usuario"}</span>
                        </div>
                    </div>
                </div>
            </div>
        );
    };

    return (
        <FormLayout
            resource="exits"
            title="salida"
            article="la"
            icon="bi-box-arrow-up"
            searchField="productName"
            renderCard={renderExitCard}
            fields={handleGetFields}
            onBeforeSave={(payload, mode) => {
                if (mode === "create") {
                    if (!payload.saleId || payload.type !== "VENTA") {
                        payload.saleId = null;
                    }
                }
                return payload;
            }}

        />
    );
}

export default Exits;


