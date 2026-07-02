import React, { useState } from 'react';
import { useUserReport } from '../hooks/useUserReport';
import { downloadReportFile } from '../utils/downloadHelper';

export default function UserReport() {
    const [filterType, setFilterType] = useState('all');
    const [filterValues, setFilterValues] = useState({
        id: '',
        fullName: '',
        role: '',
        countryName: '',
        estado: ''
    });
    const [mostrarFiltros, setMostrarFiltros] = useState(false);

    const { data: users, isLoading, isError, error } = useUserReport(filterType, filterValues);

    const dataList = users || [];

    const handleFilterTypeChange = (e) => {
        setFilterType(e.target.value);
        setFilterValues({
            id: '',
            fullName: '',
            role: '',
            countryName: '',
            estado: ''
        });
    };

    const handleInputChange = (field, value) => {
        setFilterValues(prev => ({
            ...prev,
            [field]: value
        }));
    };

    const getStatusBadgeColor = (status) => {
        if (!status) return 'bg-secondary text-white';
        const normalized = status.toLowerCase();
        if (normalized === 'activo') return 'bg-success text-white';
        if (normalized === 'inactivo') return 'bg-danger text-white';
        return 'bg-secondary text-white';
    };

    return (
        <div className="container-fluid px-3 pb-3 pt-0 fb-form-container position-relative">
            {/* Botón flotante para filtros */}
            <button
                className="btn btn-primary rounded-circle shadow position-fixed d-flex align-items-center justify-content-center"
                style={{ bottom: "25px", right: "25px", width: "55px", height: "55px", zIndex: "990", fontSize: "1.5rem" }}
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
                            <div className="col-md-3">
                                <label className="form-label fw-bold text-muted small">Filtrar por:</label>
                                <select
                                    className="form-select bg-white text-dark small"
                                    value={filterType}
                                    onChange={handleFilterTypeChange}
                                    style={{ fontSize: '0.875rem' }}
                                >
                                    <option value="all">Mostrar todos los usuarios</option>
                                    <option value="id">Buscar por ID</option>
                                    <option value="username">Buscar por nombre</option>
                                    <option value="role">Filtrar por rol</option>
                                    <option value="country">Filtrar por país</option>
                                    <option value="state">Filtrar por estado</option>
                                </select>
                            </div>

                            {/* Campo: Búsqueda por ID */}
                            {filterType === 'id' && (
                                <div className="col-md-9">
                                    <label className="form-label fw-bold text-muted small">ID del usuario:</label>
                                    <input
                                        type="number"
                                        className="form-control bg-white text-dark small"
                                        placeholder="Ej. 12"
                                        value={filterValues.id}
                                        onChange={(e) => handleInputChange('id', e.target.value)}
                                        style={{ fontSize: '0.875rem' }}
                                    />
                                </div>
                            )}

                            {/* Campo: Búsqueda por Nombre de Usuario */}
                            {filterType === 'username' && (
                                <div className="col-md-9">
                                    <label className="form-label fw-bold text-muted small">Nombre completo:</label>
                                    <div className="position-relative w-100">
                                        <i className="bi bi-search text-muted position-absolute" style={{ left: "12px", top: "50%", transform: "translateY(-50%)", zIndex: "5", fontSize: "0.85rem" }} />
                                        <input
                                            type="text"
                                            className="form-control bg-white text-dark small"
                                            placeholder="Escribe el nombre a buscar..."
                                            value={filterValues.fullName}
                                            onChange={(e) => handleInputChange('fullName', e.target.value)}
                                            style={{ paddingLeft: "35px", fontSize: '0.875rem' }}
                                        />
                                    </div>
                                </div>
                            )}

                            {/* Campo: Filtro por Rol */}
                            {filterType === 'role' && (
                                <div className="col-md-9">
                                    <label className="form-label fw-bold text-muted small">Rol:</label>
                                    <input
                                        type="text"
                                        className="form-control bg-white text-dark small"
                                        placeholder="Ej. ADMINISTRADOR, CLIENTE..."
                                        value={filterValues.role}
                                        onChange={(e) => handleInputChange('role', e.target.value)}
                                        style={{ fontSize: '0.875rem' }}
                                    />
                                </div>
                            )}

                            {/* Campo: Filtro por País */}
                            {filterType === 'country' && (
                                <div className="col-md-9">
                                    <label className="form-label fw-bold text-muted small">País de origen:</label>
                                    <input
                                        type="text"
                                        className="form-control bg-white text-dark small"
                                        placeholder="Ej. México, Colombia..."
                                        value={filterValues.countryName}
                                        onChange={(e) => handleInputChange('countryName', e.target.value)}
                                        style={{ fontSize: '0.875rem' }}
                                    />
                                </div>
                            )}

                            {/* Campo: Filtro por Estado */}
                            {filterType === 'state' && (
                                <div className="col-md-9">
                                    <label className="form-label fw-bold text-muted small">Estado:</label>
                                    <select
                                        className="form-select bg-white text-dark small"
                                        value={filterValues.estado}
                                        onChange={(e) => handleInputChange('estado', e.target.value)}
                                        style={{ fontSize: '0.875rem' }}
                                    >
                                        <option value="">-- Selecciona un estado --</option>
                                        <option value="Activo">Activo</option>
                                        <option value="Inactivo">Inactivo</option>
                                    </select>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            )}

            {/* CABECERA STICKY UNIFORME */}
            <div className="fb-section-header d-flex justify-content-between align-items-center mb-3 mt-2 p-2 px-3 bg-white rounded shadow-sm"
                 style={{ position: "sticky", top: "0", zIndex: "100" }}>
                <span className="text-secondary fw-bold small" style={{ fontSize: '0.95rem' }}>
                    {isLoading ? "Sincronizando con el servidor..." : `Mostrando ${dataList.length} registros de usuarios.`}
                </span>

                {/* GRUPO DE BOTONES DE EXPORTACIÓN */}
                <div className="d-flex gap-2">
                    <button
                        onClick={() => downloadReportFile('/api/user-report/export/excel', 'Reporte_Usuarios.xlsx')}
                        className="btn btn-outline-success btn-sm d-flex align-items-center gap-1 px-2 fw-semibold"
                        style={{ fontSize: '0.8rem' }}
                        disabled={isLoading || dataList.length === 0}
                    >
                        <i className="bi bi-file-earmark-excel-fill"></i> Excel
                    </button>
                    <button
                        onClick={() => downloadReportFile('/api/user-report/export/pdf', 'Reporte_Usuarios.pdf')}
                        className="btn btn-outline-danger btn-sm d-flex align-items-center gap-1 px-2 fw-semibold"
                        style={{ fontSize: '0.8rem' }}
                        disabled={isLoading || dataList.length === 0}
                    >
                        <i className="bi bi-file-earmark-pdf-fill"></i> PDF
                    </button>
                </div>
            </div>

            {/* CONTENEDOR DE TABLA / RESULTADOS */}
            <div className="card shadow-sm border-0 bg-white">
                <div className="card-body p-0 text-dark">
                    {isError ? (
                        <div className="alert alert-danger m-3 small" role="alert">
                            <i className="bi bi-exclamation-triangle-fill me-2"></i>
                            Error al cargar el reporte: {error?.message || "Error inesperado"}
                        </div>
                    ) : dataList.length === 0 && !isLoading ? (
                        <div className="col-12 w-100 d-flex justify-content-center align-items-center py-5" style={{ minHeight: "40vh" }}>
                            <div className="text-center text-muted fs-5">
                                <i className="bi bi-inbox display-5 d-block mb-2" style={{ lineHeight: "1" }}></i>
                                <span>No se encontraron registros de usuarios</span>
                            </div>
                        </div>
                    ) : (
                        <div className="table-responsive">
                            <table className="table table-hover align-middle mb-0" style={{ fontSize: '0.85rem' }}>
                                <thead className="table-dark text-uppercase" style={{ fontSize: '0.75rem', letterSpacing: '0.5px' }}>
                                <tr>
                                    <th scope="col" className="text-center py-2" style={{ width: '80px' }}>ID</th>
                                    <th scope="col" className="text-start py-2 ps-3">NOMBRE COMPLETO</th>
                                    <th scope="col" className="text-center py-2">ROL</th>
                                    <th scope="col" className="text-center py-2">PAÍS</th>
                                    <th scope="col" className="text-center py-2">ESTADO</th>
                                </tr>
                                </thead>
                                <tbody>
                                {dataList.map((user) => {
                                   if (!user) return null;
                                    return (
                                        <tr key={user.id}>
                                            <td className="fw-bold text-secondary text-center py-2">{user.id}</td>

                                            {/* NOMBRE COMPLETO Y EMAIL EN LA MISMA CELDA */}
                                            <td className="text-start py-2 ps-3">
                                                <div className="fw-semibold text-primary">{user.fullName}</div>
                                                <div className="text-muted" style={{ fontSize: '0.75rem' }}>{user.email}</div>
                                            </td>

                                            {/* ROL */}
                                            <td className="text-center fw-semibold text-secondary py-2">
                                                <span className="badge bg-light text-dark border px-2 py-1">
                                                    {user.role}
                                                </span>
                                            </td>

                                            {/* PAÍS */}
                                            <td className="text-center text-dark py-2">
                                                {user.countryName || 'No Asignado'}
                                            </td>

                                            {/* ESTADO */}
                                            <td className="text-center py-2">
                                                <span className={`badge px-2.5 py-1.5 rounded-pill ${getStatusBadgeColor(user.estado)}`} style={{ fontSize: '0.75rem' }}>
                                                    {user.estado || 'Activo'}
                                                </span>
                                            </td>
                                        </tr>
                                    );
                                })}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}