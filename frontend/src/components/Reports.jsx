import React from 'react';
import { useNavigate } from 'react-router-dom';

export default function Reports() {
    const navigate = useNavigate();
    const reportes = [
        {
          id: 'auditoria',
          titulo: 'Reporte de auditoría de cambios (Logs)',
          descripcion: 'Historial detallado de inserciones, ediciones y eliminaciones hechas por los usuarios.',
          icon: 'bi-shield-check',
          color: 'text-white',
          bg: 'bg-primary',
          enabled: true,
          path: '/freshbasket/reportes/auditoria'
        },
        { id: 'clientes',
          titulo: 'Reporte de compras por clientes (Fidelidad)',
          descripcion: 'Análisis de ventas, total de compras por clientes fieles.',
          icon: 'bi-graph-up-arrow',
          color: 'text-white',
          bg: 'bg-primary',
          enabled: true,
          path: '/freshbasket/reportes/clientes'
        },
        { id: 'inventario',
          titulo: 'Reporte de inventario de productos',
          descripcion: 'Balance de existencias, productos próximos a agotarse y alertas de reposición.',
          icon: 'bi-box-seam',
          color: 'text-white',
          bg: 'bg-primary',
          enabled: true,
          path: '/freshbasket/reportes/inventario'
        },
        { id: 'ventas',
          titulo: 'Reporte de ventas',
          descripcion: 'Filtrado de ventas por rango de fechas y nombre del empleado.',
          icon: 'bi-stars',
          color: 'text-white',
          bg: 'bg-primary',
          enabled: true,
          path: '/freshbasket/reportes/ventas'
        },
        { id: 'inventario-perdido',
          titulo: 'Historial de inventario perdido o dañado',
          descripcion: 'Registro de inventario de todos los productos perdidos o dañados .',
            icon: 'bi-arrow-right-circle',
          color: 'text-white',
          bg: 'bg-primary',
          enabled: true,
          path: '/freshbasket/reportes/inventario-perdido'
        },
        { id: 'productos mas vendidos',
          titulo: 'Historial de productos mas vendidos',
          descripcion: 'Bitácora completa de los productos que se venden mas, ordenados desde mas a menos.',
          icon: 'bi-arrow-up-circle',
          color: 'text-white',
          bg: 'bg-primary',
          enabled: true,
          path: '/freshbasket/reportes/productos'
        },
        { id: 'usuarios',
          titulo: 'Reporte de usuarios',
          descripcion: 'Reporte de usuarios registrados que están activos e inactivos, ademas de sus roles.',
          icon: 'bi-people',
          color: 'text-white',
          bg: 'bg-primary',
          enabled: true,
          path: '/freshbasket/reportes/usuarios'
        },

        { id: 'proveedores',
          titulo: 'Reporte de proveedores',
          descripcion: 'Historial de productos entregados, productos principales y total inventario disponible.',
          icon: 'bi-truck',
          color: 'text-white',
          bg: 'bg-primary',
          enabled: true,
          path: "/freshbasket/reportes/proveedores"
        }
    ];

    return (
        <div className="container-fluid px-3 pb-3 pt-0 fb-form-container">
            <div className="fb-section-header d-flex justify-content-between align-items-center mb-3 mt-2 p-3 bg-white rounded shadow-sm">
                <div>
                    <h4 className="fw-bold text-dark m-0" style={{ fontSize: "1.1rem" }}>Módulo Gerencial de reportes</h4>
                    <p className="text-muted small m-0">Seleccione el reporte analítico que desea consultar.</p>
                </div>
            </div>

            {/* Grid unificado */}
            <div className="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-xxl-4 g-4">
                {reportes.map((rep) => (
                    <div className="col" key={rep.id}>
                        <div
                            className={`card h-100 shadow-sm border-0 position-relative bg-white ${rep.enabled ? 'border-start border-primary border-4' : 'bg-light opacity-50'}`}
                            onClick={() => rep.enabled && navigate(rep.path)}
                            style={{ cursor: rep.enabled ? 'pointer' : 'not-allowed', transition: 'transform 0.2s' }}
                        >
                            <div className="card-body d-flex flex-column p-4 text-dark">
                                <div className={`d-inline-flex align-items-center justify-content-center p-3 rounded-3 mb-3 shadow-sm ${rep.bg}`} style={{ width: '50px', height: '50px' }}>
                                    <i className={`bi ${rep.icon} fs-4 ${rep.color}`}></i>
                                </div>

                                <h5 className="fw-bold text-dark mb-2" style={{ fontSize: "1.05rem" }}>{rep.titulo}</h5>
                                <p className="text-muted small flex-grow-1 mb-3">{rep.descripcion}</p>

                                <div className="d-flex justify-content-between align-items-center mt-auto">
                                    {rep.enabled ? (
                                        <span className="badge bg-success text-white px-2.5 py-1.5 rounded-pill small">
                                        Ver <i className="bi bi-arrow-right ms-1"></i>
                                        </span>
                                        ) : (
                                        <span className="badge bg-secondary text-white px-2.5 py-1.5 rounded-pill small">
                                      <i className="bi bi-lock-fill me-1"></i> Bloqueado
                                     </span>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}