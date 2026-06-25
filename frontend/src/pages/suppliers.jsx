import React from "react";
import FormLayout from "../components/FormLayout.jsx";
import { useEntity } from "../hooks/useEntity.js";

function Suppliers() {
  const countries = useEntity("countries");
  const countriesList = countries.list.data || [];

  // Definimos las variables estructurales del formulario
  const supplierFields = [
    {
      label: "Nombre comercial / Empresa",
      name: "name",
      icon: "bi-person",
      placeholder: "Nombre del proveedor"
    },
    {
      label: "Apellido",
      name: "lastName",
      icon: "bi-person",
      placeholder: "Apellido del contacto",
    },
    {
      label: "Teléfono de contacto",
      name: "phone",
      icon: "bi-telephone",
      placeholder: "Ej: 7777-7777"
    },
    {
      label: "Correo electrónico",
      name: "email",
      type: "email",
      icon: "bi-envelope",
      placeholder: "correo@ejemplo.com"
    },
    {
      label: "Dirección del proveedor",
      name: "address",
      icon: "bi-geo-alt",
      placeholder: "Dirección completa"
    },
    {
      label: "País de origen",
      name: "countryName",
      icon: "bi-globe",
      placeholder: "Selecciona o escribe un país",
      list: "suppliers-countries-datalist",
      options: countriesList.map(c => c.name || c.countryName).filter(Boolean)
    }
  ];

    const renderSupplierCard = (sup) => {
    const supplierId = sup.id ?? sup.supplierId ?? sup.supplier_id ?? sup.suppliers_id;

    const lastNameStr = sup.lastName ?? sup.last_name ?? "";
    const fullName = `${sup.name || "Proveedor sin nombre"} ${lastNameStr}`.trim();
    const countryDisplay = sup.countryName ?? sup.country_name ?? sup.country?.name ?? "Sin país asignado";

    return (
        <div key={supplierId} className="d-flex flex-column justify-content-between h-100 w-100"
             style={{minHeight: "100%"}}>

          {/* Encabezado de la Tarjeta */}
          <div>
            <div className="d-flex justify-content-between align-items-start gap-2 mb-2">
              <h6 className="fw-bold text-dark m-0 small lh-sm text-wrap text-truncate"
                  style={{ display: "-webkit-box", WebkitLineClamp: "2", WebkitBoxOrient: "vertical",
                    overflow: "hidden", height: "2.4rem"
                  }}>
                {fullName}
              </h6>
              <span className="badge bg-secondary-subtle text-secondary flex-shrink-0"
                    style={{fontSize: "0.7rem", marginTop: "0.1rem"}}>
              ID: {supplierId}
            </span>
            </div>
          </div>
          <div className="flex-grow-1 mb-2 d-flex flex-column justify-content-start text-muted"
               style={{fontSize: "0.85rem"}}>
            <p className="mb-2 text-dark text-truncate">
              <i className="bi bi-envelope text-muted me-2"/>
              {sup.email || "Sin correo electrónico"}
            </p>
            <p className="mb-3 text-dark">
              <i className="bi bi-telephone text-muted me-2"/>
              {sup.phone || "Sin teléfono"}
            </p>
            <div className="d-flex gap-2 align-items-start pt-2 border-top mb-2">
              <i className="bi bi-geo-alt text-muted mt-1" style={{fontSize: "0.75rem"}}/>
              <div>
                <span className="text-dark d-block fw-bold" style={{fontSize: "0.75rem"}}>Dirección:</span>
                <span className="text-secondary d-block lh-sm">{sup.address || "Sin dirección registrada"}</span>
              </div>
            </div>

            <div className="d-flex gap-2 align-items-start mb-1 mt-auto">
              <i className="bi bi-globe text-success mt-1" style={{fontSize: "0.75rem"}}/>
              <div>
                <span className="text-dark d-block fw-bold" style={{fontSize: "0.75rem"}}>País de origen:</span>
                <span className="text-secondary d-block lh-sm fw-semibold">{countryDisplay}</span>
              </div>
            </div>
          </div>

        </div>
    );
  };

  return (
      <FormLayout
          resource="suppliers"
          title="proveedor"
          article="el"
          icon="bi-truck"
          searchField="name"
          fields={supplierFields}
          renderCard={renderSupplierCard}
      />
  );
}

export default Suppliers;