
import React from "react";
import FormLayout from "../components/FormLayout.jsx";

function Categories() {
    const title = "categoría";
    const resource = "categories";

    // Campos del formulario
    const categoryFields = [
        {
            label: "Nombre de la categoría",
            name: "name",
            icon: "bi-tag",
            type: "text",
            placeholder: "Ej: Frutas y Verduras"
        },
        {
            label: "Descripción de la categoría",
            name: "description",
            icon: "bi-justify-left",
            type: "text",
            placeholder: "Breve descripción del tipo de productos"
        }
    ];

    // Renderizador estético unificado de tarjetas
    const renderCategoryCard = (cat) => {
        const categoryId = cat.id ?? cat.categoryId ?? cat.category_id ?? cat.categories_id;

        return (
            <div key={categoryId} className="d-flex flex-column justify-content-between h-100 w-100" style={{ minHeight: "100%" }}>
                <div>
                    <div className="d-flex justify-content-between align-items-start gap-2 mb-2">
                        <h6 className="fw-bold text-dark m-0 small lh-sm text-wrap text-truncate"
                            style={{ display: "-webkit-box", WebkitLineClamp: "2", WebkitBoxOrient: "vertical",
                                overflow: "hidden", height: "2.4rem" }}>
                            {cat.name || "Categoría sin nombre"}
                        </h6>
                        <span className="badge bg-secondary-subtle text-secondary flex-shrink-0"
                              style={{ fontSize: "0.7rem", marginTop: "0.1rem" }}>
                    ID: {categoryId}
                </span>
                    </div>
                </div>
                <div className="flex-grow-1 d-flex flex-column justify-content-start text-muted" style={{ fontSize: "0.85rem" }}>
                    <div className="d-flex gap-2 align-items-start pt-2 border-top">
                        <i className="bi bi-justify-left text-muted mt-1" style={{ fontSize: "0.75rem" }} />
                        <div className="w-100">
                            <span className="text-dark d-block fw-bold" style={{ fontSize: "0.75rem" }}>Descripción:</span>
                            <span className="text-mute d-block lh-sm text-wrap text-truncate"
                                  style={{ display: "-webkit-box", WebkitLineClamp: "2", WebkitBoxOrient: "vertical",
                                      overflow: "hidden", height: "2.2rem"
                                  }}>
                        {cat.description || "Sin descripción asignada"}
                    </span>
                        </div>
                    </div>
                </div>

            </div>
        );
    };

    return (
        <FormLayout
            resource={resource}
            title={title}
            article="la"
            icon="bi-tags"
            searchField="name"
            fields={categoryFields}
            renderCard={renderCategoryCard}
        />
    );
}

export default Categories;