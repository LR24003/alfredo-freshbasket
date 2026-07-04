import React, { useState } from "react";
import FormLayout from "../components/FormLayout.jsx";
import { useEntity } from "../hooks/useEntity.js";
import { useCart } from "../hooks/useCart.js";
import { toast } from 'react-hot-toast';

const CAT_TIPO_ITEM = [
  { value: 1, label: "Bienes" },
  { value: 2, label: "Servicios" },
  { value: 3, label: "Bienes y Servicios" },
  { value: 4, label: "Otros" }
];

const CAT_TIPO_IMPUESTO = [
  { value: 1, label: "Gravado (13% IVA)" },
  { value: 2, label: "Exento de IVA" },
  { value: 3, label: "No Sujeto" }
];

const CAT_UNIDAD_MEDIDA = [
  { value: "59", label: "Unidades" },
  { value: "23", label: "Kilogramos " },
  { value: "22", label: "Libras" },
  { value: "24", label: "Litros" },
  { value: "99", label: "Otros" }
];

function Products() {
  const userLogin = localStorage.getItem("userName") || localStorage.getItem("userEmail") || "";

  const categories = useEntity("categories");
  const suppliers = useEntity("suppliers");
  const { cart, updateQuantity } = useCart();

  const categoriesList = categories.list.data || [];
  const suppliersList = suppliers.list.data || [];

  // Estado local para capturar el producto seleccionado de la tarjeta.
  const [selectedProduct, setSelectedProduct] = useState(null);

  const getProductFields = (isEditMode) => {
    const currentEntity = isEditMode ? selectedProduct : null;

    return [
      {
        label: "Nombre del producto",
        name: "name",
        icon: "bi-box-seam",
        placeholder: "Ej: Carne de Res"
      },
      {
        label: "Precio",
        name: "price",
        icon: "bi-currency-dollar",
        type: "number",
        placeholder: "0.00",
        step: "0.01"
      },
      {
        label: "Stock inicial",
        name: "currentStock",
        icon: "bi-hash",
        type: "number",
        placeholder: "25",
        disabledOnUpdate: true
      },
      {
        label: "URL de imagen",
        name: "imageUrl",
        icon: "bi-image",
        placeholder: "http://...jpg",
        required: false
      },
      {
        label: "Descuento",
        name: "discount",
        type: "number",
        icon: "bi-percent",
        placeholder: "0.00",
        required: false,
        min: 0,
        step: "0.01",
        defaultValue: "0.00"
      },
      {
        label: "Categoría del producto",
        name: "categoryName",
        icon: "bi-bookmark-star",
        placeholder: "Seleccione o escribe",
        list: "products-cats-options",
        options: categoriesList.map((c) => c.name || c.categoryName)
      },
      {
        label: "Proveedor del producto",
        name: "supplierName",
        icon: "bi-truck",
        placeholder: "Seleccione o escribe",
        list: "products-sups-options",
        options: suppliersList.map((s) => `${s.name || s.supplierName || ""} ${s.lastName || ""}`.trim())
      },
      {
        label: "Descripción del producto",
        name: "description",
        icon: "bi-justify-left",
        placeholder: "Detalles del producto"
      },
      {
        name: "minStock",
        label: "Stock mínimo requerido del producto",
        type: "number",
        placeholder: "Ej. 5",
        icon: "bi-exclamation-triangle",
        required: true,
        step: "1"
      },
      {
        name: "tipoItem",
        label: "Tipo de artículo según MH",
        type: "number",
        placeholder: "Seleccione o escribe (Ej. 1)",
        icon: "bi-list-check",
        required: true,
        step: "1",
        list: "mh-tipo-item-options",
        options: CAT_TIPO_ITEM.map(opt => ({ value: opt.value, label: `${opt.value} - ${opt.label}` })),
        defaultValue: currentEntity?.tipoItem ?? 1
      },
      {
        name: "tipoImpuestoDefecto",
        label: "Tipo de impuesto por defecto",
        type: "number",
        placeholder: "Seleccione o escribe (Ej. 1)",
        icon: "bi-percent",
        required: true,
        step: "1",
        list: "mh-tipo-impuesto-options",
        options: CAT_TIPO_IMPUESTO.map(opt => ({ value: opt.value, label: `${opt.value} - ${opt.label}` })),
        defaultValue: currentEntity?.tipoImpuestoDefecto ?? 1
      },
      {
        name: "unidadMedidaDefecto",
        label: "Unidad de medida por defecto",
        type: "text",
        placeholder: "Seleccione o escribe (Ej. 99)",
        icon: "bi-rulers",
        required: true,
        list: "mh-unidad-medida-options",
        options: CAT_UNIDAD_MEDIDA.map(opt => ({ value: opt.value, label: `${opt.value} - ${opt.label}` })),
        defaultValue: currentEntity?.unidadMedidaDefecto ?? "99"
      },
      {
        label: isEditMode ? "Usuario que actualiza:" : "Usuario que registra:",
        name: "userName",
        icon: "bi-person-badge",
        defaultValue: userLogin,
        disabled: true
      }
    ];
  };

  const renderProductCard = (p) => (
      <ProductCard
          key={p.id || p.productId || p.products_id}
          p={p}
          cart={cart}
          updateQuantity={updateQuantity}
          onEditCapture={() => setSelectedProduct(p)}
      />
  );

  return (
      <FormLayout
          resource="products"
          title="producto"
          article="el"
          icon="bi-box-seam-fill"
          searchField="name"
          fields={getProductFields}
          renderCard={renderProductCard}
      />
  );
}

function ProductCard({ p, cart, updateQuantity, onEditCapture }) {
  const [isExpanded, setIsExpanded] = useState(false);
  const productId = p.id ?? p.productId ?? p.products_id;

  const itemEnCarrito = cart?.items?.find(item => item.productId === productId);
  const cantidadActual = itemEnCarrito ? itemEnCarrito.quantity : 0;

  const precioNumerico = Number(p.price || 0);
  const porcentajeDescuento = Number(p.discount || 0);
  const dineroDescontado = (precioNumerico * porcentajeDescuento) / 100;

  const idTipoItem = (p.tipoItem ?? "").toString().trim();
  const idImpuesto = (p.tipoImpuestoDefecto ?? "").toString().trim();
  const idUnidadMedida = (p.unidadMedidaDefecto ?? "").toString().trim();

  const labelTipoItem = CAT_TIPO_ITEM.find(c => c.value.toString() === idTipoItem)?.label || `Código ${idTipoItem || 'No def.'}`;
  const labelImpuesto = CAT_TIPO_IMPUESTO.find(c => c.value.toString() === idImpuesto)?.label || `Código ${idImpuesto || 'No def.'}`;
  const labelMedida = CAT_UNIDAD_MEDIDA.find(c => c.value.toString() === idUnidadMedida)?.label || `Código ${idUnidadMedida || 'No def.'}`;

  return (
      <div className="d-flex flex-direction-column justify-content-between h-100 w-100"
           style={{ display: 'flex', flexDirection: 'column', height: '100%' }}
           onClick={onEditCapture}
      >
        <div className="d-flex justify-content-between align-items-start gap-2 mb-2">
          <h6 className="fw-bold text-dark m-0 small lh-sm text-wrap text-truncate"
              style={{ display: "-webkit-box", WebkitLineClamp: "2", WebkitBoxOrient: "vertical", overflow: "hidden", height: "2.4rem" }}>
            {p.name || "Producto sin nombre"}
          </h6>
          <span className="badge bg-secondary-subtle text-secondary flex-shrink-0 style-id-fallback" style={{ fontSize: "0.7rem", marginTop: "0.1rem" }}>
            ID: {productId}
         </span>
        </div>

        {/* Imagen del producto */}
        <div className="fb-product-card-image-wrap mb-3 text-center">
          {p.imageUrl ? (
              <img
                  src={p.imageUrl}
                  alt={p.name}
                  className="fb-product-card-img-element rounded"
                  style={{ width: "100%", height: "140px", objectFit: "contain" }}
                  onError={(e) => {
                    e.currentTarget.onerror = null;
                    e.currentTarget.src = "https://placehold.co/120x120?text=FreshBasket";
                  }}
              />
          ) : (
              <div className="fb-product-card-img-placeholder d-flex align-items-center justify-content-center bg-light rounded" style={{ height: "140px" }}>
                <i className="bi bi-image" style={{ fontSize: "2.5rem", color: "#a3b8a3" }} />
              </div>
          )}
        </div>

        <div className="flex-grow-1 mb-3 d-flex flex-column text-muted" style={{ fontSize: "0.85rem" }}>
          <p className="mb-2 text-dark">
            <i className="bi bi-currency-dollar text-success" /> <strong>Precio:</strong> ${precioNumerico.toFixed(2)}
          </p>
          <div className="d-flex gap-2 align-items-start mb-2">
            <i className="bi bi-justify-left text-muted mt-1" style={{ fontSize: "0.65rem" }} />
            <div>
              <span className="text-muted d-block fw-bold" style={{ fontSize: "0.85rem" }}>Descripción:</span>
              <span className="text-dark d-block lh-sm">{p.description || "Sin descripción"}</span>
            </div>
          </div>

          {porcentajeDescuento > 0 && (
              <div className="d-flex gap-2 align-items-start mb-2">
                <i className="bi bi-percent text-danger mt-1" style={{ fontSize: "0.75rem" }} />
                <div>
                  <span className="text-muted d-block" style={{ fontSize: "0.75rem" }}>Descuento:</span>
                  <span className="text-success fw-bold lh-sm">
                    {porcentajeDescuento}% (Ahorras: ${dineroDescontado.toFixed(2)})
                  </span>
                </div>
              </div>
          )}

          {/* DETALLES EXTENDIDOS */}
          {isExpanded && (
              <div className="fb-product-card-extra-info pt-2 border-top mt-auto" style={{ fontSize: "0.8rem" }}>
                <p className="mb-1">
                  <i className="bi bi-hash text-muted" /> <strong>Stock actual:</strong> {p.currentStock ?? 0} (Min: {p.minStock || 0})
                </p>
                <p className="mb-1">
                  <i className="bi bi-bookmark-star text-muted" /> <strong>Categoría:</strong> {p.categoryName || "Sin categoría"}
                </p>
                <p className="mb-1">
                  <i className="bi bi-truck text-muted" /> <strong>Proveedor:</strong> {p.supplierName || "Sin proveedor"}
                </p>
                <p className="mb-1 text-truncate">
                  <i className="bi bi-list-check text-muted" /> <strong>Tipo Artículo:</strong> {labelTipoItem}
                </p>
                <p className="mb-1 text-truncate">
                  <i className="bi bi-percent text-muted" /> <strong>Tipo impuesto:</strong> {labelImpuesto}
                </p>
                <p className="mb-1 text-truncate">
                  <i className="bi bi-rulers text-muted" /> <strong>Tipo Medida:</strong> {labelMedida}
                </p>
                <p className="mb-1">
                  <i className="bi bi-person text-muted" /> <strong>Registrado por:</strong> {p.userName || "Sin usuario"}
                </p>
              </div>
          )}
        </div>

        <div className="d-flex justify-content-center align-items-center gap-2 pt-2 border-top mt-auto">
          <button
              type="button"
              onClick={(e) => {
                if (e) { e.preventDefault(); e.stopPropagation(); }
                const siguienteCantidad = cantidadActual === 0 ? 1 : cantidadActual + 1;
                updateQuantity({ productId, quantity: siguienteCantidad });
              }}
              className="btn btn-primary btn-sm d-flex align-items-center justify-content-center gap-1 flex-fill w-50 py-1"
              style={{ fontSize: "0.8rem" }}
          >
            <i className="bi bi-cart-plus-fill" />
            <span className="text-truncate">
              {cantidadActual > 0 ? `Carrito: ${cantidadActual}` : "Agregar"}
            </span>
          </button>

          <button
              type="button"
              onClick={(e) => {
                if (e) { e.preventDefault(); e.stopPropagation(); }
                setIsExpanded(!isExpanded);
              }}
              className={`btn btn-sm d-flex align-items-center justify-content-center gap-1 flex-fill w-50 py-1 ${isExpanded ? "btn-secondary" : "btn-outline-secondary"}`}
              style={{ fontSize: "0.8rem" }}
          >
            <i className={`bi ${isExpanded ? "bi-chevron-up" : "bi-chevron-down"}`} />
            <span>{isExpanded ? "Menos" : "Detalles"}</span>
          </button>
        </div>
      </div>
  );
}

export default Products;