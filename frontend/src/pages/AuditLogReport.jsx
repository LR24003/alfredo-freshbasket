import React, { useState } from 'react';
import { useAuditLogs } from '../hooks/useAuditLogs';

export default function AuditLogReport() {
    const [filterType, setFilterType] = useState('all');
    const [filterValue, setFilterValue] = useState('');
    const [mostrarFiltros, setMostrarFiltros] = useState(false);

    const { data: logs, isLoading, isError, error } = useAuditLogs(filterType, filterValue);
    const dataList = logs || [];

    const handleFilterTypeChange = (e) => {
        setFilterType(e.target.value);
        setFilterValue('');
    };

    return (
        <div className="container-fluid px-3 pb-3 pt-0 fb-form-container position-relative">
            <button
                className="btn btn-primary rounded-circle shadow position-fixed d-flex align-items-center justify-content-center"
                style={{
                    bottom: "25px", right: "25px", width: "55px", height: "55px",
                    zIndex: "990", fontSize: "1.5rem"
                }}
                onClick={() => setMostrarFiltros(!mostrarFiltros)}
                title={mostrarFiltros ? "Cerrar filtros" : "Abrir filtros"}
                type="button"
            >
                <i className={`bi ${mostrarFiltros ? "bi-x-lg" : "bi-sliders"}`}></i>
            </button>
            {mostrarFiltros && (
                <div className="position-sticky bg-transparent pb-2 mb-3 w-100" style={{ top: "-24px", paddingTop: "4px" }}>
                    <div className="card p-3 mb-0 shadow border-0 bg-white">
                        <div className="row g-3 align-items-end p-2 bg-light rounded shadow-inner">
                            <div className="col-md-4">
                                <label className="form-label fw-bold text-muted small">Filtrar por:</label>
                                <select
                                    className="form-select bg-white text-dark"
                                    value={filterType}
                                    onChange={handleFilterTypeChange}
                                >
                                    <option value="all">Mostrar todos los registros</option>
                                    <option value="username">Buscar por usuario</option>
                                    <option value="action">Buscar por acción</option>
                                </select>
                            </div>

                            {/* Campo de Búsqueda Dinámica */}
                            {filterType !== 'all' && (
                                <div className="col-md-8">
                                    <label className="form-label fw-bold text-muted small">Valor de búsqueda:</label>
                                    <div className="position-relative w-100">
                                        <i
                                            className="bi bi-search text-muted position-absolute"
                                            style={{ left: "12px", top: "50%", transform: "translateY(-50%)", zIndex: "5", fontSize: "0.95rem" }}
                                        />
                                        {filterType === 'username' ? (
                                            <input
                                                type="text"
                                                className="form-control bg-white text-dark"
                                                placeholder="Escribe el nombre del usuario..."
                                                value={filterValue}
                                                onChange={(e) => setFilterValue(e.target.value)}
                                                style={{ paddingLeft: "35px" }}
                                            />
                                        ) : (
                                            <select
                                                className="form-select bg-white text-dark"
                                                value={filterValue}
                                                onChange={(e) => setFilterValue(e.target.value)}
                                                style={{ paddingLeft: "35px" }}
                                            >
                                                <option value="">-- Selecciona una acción --</option>
                                                <option value="INSERT">INSERT</option>
                                                <option value="UPDATE">UPDATE</option>
                                                <option value="DELETE">DELETE</option>
                                            </select>
                                        )}
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            )}

            {/* 3. CABECERA STICKY UNIFORME */}
            <div className="fb-section-header d-flex justify-content-between align-items-center mb-3 mt-2 p-3 bg-white rounded shadow-sm" style={{ position: "sticky", top: "0", zIndex: "100" }}>
                <span className="text-muted fw-semibold">
                    {isLoading ? "Sincronizando con el servidor..." : `Mostrando ${dataList.length} registros de auditoría`}
                </span>
            </div>

            {/* 4. CONTENEDOR DE TABLA / RESULTADOS */}
            <div className="card shadow-sm border-0 bg-white">
                <div className="card-body p-0 text-dark">
                    {isError ? (
                        <div className="alert alert-danger m-3" role="alert">
                            <i className="bi bi-exclamation-triangle-fill me-2"></i>
                            Error al cargar reportes: {error.message}
                        </div>
                    ) : dataList.length === 0 && !isLoading ? (
                        <div className="col-12 w-100 d-flex justify-content-center align-items-center py-5" style={{ minHeight: "40vh" }}>
                            <div className="text-center text-muted fs-4">
                                <i className="bi bi-inbox display-4 d-block mb-2" style={{ lineHeight: "1" }}></i>
                                <span>No se encontraron registros</span>
                            </div>
                        </div>
                    ) : (
                        <div className="table-responsive">
                            <table className="table table-hover align-middle mb-0">
                                <thead className="table-dark text-uppercase small text-center">
                                <tr>
                                    <th scope="col" style={{ width: '90px' }}>ID</th>
                                    <th scope="col" className="text-center">Entidad</th>
                                    <th scope="col">ID ENTIDAD</th>
                                    <th scope="col" className="text-center">Usuario que modificó</th>
                                    <th scope="col">Acción</th>
                                    <th scope="col">Fecha / Hora</th>
                                </tr>
                                </thead>
                                <tbody className="text-center">
                                {dataList.map((log) => (
                                    <tr key={log.id}>
                                        <td className="fw-bold text-secondary">{log.id}</td>
                                        <td className="text-start fw-semibold text-primary">
                                            <i className="bi bi-box-seam me-2"></i>{log.entity}
                                        </td>
                                        <td>
                                                <span className="badge bg-light text-dark border">
                                                    ID: {log.entityId}
                                                </span>
                                        </td>
                                        <td className="text-start fw-semibold text-dark">
                                            {log.userName}
                                        </td>
                                        <td>
                                                <span className={`badge px-2.5 py-1.5 rounded-pill ${
                                                    log.action === 'INSERT' ? 'bg-success text-white' :
                                                        log.action === 'UPDATE' ? 'bg-warning text-dark' :
                                                            'bg-danger text-white'
                                                }`}>
                                                    {log.action}
                                                </span>
                                        </td>
                                        <td className="text-muted small">
                                            {(() => {
                                                if (!log.createdAt) return "Sin fecha";

                                                try {
                                                    let fechaNormalizada = log.createdAt;

                                                    if (typeof fechaNormalizada === "string") {
                                                        fechaNormalizada = fechaNormalizada.replace(" ", "T");
                                                        const partes = fechaNormalizada.split(".");
                                                        if (partes.length > 1) {
                                                            fechaNormalizada = partes[0] + "." + partes[1].substring(0, 3);
                                                        }
                                                    }

                                                    const fechaObj = new Date(fechaNormalizada);

                                                    if (isNaN(fechaObj.getTime())) {
                                                        return String(log.createdAt);
                                                    }

                                                    return fechaObj.toLocaleString('es-ES', {
                                                        year: 'numeric', month: '2-digit', day: '2-digit',
                                                        hour: '2-digit', minute: '2-digit', second: '2-digit'
                                                    });
                                                } catch (e) {
                                                    return String(log.createdAt);
                                                }
                                            })()}
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}