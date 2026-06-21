import { useMemo } from "react";
import { tieneAcceso } from "../Config/permissions";

export function useMenu(userRole) {
    // 1. MENÚ PRINCIPAL
    const menuItems = useMemo(() => {
        return [
            { key: "home",         icon: "bi-house-door",         label: "Inicio",        path: "/freshbasket" },
            { key: "productos",    icon: "bi-basket3",            label: "Productos",     path: "/freshbasket/productos", hasSubmenu: true },

            ...(tieneAcceso(userRole, "crear") ? [
                { key: "ventas",       icon: "bi-cash-coin",          label: "Ventas (POS)",  path: "/freshbasket/ventas", hasSubmenu: false }
            ] : []),
            ...(tieneAcceso(userRole, "crear") ? [
                { key: "entradas",     icon: "bi-arrow-up-circle",    label: "Entradas" ,   path: "/freshbasket/entradas", hasSubmenu: true }
            ] : []),
            ...(tieneAcceso(userRole, "crear") ? [
                { key: "salidas",      icon: "bi-arrow-right-circle",  label: "Salidas" ,    path: "/freshbasket/salidas", hasSubmenu: true }
            ] : []),
            ...(tieneAcceso(userRole, "crear") ? [
                { key: "proveedores",  icon: "bi-truck",              label: "Proveedores", path: "/freshbasket/proveedores", hasSubmenu: true }
            ] : []),
            ...(tieneAcceso(userRole, "verModuloUsuarios") ? [
                { key: "usuarios",     icon: "bi-people",             label: "Usuarios",    path: "/freshbasket/usuarios", hasSubmenu: true }
            ] : []),
            ...(tieneAcceso(userRole, "crear") ? [
                { key: "categorias",   icon: "bi-list-stars",         label: "Categorias",  path: "/freshbasket/categorias", hasSubmenu: true }
            ] : []),
            ...(tieneAcceso(userRole, "crear") ? [
                { key: "paises",       icon: "bi-globe",              label: "Paises",      path: "/freshbasket/paises", hasSubmenu: true }
            ] : []),
        ];
    }, [userRole]);

    // 2. SUBMENÚS POR MÓDULO
    const productSubItems = useMemo(() => [
        ...(tieneAcceso(userRole, "verTabsConsulta") ? [
            { key: "all",    icon: "bi-basket3",    label: "Todos los productos" },
            { key: "name",   icon: "bi-search",           label: "Buscar por nombre" },
            { key: "category",   icon: "bi-search",           label: "Buscar por categoria" },
            { key: "id",     icon: "bi-tag-fill",         label: "Buscar por ID" }
        ] : []),
        ...(tieneAcceso(userRole, "crear") ? [{ key: "create", icon: "bi-plus-circle-fill", label: "Registrar producto" }] : []),
        ...(tieneAcceso(userRole, "actualizar") ? [{ key: "update", icon: "bi-pencil-square",    label: "Actualizar producto" }] : []),
        ...(tieneAcceso(userRole, "eliminar") ? [{ key: "delete", icon: "bi-trash3-fill",      label: "Eliminar producto" }] : [])
    ], [userRole]);

    const userSubItems = useMemo(() => [
        ...(tieneAcceso(userRole, "verModuloUsuarios") ? [
            { key: "all",    icon: "bi-people",    label: "Todos los usuarios" },
            { key: "name",   icon: "bi-search",           label: "Buscar por nombre" },
            { key: "id",     icon: "bi-tag-fill",         label: "Buscar por ID" }
        ] : []),
        ...(tieneAcceso(userRole, "crear") ? [{ key: "create", icon: "bi-plus-circle-fill", label: "Registrar usuario" }] : []),
        ...(tieneAcceso(userRole, "actualizar") ? [{ key: "update", icon: "bi-pencil-square",    label: "Actualizar usuario" }] : []),
        ...(tieneAcceso(userRole, "eliminar") ? [{ key: "delete", icon: "bi-trash3-fill",      label: "Eliminar usuario" }] : [])
    ], [userRole]);

    const supplierSubItems = useMemo(() => [
        ...(tieneAcceso(userRole, "verTabsConsulta") ? [
            { key: "all",    icon: "bi-truck",    label: "Todos los proveedores" },
            { key: "name",   icon: "bi-search",           label: "Buscar por nombre" },
            { key: "id",     icon: "bi-tag-fill",         label: "Buscar por ID" }
        ] : []),
        ...(tieneAcceso(userRole, "crear") ? [{ key: "create", icon: "bi-plus-circle-fill", label: "Registrar proveedor" }] : []),
        ...(tieneAcceso(userRole, "actualizar") ? [{ key: "update", icon: "bi-pencil-square",    label: "Actualizar proveedor" }] : []),
        ...(tieneAcceso(userRole, "eliminar") ? [{ key: "delete", icon: "bi-trash3-fill",      label: "Eliminar proveedor" }] : [])
    ], [userRole]);

    const entrySubItems = useMemo(() => [
        ...(tieneAcceso(userRole, "verTabsConsulta") ? [
            { key: "all",    icon: "bi-arrow-up-circle",    label: "Todas las entradas" },
            { key: "id",     icon: "bi-tag-fill",         label: "Buscar por ID" }
        ] : []),
        ...(tieneAcceso(userRole, "crear") ? [{ key: "create", icon: "bi-plus-circle-fill", label: "Registrar entrada" }] : []),
        ...(tieneAcceso(userRole, "actualizar") ? [{ key: "update", icon: "bi-pencil-square",    label: "Actualizar entrada" }] : []),
        ...(tieneAcceso(userRole, "eliminar") ? [{ key: "delete", icon: "bi-trash3-fill",      label: "Eliminar entrada" }] : [])
    ], [userRole]);

    const exitSubItems = useMemo(() => [
        ...(tieneAcceso(userRole, "verTabsConsulta") ? [
            { key: "all",    icon: "bi-arrow-right-circle",    label: "Todas las salidas" },
            { key: "id",     icon: "bi-tag-fill",         label: "Buscar por ID" }
        ] : []),
        ...(tieneAcceso(userRole, "crear") ? [{ key: "create", icon: "bi-plus-circle-fill", label: "Registrar salida" }] : []),
        ...(tieneAcceso(userRole, "actualizar") ? [{ key: "update", icon: "bi-pencil-square",    label: "Actualizar salida" }] : []),
        ...(tieneAcceso(userRole, "eliminar") ? [{ key: "delete", icon: "bi-trash3-fill",      label: "Eliminar salida" }] : [])
    ], [userRole]);

    const categorySubItems = useMemo(() => [
        ...(tieneAcceso(userRole, "verTabsConsulta") ? [
            { key: "all",    icon: "bi-list-stars",    label: "Todas las categorias" },
            { key: "name",   icon: "bi-tag-fill",         label: "Buscar por nombre" },
            { key: "id",     icon: "bi-tag-fill",         label: "Buscar por ID" }
        ] : []),
        ...(tieneAcceso(userRole, "crear") ? [{ key: "create", icon: "bi-plus-circle-fill", label: "Registrar categoria" }] : []),
        ...(tieneAcceso(userRole, "actualizar") ? [{ key: "update", icon: "bi-pencil-square",    label: "Actualizar categoria" }] : []),
        ...(tieneAcceso(userRole, "eliminar") ? [{ key: "delete", icon: "bi-trash3-fill",      label: "Eliminar categoria" }] : [])
    ], [userRole]);

    const countrySubItems = useMemo(() => [
        ...(tieneAcceso(userRole, "verTabsConsulta") ? [
            { key: "all",    icon: "bi-globe",    label: "Todos los paises" },
            { key: "name",   icon: "bi-tag-fill",         label: "Buscar por nombre" },
            { key: "id",     icon: "bi-tag-fill",         label: "Buscar por ID" }
        ] : []),
        ...(tieneAcceso(userRole, "crear") ? [{ key: "create", icon: "bi-plus-circle-fill", label: "Registrar pais" }] : []),
        ...(tieneAcceso(userRole, "actualizar") ? [{ key: "update", icon: "bi-pencil-square",    label: "Actualizar pais" }] : []),
        ...(tieneAcceso(userRole, "eliminar") ? [{ key: "delete", icon: "bi-trash3-fill",      label: "Eliminar pais" }] : [])
    ], [userRole]);

    // Helper dinámico para resolver qué submenú le corresponde al item activo de la barra lateral
    const getSubItems = (key) => {
        const subMenus = {
            productos: productSubItems,
            usuarios: userSubItems,
            proveedores: supplierSubItems,
            entradas: entrySubItems,
            salidas: exitSubItems,
            categorias: categorySubItems,
            paises: countrySubItems,
        };
        return subMenus[key] || [];
    };

    return { menuItems, getSubItems };
}