import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import FormLayout from "../components/FormLayout.jsx";
import { useEntity } from "../hooks/useEntity.js";

function Users() {
  const countries = useEntity("countries");
  const countriesList = countries.list.data || [];

  const location = useLocation();
  const navigate = useNavigate();
  const queryParams = new URLSearchParams(location.search);

  const isFromVentas = queryParams.get("from") === "ventas";
  const userRoleLogged = localStorage.getItem("userRole")?.toUpperCase();
  const esCreacionDirecta = location.pathname.endsWith("/nuevo") || isFromVentas;

  let userFields = [
    {
      label: "Nombre",
      name: "name",
      icon: "bi-person",
      placeholder: "Nombre del usuario"
    },
    {
      label: "Apellido",
      name: "lastName",
      icon: "bi-person",
      placeholder: "Apellido del usuario"
    },
    {
      label: "Teléfono",
      name: "phone",
      icon: "bi-telephone",
      placeholder: "Ej: 7777-7777"
    },
    {
      label: "Email",
      name: "email",
      icon: "bi-envelope",
      type: "email",
      placeholder: "correo@ejemplo.com"
    },
    {
      label: "Rol",
      name: "role",
      icon: "bi-person-badge",
      placeholder: "Selecciona un rol de la lista",
      list: "users-roles-datalist",
      options: ["CLIENTE", "ADMINISTRADOR", "SOPORTE", "EMPLEADO"]
    },
    {
      label: "Contraseña",
      name: "password",
      icon: "bi-lock",
      type: "password",
      placeholder: "••••••••",
      disabledOnUpdate: true,
      requiredOnUpdate: false,
      classNameOnUpdate: "fb-hidden-password-field"
    },
    {
      label: "País",
      name: "countryName",
      icon: "bi-globe",
      placeholder: "Selecciona o escribe un país",
      list: "users-countries-datalist",
      options: countriesList.map(c => c.name || c.countryName).filter(Boolean)
    }
  ];

  if (isFromVentas) {
    userFields = userFields.filter(field => field.name !== "role");
  }

  const renderUserCard = (u2) => {
    if (esCreacionDirecta) return null;

    const targetRole = (u2.role || "CLIENTE").toUpperCase();
    if (userRoleLogged === "EMPLEADO" && targetRole !== "CLIENTE") {
      return null;
    }

    const userId = u2.id ?? u2.userId ?? u2.user_id ?? u2.users_id;
    const lastNameStr = u2.lastName ?? u2.last_name ?? "";
    const fullName = `${u2.name || "Usuario sin nombre"} ${lastNameStr}`.trim();
    const countryDisplay = u2.countryName ?? u2.country_name ?? u2.country?.name ?? "Sin país asignado";

    return (
        <div key={userId} className="d-flex flex-column justify-content-between h-100 w-100" style={{ minHeight: "100%" }}>
          <div>
            <div className="d-flex justify-content-between align-items-start gap-2 mb-2">
              <h6 className="fw-bold text-dark m-0 small lh-sm text-wrap text-truncate"
                  style={{ display: "-webkit-box", WebkitLineClamp: "2", WebkitBoxOrient: "vertical", overflow: "hidden", height: "2.4rem" }}>
                {fullName}
              </h6>
              <span className="badge bg-secondary-subtle text-secondary flex-shrink-0" style={{ fontSize: "0.7rem", marginTop: "0.1rem" }}>
                ID: {userId}
              </span>
            </div>
          </div>
          <div className="flex-grow-1 mb-2 d-flex flex-column justify-content-start text-muted" style={{ fontSize: "0.85rem" }}>
            <p className="mb-2 text-dark text-truncate">
              <i className="bi bi-envelope text-muted me-2" />
              {u2.email || "No disponible"}
            </p>
            <p className="mb-3 text-dark">
              <i className="bi bi-telephone text-muted me-2" />
              {u2.phone || "Sin teléfono registrado"}
            </p>
            <div className="d-flex gap-2 align-items-center pt-2 border-top mb-2">
              <i className="bi bi-person-badge text-muted" style={{ fontSize: "0.75rem" }} />
              <div>
              <span className="badge bg-primary-subtle text-primary border border-primary-subtle px-2 py-0.5 text-uppercase" style={{ fontSize: '0.65rem' }}>
              {u2.role || "CLIENTE"}
               </span>
              </div>
            </div>
            <div className="d-flex gap-2 align-items-center mb-1">
              <i className="bi bi-globe text-success" style={{ fontSize: "0.75rem" }} />
              <div>
                <span className="text-secondary d-block lh-sm fw-semibold">{countryDisplay}</span>
              </div>
            </div>
          </div>

        </div>
    );
  };

  return (
      <FormLayout
          resource="users"
          title={isFromVentas ? "Cliente desde Caja" : "usuario"}
          article={isFromVentas ? "al" : "el"}
          icon="bi-person-fill-add"
          searchField="name"
          fields={userFields}
          renderCard={renderUserCard}
          forcedMode={esCreacionDirecta ? "create" : "list"}
          onBeforeSave={(formData, mode) => {
            const datosListos = { ...formData };

            if (mode === "update") {
              if (!datosListos.password || datosListos.password.trim() === "" || datosListos.password === "••••••••") {
                datosListos.password = "NO_CHANGED";
              }
            }

            if (isFromVentas) {
              datosListos.role = "CLIENTE";
              if (!datosListos.password || datosListos.password === "NO_CHANGED") {
                datosListos.password = "ClienteFresh2026*";
              }
            }
            return datosListos;
          }}
          onSuccessHook={() => {
            if (isFromVentas) {
              navigate("/freshbasket/ventas");
            }
          }}
      />
  );
}

export default Users;