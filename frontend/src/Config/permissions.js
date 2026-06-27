export const matriz_permisos = {
  ADMINISTRADOR: { verTabsConsulta: true, crear: true,  actualizar: true,  eliminar: true,
    verModuloUsuarios: true, puedeUsarCarrito: false, verReportes: true  },

  EMPLEADO:      { verTabsConsulta: true, crear: true,  actualizar: false, eliminar: false,
    verModuloUsuarios: false, puedeUsarCarrito: false, verReportes: false },

  SOPORTE:       { verTabsConsulta: true, crear: true,  actualizar: true,  eliminar: false,
    verModuloUsuarios: true,  puedeUsarCarrito: true,  verReportes: false },

  CLIENTE:       { verTabsConsulta: true, crear: false, actualizar: false, eliminar: false,
    verModuloUsuarios: false, puedeUsarCarrito: true,  verReportes: false }
};

export const tieneAcceso = (rol, accion) => {
  if (!rol) return false;
  let rolLimpio = rol.toUpperCase().trim();
  if (rolLimpio === "ADMIN") rolLimpio = "ADMINISTRADOR";
  return matriz_permisos[rolLimpio]?.[accion] || false;
};