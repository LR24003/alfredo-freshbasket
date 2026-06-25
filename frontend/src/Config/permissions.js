
export const matriz_permisos = {
  ADMINISTRADOR: { verTabsConsulta: true, crear: true,  actualizar: true,  eliminar: true,  verModuloUsuarios: true,  puedeUsarCarrito: false },
  EMPLEADO:      { verTabsConsulta: true, crear: true,  actualizar: false, eliminar: false, verModuloUsuarios: false, puedeUsarCarrito: false },
  SOPORTE:       { verTabsConsulta: true, crear: true,  actualizar: true,  eliminar: false, verModuloUsuarios: true,  puedeUsarCarrito: true  },
  CLIENTE:       { verTabsConsulta: true, crear: false, actualizar: false, eliminar: false, verModuloUsuarios: false, puedeUsarCarrito: true  }
};

export const tieneAcceso = (rol, accion) => {
  if (!rol) return false;
  let rolLimpio = rol.toUpperCase().trim();
  if (rolLimpio === "ADMIN") rolLimpio = "ADMINISTRADOR";
  return matriz_permisos[rolLimpio]?.[accion] || false;
};