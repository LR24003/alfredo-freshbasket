import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import FormLayout from "../components/FormLayout.jsx";
import { useEntity } from "../hooks/useEntity.js";

function Users() {
  const countries = useEntity("countries");
  const countriesList = countries.list.data || [];

  // 🚀 DETECTAR ORIGEN Y RUTA DE CREACIÓN DIRECTA
  const location = useLocation();
  const navigate = useNavigate();
  const queryParams = new URLSearchParams(location.search);

  const isFromVentas = queryParams.get("from") === "ventas";
  const userRoleLogged = localStorage.getItem("userRole")?.toUpperCase();

  // 🌟 Evalúa si estamos en la subruta dedicada al formulario directo o viniendo de caja
  const esCreacionDirecta = location.pathname.endsWith("/nuevo") || isFromVentas;

  // Filtramos o modificamos los campos si el origen es la caja registradora
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

  // 🚀 ADAPTACIÓN EXPRESS PARA EL POS:
  if (isFromVentas) {
    userFields = userFields.filter(field => field.name !== "role");
  }

  // 🛡️ Si entra de forma directa desde ventas, retornamos null para blindar que no se pinte listado secundario
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
        <div key={userId} className="fb-user-display-card">
          <div className="fb-card-user-info">
            <h4 className="fb-card-user-title">{fullName}</h4>
            <span className="fb-card-user-id">ID: {userId}</span>
          </div>
          <div className="fb-card-user-body">
            <p className="fb-card-user-detail">
              <i className="bi bi-envelope" /> {u2.email || "No disponible"}
            </p>
            <p className="fb-card-user-detail">
              <i className="bi bi-telephone" /> {u2.phone || "Sin teléfono registrado"}
            </p>
            <p className="fb-card-user-detail">
              <i className="bi bi-person-badge" /> <span className="fb-role-badge">{u2.role || "CLIENTE"}</span>
            </p>
            <p className="fb-card-user-detail">
              <i className="bi-globe" />   <span className="fb-country">{countryDisplay}</span>
            </p>
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
            if (mode === "update" && (!formData.password || formData.password.trim() === "")) {

            }

            if (isFromVentas) {
              formData.role = "CLIENTE";

              if (!formData.password) {
                formData.password = "ClienteFresh2026*";
              }
            }
            return formData;
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