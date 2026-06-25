import React, { useState } from "react";
import FormLayout from "../components/FormLayout.jsx";
import { useEntity } from "../hooks/useEntity.js";
import { useCart } from "../hooks/useCart.js";
import { toast } from 'react-hot-toast';

function Products() {
  const userLogin = localStorage.getItem("userName") || localStorage.getItem("userEmail") || "";

  const categories = useEntity("categories");
  const suppliers = useEntity("suppliers");

  const { cart, updateQuantity } = useCart();

  const categoriesList = categories.list.data || [];
  const suppliersList = suppliers.list.data || [];

  const getProductFields = (isEditMode) => [
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
      label: isEditMode ? "Usuario que actualiza:" : "Usuario que registra:",
      name: "userName",
      icon: "bi-person-badge",
      defaultValue: userLogin,
      disabled: true
    }
  ];

  const renderProductCard = (p) => (
      <ProductCard
          key={p.id || p.productId || p.products_id}
          p={p}
          cart={cart}
          updateQuantity={updateQuantity}
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

function ProductCard({ p, cart, updateQuantity }) {
  const [isExpanded, setIsExpanded] = useState(false);
  const productId = p.id ?? p.productId ?? p.products_id;

  const itemEnCarrito = cart?.items?.find(item => item.productId === productId);
  const cantidadActual = itemEnCarrito ? itemEnCarrito.quantity : 0;

  const precioNumerico = Number(p.price || 0);
  const porcentajeDescuento = Number(p.discount || 0);
  const dineroDescontado = (precioNumerico * porcentajeDescuento) / 100;

  const handleAgregarAlCarrito = (e) => {
    if (e) {
      e.preventDefault();
      e.stopPropagation();
    }

    const siguienteCantidad = cantidadActual === 0 ? 1 : cantidadActual + 1;

    updateQuantity(
        { productId, quantity: siguienteCantidad },
        {
          onSuccess: () => {
            toast.success('¡Producto añadido a tu carrito!', {
              id: `add-cart-${productId}`,
              duration: 1500,
              icon: '🛒'
            });
          },
          onError: (error) => {
            toast.error(
                error.response?.data?.message || 'No se pudo actualizar el carrito',
                { id: 'cart-error' }
            );
          }
        }
    );
  };

  return (
      <div className="d-flex flex-direction-column justify-content-between h-100 w-100"
           style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>

        <div className="d-flex justify-content-between align-items-start gap-2 mb-2">
          <h6 className="fw-bold text-dark m-0 small lh-sm text-wrap text-truncate"
              style={{ display: "-webkit-box", WebkitLineClamp: "2", WebkitBoxOrient: "vertical", overflow: "hidden", height: "2.4rem" }}>
            {p.name || "Producto sin nombre"}
          </h6>
          <span className="badge bg-secondary-subtle text-secondary flex-shrink-0 style-id-fallback"
                style={{ fontSize: "0.7rem", marginTop: "0.1rem" }}>
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
          {/* INFO SIEMPRE VISIBLE */}
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

          {/* INFO DESPLEGABLE */}
          {isExpanded && (
              <div className="fb-product-card-extra-info pt-2 border-top mt-auto" style={{ fontSize: "0.8rem" }}>
                <p className="mb-1">
                  <i className="bi bi-hash text-muted" /> <strong>Stock actual:</strong> {p.currentStock ?? p.stockActual ?? p.stock ?? 0}
                </p>
                <p className="mb-1">
                  <i className="bi bi-bookmark-star text-muted" /> <strong>Categoría:</strong> {p.categoryName || "Sin categoría"}
                </p>
                <p className="mb-1">
                  <i className="bi bi-truck text-muted" /> <strong>Proveedor:</strong> {p.supplierName || "Sin proveedor"}
                </p>
                <p className="mb-1">
                  <i className="bi bi-person text-muted" /> <strong> Registrado por:</strong> {p.userName || "Sin usuario"}
                </p>
              </div>
          )}
        </div>
        <div className="d-flex justify-content-center align-items-center gap-2 pt-2 border-top mt-auto">
          {/* BOTÓN AGREGAR */}
          <button
              type="button"
              onClick={(e) => handleAgregarAlCarrito(e)}
              className="btn btn-primary btn-sm d-flex align-items-center justify-content-center gap-1 flex-fill w-50 py-1"
              style={{ fontSize: "0.8rem" }}
          >
            <i className="bi bi-cart-plus-fill" />
            <span className="text-truncate">
            {cantidadActual > 0 ? `Carrito: ${cantidadActual}` : "Agregar"}
          </span>
          </button>

          {/* BOTÓN DETALLES */}
          <button
              type="button"
              onClick={() => setIsExpanded(!isExpanded)}
              className={`btn btn-sm d-flex align-items-center justify-content-center gap-1 flex-fill w-50 py-1 
              ${ isExpanded ? "btn-secondary" : "btn-outline-secondary"
              }`}
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