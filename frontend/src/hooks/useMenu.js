import { useMemo } from "react";
import { tieneAcceso } from "../Config/permissions";

export function useMenu(userRole) {
    const menuItems = useMemo(() => {
        const rolU = userRole?.toUpperCase().trim();
        const esCliente = rolU === "CLIENTE" || rolU === "CLIENT" || rolU === "USUARIO";

        return [
            { key: "home",         icon: "bi-house-door",         label: "Inicio",        path: "/freshbasket" },
            { key: "productos",    icon: "bi-basket3",            label: "Productos",     path: "/freshbasket/productos" },

            ...(tieneAcceso(userRole, "crear") && !esCliente ? [
                { key: "ventas",       icon: "bi-cash-coin",          label: "Ventas (POS)",  path: "/freshbasket/ventas" }
            ] : []),

            ...(tieneAcceso(userRole, "verTabsConsulta") && !esCliente ? [
                { key: "entradas",     icon: "bi-arrow-up-circle",    label: "Entradas",     path: "/freshbasket/entradas" },
                { key: "salidas",      icon: "bi-arrow-right-circle", label: "Salidas",      path: "/freshbasket/salidas" },
                { key: "proveedores",  icon: "bi-truck",              label: "Proveedores",  path: "/freshbasket/proveedores" },
                { key: "categorías",   icon: "bi-list-stars",         label: "Categorías",   path: "/freshbasket/categorias" },
                { key: "paises",       icon: "bi-globe",              label: "Países",       path: "/freshbasket/paises" }
            ] : []),

            ...(tieneAcceso(userRole, "verModuloUsuarios") ? [
                { key: "usuarios",     icon: "bi-people",             label: "Usuarios",     path: "/freshbasket/usuarios" }
            ] : []),
        ];
    }, [userRole]);

    return { menuItems };
}