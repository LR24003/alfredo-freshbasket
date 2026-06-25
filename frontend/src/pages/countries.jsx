
import React from "react";
import FormLayout from "../components/FormLayout.jsx";

function Countries() {

    // Campos del formulario
    const countryFields = [
        {
            label: "Nombre del país",
            name: "name",
            icon: "bi-globe",
            placeholder: "Ej: El Salvador"
        },
        {
            label: "Descripción",
            name: "description",
            icon: "bi-justify-left",
            placeholder: "Ej: Región Centroamérica - Proveedor de perecederos"
        },
    ];

    // Renderizador estético de tarjetas unificado
    const renderCountryCard = (co) => {
        const countryId = co.id ?? co.countryId ?? co.country_id ?? co.countries_id;

        return (
            <div key={countryId} className="d-flex flex-column justify-content-between h-100 w-100" style={{ minHeight: "100%" }}>
                <div>
                    <div className="d-flex justify-content-between align-items-start gap-2 mb-2">
                        <h6 className="fw-bold text-dark m-0 small lh-sm text-wrap text-truncate"
                            style={{ display: "-webkit-box", WebkitLineClamp: "2", WebkitBoxOrient: "vertical", overflow: "hidden", height: "2.4rem" }}>
                            {co.name || "País sin nombre"}
                        </h6>
                        <span className="badge bg-secondary-subtle text-secondary flex-shrink-0"
                              style={{ fontSize: "0.7rem", marginTop: "0.1rem" }}>
                    ID: {countryId}
                </span>
                    </div>
                </div>
                <div className="flex-grow-1 d-flex flex-column justify-content-start text-muted" style={{ fontSize: "0.85rem" }}>
                    <div className="d-flex gap-2 align-items-start pt-2 border-top">
                        <i className="bi bi-justify-left text-muted mt-1" style={{ fontSize: "0.75rem" }} />
                        <div className="w-100">
                            <span className="text-secondary d-block fw-bold" style={{ fontSize: "0.75rem" }}>Descripción:</span>
                            <span className="text-dark d-block lh-sm text-wrap text-truncate"
                                  style={{ display: "-webkit-box", WebkitLineClamp: "2",
                                      WebkitBoxOrient: "vertical", overflow: "hidden", height: "2.2rem"
                                  }}>
                        {co.description || "Sin descripción"}
                    </span>
                        </div>
                    </div>
                </div>

            </div>
        );
    };

    return (
        <FormLayout
            resource="countries"
            title="país"
            article="el"
            icon="bi-globe-americas"
            searchField="name"
            fields={countryFields}
            renderCard={renderCountryCard}
        />
    );
}

export default Countries;