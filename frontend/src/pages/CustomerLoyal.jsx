import React, { useState } from 'react';
import { useCustomerLoyal } from '../hooks/useCustomerLoyal';
import { downloadReportFile } from '../utils/downloadHelper.js';

export default function CustomerLoyal() {
    const [filterType, setFilterType] = useState('all');
    const [filterValue, setFilterValue] = useState('');
    const [mostrarFiltros, setMostrarFiltros] = useState(false);

    // Pasamos los filtros al hook para que realice la petición al servidor en cada cambio
    const { data: logs, isLoading, isError, error } = useCustomerLoyal(filterType, filterValue);
    const dataList = logs || [];

    const handleFilterTypeChange = (e) => {
        setFilterType(e.target.value);
        setFilterValue('');
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
                            <div className="col-md-4">
                                <label className="form-label fw-bold text-muted small">Filtrar por:</label>
                                <select
                                    className="form-select bg-white text-dark small"
                                    value={filterType}
                                    onChange={handleFilterTypeChange}
                                    style={{ fontSize: '0.875rem' }}
                                >
                                    <option value="all">Mostrar todos los registros</option>
                                    <option value="customername">Buscar por cliente</option>
                                    <option value="totalpurchases">Buscar por total de compras</option>
                                </select>
                            </div>

                            {/* Campo de Búsqueda Dinámica */}
                            {filterType !== 'all' && (
                                <div className="col-md-8">
                                    <label className="form-label fw-bold text-muted small">Valor de búsqueda:</label>
                                    <div className="position-relative w-100">
                                        <i className="bi bi-search text-muted position-absolute" style={{ left: "12px", top: "50%", transform: "translateY(-50%)", zIndex: "5", fontSize: "0.85rem" }} />
                                        {filterType === 'customername' ? (
                                            <input
                                                type="text"
                                                className="form-control bg-white text-dark small"
                                                placeholder="Escribe el nombre del cliente..."
                                                value={filterValue}
                                                onChange={(e) => setFilterValue(e.target.value)}
                                                style={{ paddingLeft: "35px", fontSize: '0.875rem' }}
                                            />
                                        ) : (
                                            <select
                                                className="form-select bg-white text-dark small"
                                                value={filterValue}
                                                onChange={(e) => setFilterValue(e.target.value)}
                                                style={{ paddingLeft: "35px", fontSize: '0.875rem' }}
                                            >
                                                <option value="">-- Selecciona un rango --</option>
                                                <option value="1 a 10">1 a 10</option>
                                                <option value="11 a 20">11 a 20</option>
                                                <option value="21+">Más de 20</option>
                                            </select>
                                        )}
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            )}

            {/* CABECERA STICKY UNIFORME */}
            <div className="fb-section-header d-flex justify-content-between align-items-center mb-3 mt-2 p-2 px-3 bg-white rounded shadow-sm" style={{ position: "sticky", top: "0", zIndex: "100" }}>
                <span className="text-secondary fw-bold small" style={{ fontSize: '0.95rem' }}>
                    {isLoading ? "Sincronizando con el servidor..." : `Mostrando ${dataList.length} registros de clientes`}
                </span>

                {/* GRUPO DE BOTONES DE EXPORTACIÓN */}
                <div className="d-flex gap-2">
                    <button
                        onClick={() => downloadReportFile('/api/customer-loyal-report/export/excel', 'Reporte_Clientes_Fieles.xlsx')}
                        className="btn btn-outline-success btn-sm d-flex align-items-center gap-1 px-2 fw-semibold"
                        style={{ fontSize: '0.8rem' }}
                        disabled={isLoading || dataList.length === 0}
                    >
                        <i className="bi bi-file-earmark-excel-fill"></i> Excel
                    </button>
                    <button
                        onClick={() => downloadReportFile('/api/customer-loyal-report/export/pdf', 'Reporte_Clientes_Fieles.pdf')}
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
                            Error al cargar el reporte: {error.message}
                        </div>
                    ) : dataList.length === 0 && !isLoading ? (
                        <div className="col-12 w-100 d-flex justify-content-center align-items-center py-5" style={{ minHeight: "40vh" }}>
                            <div className="text-center text-muted fs-5">
                                <i className="bi bi-inbox display-5 d-block mb-2" style={{ lineHeight: "1" }}></i>
                                <span>No se encontraron registros</span>
                            </div>
                        </div>
                    ) : (
                        <div className="table-responsive">
                            <table className="table table-hover align-middle mb-0" style={{ fontSize: '0.85rem' }}>
                                <thead className="table-dark text-uppercase" style={{ fontSize: '0.75rem', letterSpacing: '0.5px' }}>
                                <tr>
                                    <th scope="col" className="text-center py-2" style={{ width: '80px' }}>ID</th>
                                    <th scope="col" className="text-start py-2 ps-4">NOMBRE CLIENTE</th>
                                    <th scope="col" className="text-start py-2 ps-4">CORREO ELECTRONICO</th>
                                    <th scope="col" className="text-center py-2">TOTAL COMPRAS</th>
                                    <th scope="col" className="text-center py-2 pe-4">TOTAL GASTADO</th>
                                </tr>
                                </thead>
                                <tbody>
                                {dataList.map((loyal) => (
                                    <tr key={loyal.id}>
                                        <td className="fw-bold text-secondary text-center py-2">{loyal.id}</td>
                                        <td className="text-start fw-semibold text-primary ps-4 py-2">
                                            <i className="me-2"></i>{loyal.customerName}
                                        </td>
                                        <td className="text-start text-dark ps-4 py-2">
                                            {loyal.customerEmail}
                                        </td>
                                        <td className="text-center py-2">
                                            <span className={`badge px-2.5 py-1.5 rounded-pill ${
                                                loyal.totalPurchases === '1 a 10' ? 'bg-success text-white' :
                                                    loyal.totalPurchases === '11 a 20' ? 'bg-warning text-dark' :
                                                        'bg-success text-white'
                                            }`} style={{ fontSize: '0.75rem' }}>
                                                {loyal.totalPurchases}
                                            </span>
                                        </td>
                                        <td className="text-center fw-bold text-success pe-4 py-2">
                                            ${Number(loyal.totalSpent || 0).toLocaleString('es-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
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