import "../styles/freshbasket.css";
import { NotificationBell } from "../components/NotificationBell";
import { useCart } from "../hooks/useCart";
import { useMenu } from "../hooks/useMenu";
import React, { useState, useEffect, useRef } from "react";
import { useNavigate, Outlet, useLocation } from "react-router-dom";
import { tieneAcceso } from "../Config/permissions";

function Freshbasket({ onLogout }) {
  const navigate = useNavigate();
  const location = useLocation();

  const userRole = (localStorage.getItem("userRole") || "USUARIO").toUpperCase().trim();
  const userEmail = localStorage.getItem("userEmail") || "correodeejemplo@mail.com";

  const { menuItems } = useMenu(userRole);
  const { cart, refetch } = useCart();
  const totalArticulos = cart?.items?.reduce((sum, item) => sum + item.quantity, 0) || 0;

  useEffect(() => {
    if (refetch) refetch();
  }, [location.pathname, refetch]);

  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const isAdmin = userRole === "ADMINISTRADOR" || userRole === "ADMIN";
  const profileRef = useRef(null);

  useEffect(() => {
    if (!localStorage.getItem("token")) navigate("/login");
  }, [navigate]);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (profileRef.current && !profileRef.current.contains(event.target)) {
        setShowProfileMenu(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    localStorage.removeItem("userName");
    localStorage.removeItem("userEmail");
    if (onLogout) onLogout();
    navigate("/login");
  };

  // Helper dinámico para resolver el título sin romper la navegación por subrutas
  const getTopbarTitle = () => {
    const path = location.pathname.toLowerCase();
    if (path.includes("cart")) return "Mi Carrito";
    if (path.includes("reportes")) return "Reportes";

    const coincidenciaMenu = menuItems.find(m => path === m.path.toLowerCase());
    return coincidenciaMenu ? coincidenciaMenu.label : "Panel";
  };

  return (
      <div className="fb-root">
        {mobileMenuOpen && (
            <div className="fb-sidebar-overlay" onClick={() => setMobileMenuOpen(false)} />
        )}
        {/* SIDEBAR */}
        <div className={`fb-sidebar ${mobileMenuOpen ? "open" : ""}`}>
          <div className="fb-sidebar-brand">
            <div style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
              <i className="bi bi-basket3-fill fb-sidebar-brand-icon" />
              <span className="fb-sidebar-brand-name">FreshBasket</span>
            </div>
            <button className="fb-sidebar-close-btn" onClick={() => setMobileMenuOpen(false)} aria-label="Cerrar menú">
              <i className="bi bi-x-lg" />
            </button>
          </div>
          <div className="fb-sidebar-section">MÓDULOS</div>
          <nav className="fb-nav fb-nav-mobile-stack">
            {menuItems.map((item) => {
              const isActive = location.pathname === item.path || (item.path !== "/freshbasket" && location.pathname.startsWith(item.path));

              return (
                  <div key={item.key} style={{ width: "100%" }}>
                    <button
                        className={`fb-nav-item ${isActive ? "fb-nav-item-active" : ""}`}
                        onClick={() => {
                          if (item.path) navigate(item.path);
                          setMobileMenuOpen(false);
                        }}
                    >
                      <div className="fb-nav-item-content" style={{ display: "flex", alignItems: "center", gap: "0.75rem", flex: 1 }}>
                        {item.icon && <i className={`bi ${item.icon} fb-nav-icon`} style={{ fontSize: "1.1rem" }} />}
                        <span>{item.label}</span>
                      </div>
                    </button>
                  </div>
              );
            })}
          </nav>
        </div>

        {/* PANEL CONTENT AREA */}
        <div className="fb-main">
          <div className="fb-topbar">
            <div style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
              <button className="fb-hamburger-btn" onClick={() => setMobileMenuOpen(true)} aria-label="Abrir menú">
                <i className="bi bi-list" />
              </button>
              <div className="fb-mobile-logo">
                <i className="bi bi-basket3-fill" />
                <span className="fb-mobile-brand-title">FreshBasket</span>
              </div>
              <div className="fb-desktop-titles">
                {/* 🌟 Título dinámico corregido y protegido */}
                <h2 className="fb-top-title">{getTopbarTitle()}</h2>
                <p style={{ margin: 0 }} className="fb-top-sub">Bienvenido/a</p>
              </div>
            </div>
            <div className="fb-top-right" style={{ display: "flex", alignItems: "center", gap: "1.2rem" }}>
              {tieneAcceso(userRole, "puedeUsarCarrito") && (
                  <button type="button" onClick={() => navigate("/freshbasket/cart")} className="fb-topbar-cart-btn" title="Ver mi carrito">
                    <i className="bi bi-cart3 text-dark" style={{ fontSize: "1.3rem" }} />
                    {totalArticulos > 0 && <span className="fb-cart-badge">{totalArticulos}</span>}
                  </button>
              )}
              <NotificationBell isAdmin={isAdmin} />
              <div className="fb-profile-container" ref={profileRef} style={{ position: "relative" }}>
                <button onClick={() => setShowProfileMenu(!showProfileMenu)} className="fb-logout-btn fb-profile-trigger-btn">
                  <i className="bi bi-person-circle fb-profile-icon" />
                  <span className="fb-profile-text-desktop">Perfil</span>
                  <i className={`bi ${showProfileMenu ? "bi-chevron-up" : "bi-chevron-down"} fb-profile-arrow`} />
                </button>
                {showProfileMenu && (
                    <div className="fb-profile-dropdown">
                      <div className="fb-profile-header">
                        <span className={`fb-role-badge ${userRole}`}>{userRole}</span>
                        <h6 className="fb-profile-name fw-bold text-dark mt-2 mb-1" style={{ fontSize: "0.95rem" }}>
                          {localStorage.getItem("userName") || "Usuario Registrado"}
                        </h6>
                        <p className="fb-profile-email"><i className="bi bi-envelope-fill" /> {userEmail}</p>
                      </div>
                      <button onClick={() => { navigate("my-profile"); setShowProfileMenu(false); }}
                              className="fb-profile-edit-btn fb-profile-edit-action-btn mb-2"
                              style={{ width: "100%", textAlign: "left" }}>
                        <i className="bi bi-gear-fill" /> Actualizar datos
                      </button>
                      <button onClick={handleLogout} className="fb-logout-btn fb-profile-logout-action-btn">
                        <i className="bi bi-box-arrow-left" /> Salir
                      </button>
                    </div>
                )}
              </div>
            </div>
          </div>

          {/* VISTA DINÁMICA DE CONTENIDO */}
          <div className="fb-content">
            {location.pathname === "/freshbasket" && (
                <div className="fb-photo-section" style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "calc(100vh - 90px)", width: "100%", padding: "2rem" }}>
                  <img src="/logo1.png" alt="Foto principal FreshBasket" className="fb-photo" style={{ width: "100%", maxWidth: "700px", objectFit: "contain" }} />
                </div>
            )}
            <Outlet />
          </div>
        </div>
      </div>
  );
}

export default Freshbasket;