import React from "react";
import FormLayout from "../components/FormLayout.jsx";
import { useEntity } from "../hooks/useEntity.js";

// Campos del formulario
export const getEntryFields = (isEditMode, activeProducts = [], suppliersList = [], userLogin = "") => [
  {
    label: "Producto",
    name: "productName",
    icon: "bi-tag",
    placeholder: "Selecciona o escribe un producto",
    list: "entries-products-list",
    options: activeProducts.map(p => p.name || p.productName)
  },
  {
    label: "Costo unitario",
    name: "unitCost",
    icon: "bi-currency-dollar",
    type: "number",
    placeholder: "0.00",
    step: "0.01"
  },
  {
    label: "Cantidad ingresada",
    name: "quantity",
    icon: "bi-layers",
    type: "number",
    placeholder: "Ej: 100",
    step: "1"
  },
  {
    label: "Proveedor",
    name: "supplierName",
    icon: "bi-building",
    placeholder: "Selecciona o escribe un proveedor",
    list: "entries-suppliers-list",
    options: suppliersList.map(s => `${s.name || s.supplierName || ""} ${s.lastName || ""}`.trim())
  },
  {
    label: isEditMode ? "Usuario que actualiza:" : "Usuario que registra:",
    name: "userName",
    icon: "bi-person",
    defaultValue: userLogin,
    disabled: true
  }
];

function Entries() {
  const userLogin = localStorage.getItem("userName") || localStorage.getItem("userEmail") || "";

  // Descarga dinámica de catálogos mediante TanStack Query
  const products = useEntity("products");
  const suppliers = useEntity("suppliers");

  const activeProducts = (products.list.data || []).filter(p => p.active === true || p.active === undefined);
  const suppliersList = suppliers.list.data || [];

  const handleGetFields = (isEditMode) => {
    return getEntryFields(isEditMode, activeProducts, suppliersList, userLogin);
  };

  const renderEntryCard = (entry) => {
    const entryId = entry.id ?? entry.entryId ?? entry.entries_id;

    const formattedDate = entry.entryDate
        ? String(entry.entryDate).split("T")[0]
        : "No disponible";

    return (
        <div key={entryId} className="d-flex flex-column justify-content-between h-100 w-100" style={{ minHeight: "100%" }}>
          <div>
            <div className="d-flex justify-content-between align-items-start gap-2 mb-2">
              <h6 className="fw-bold text-dark m-0 small lh-sm text-wrap text-truncate"
                  style={{ display: "-webkit-box", WebkitLineClamp: "2", WebkitBoxOrient: "vertical", overflow: "hidden", height: "2.4rem" }}>
                {entry.productName || "Producto desconocido"}
              </h6>
              <span className="badge bg-secondary-subtle text-secondary flex-shrink-0"
                    style={{ fontSize: "0.7rem", marginTop: "0.1rem" }}>
              ID: {entryId}
            </span>
            </div>
          </div>
          <div className="flex-grow-1 mb-2 d-flex flex-column justify-content-start text-muted" style={{ fontSize: "0.85rem" }}>
            <p className="mb-2 text-dark">
              <i className="bi bi-calendar-event text-muted me-2" />
              <strong>Fecha registro:</strong> {formattedDate}
            </p>
            <p className="mb-2 text-dark">
              <i className="bi bi-currency-dollar text-success me-2" />
              <strong>Costo unitario:</strong> ${Number(entry.unitCost || 0).toFixed(2)}
            </p>
            <p className="mb-3 text-dark">
              <i className="bi bi-layers text-muted me-2" />
              <strong>Cantidad:</strong> {entry.quantity || 0}
            </p>
            <div className="d-flex gap-2 align-items-start pt-2 border-top mb-2">
              <i className="bi bi-truck text-muted mt-1" style={{ fontSize: "0.75rem" }} />
              <div>
                <span className="mb-4 text-dark fw-bold" style={{ fontSize: "0.75rem" }}>Proveedor:</span>
                <span className="text-mute d-block lh-sm fw-semibold">{entry.supplierName || "Sin proveedor"}</span>
              </div>
            </div>
            <div className="d-flex gap-2 align-items-start mb-1">
              <i className="bi bi-person text-muted mt-1" style={{ fontSize: "0.75rem" }} />
              <div>
                <span className="mb-5 text-dark fw-bold" style={{ fontSize: "0.75rem" }}>Registrado por:</span>
                <span className="text-muted d-block lh-sm">{entry.userName || "Sin usuario"}</span>
              </div>
            </div>
          </div>
        </div>
    );
  };

  return (
      <FormLayout
          resource="entries"
          title="entrada"
          article="la"
          icon="bi-box-seam-fill"
          searchField="productName"
          fields={handleGetFields}
          renderCard={renderEntryCard}
          userLogin={userLogin}
      />
  );
}

export default Entries;