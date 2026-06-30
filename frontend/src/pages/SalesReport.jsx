import React, { useState } from 'react';
import { useSalesReport } from '../hooks/useSalesReport';
import { downloadReportFile } from '../utils/downloadHelper';

export default function SalesReport() {
    const [filterType, setFilterType] = useState('all');
    // Estado estructurado como objeto para manejar los múltiples filtros del endpoint de ventas
    const [filterValues, setFilterValues] = useState({
        employeeName: '',
        day: '',
        month: '',
        paymentMethod: '',
        startDate: '',
        endDate: ''
    });
    const [mostrarFiltros, setMostrarFiltros] = useState(false);

    // Consumo del Hook usando el tipo de filtro y el objeto de valores
    const { data: sales, isLoading, isError, error } = useSalesReport(filterType, filterValues);
    const dataList = sales || [];

    const handleFilterTypeChange = (e) => {
        setFilterType(e.target.value);
        // Reiniciamos todos los valores al cambiar de tipo de filtro
        setFilterValues({
            employeeName: '',
            day: '',
            month: '',
            paymentMethod: '',
            startDate: '',
            endDate: ''
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
        if (normalized.includes('completada') || normalized.includes('pagado')) return 'bg-success text-white';
        if (normalized.includes('pendiente')) return 'bg-warning text-dark';
        return 'bg-danger text-white';
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
                                    <option value="all">Mostrar todos los registros</option>
                                    <option value="employeename">Buscar por empleado</option>
                                    <option value="specific-date">Filtro por Día / Mes / Pago</option>
                                    <option value="date-range">Filtro por Rango de fechas</option>
                                </select>
                            </div>

                            {/* Campo: Búsqueda por Empleado */}
                            {filterType === 'employeename' && (
                                <div className="col-md-9">
                                    <label className="form-label fw-bold text-muted small">Nombre del empleado:</label>
                                    <div className="position-relative w-100">
                                        <i className="bi bi-search text-muted position-absolute" style={{ left: "12px", top: "50%", transform: "translateY(-50%)", zIndex: "5", fontSize: "0.85rem" }} />
                                        <input
                                            type="text"
                                            className="form-control bg-white text-dark small"
                                            placeholder="Escribe el nombre del empleado..."
                                            value={filterValues.employeeName}
                                            onChange={(e) => handleInputChange('employeeName', e.target.value)}
                                            style={{ paddingLeft: "35px", fontSize: '0.875rem' }}
                                        />
                                    </div>
                                </div>
                            )}

                            {/* Campos: Filtro Dinámico por Día, Mes y Método de Pago */}
                            {filterType === 'specific-date' && (
                                <>
                                    <div className="col-md-2">
                                        <label className="form-label fw-bold text-muted small">Día:</label>
                                        <input
                                            type="number"
                                            min="1"
                                            max="31"
                                            className="form-control bg-white text-dark small"
                                            placeholder="Ej. 15"
                                            value={filterValues.day}
                                            onChange={(e) => handleInputChange('day', e.target.value)}
                                            style={{ fontSize: '0.875rem' }}
                                        />
                                    </div>
                                    <div className="col-md-3">
                                        <label className="form-label fw-bold text-muted small">Mes:</label>
                                        <select
                                            className="form-select bg-white text-dark small"
                                            value={filterValues.month}
                                            onChange={(e) => handleInputChange('month', e.target.value)}
                                            style={{ fontSize: '0.875rem' }}
                                        >
                                            <option value="">-- Selecciona un mes --</option>
                                            <option value="1">Enero</option>
                                            <option value="2">Febrero</option>
                                            <option value="3">Marzo</option>
                                            <option value="4">Abril</option>
                                            <option value="5">Mayo</option>
                                            <option value="6">Junio</option>
                                            <option value="7">Julio</option>
                                            <option value="8">Agosto</option>
                                            <option value="9">Septiembre</option>
                                            <option value="10">Octubre</option>
                                            <option value="11">Noviembre</option>
                                            <option value="12">Diciembre</option>
                                        </select>
                                    </div>
                                    <div className="col-md-4">
                                        <label className="form-label fw-bold text-muted small">Metodo de pago:</label>
                                        <input
                                            type="text"
                                            className="form-control bg-white text-dark small"
                                            placeholder="Ej. Efectivo, Tarjeta..."
                                            value={filterValues.paymentMethod}
                                            onChange={(e) => handleInputChange('paymentMethod', e.target.value)}
                                            style={{ fontSize: '0.875rem' }}
                                        />
                                    </div>
                                </>
                            )}

                            {/* Campos: Filtro por Rango de Fechas (Calendario) */}
                            {filterType === 'date-range' && (
                                <>
                                    <div className="col-md-3">
                                        <label className="form-label fw-bold text-muted small">Fecha inicio:</label>
                                        <input
                                            type="datetime-local"
                                            className="form-control bg-white text-dark small"
                                            value={filterValues.startDate}
                                            onChange={(e) => handleInputChange('startDate', e.target.value)}
                                            style={{ fontSize: '0.875rem' }}
                                        />
                                    </div>
                                    <div className="col-md-3">
                                        <label className="form-label fw-bold text-muted small">Fecha final:</label>
                                        <input
                                            type="datetime-local"
                                            className="form-control bg-white text-dark small"
                                            value={filterValues.endDate}
                                            onChange={(e) => handleInputChange('endDate', e.target.value)}
                                            style={{ fontSize: '0.875rem' }}
                                        />
                                    </div>
                                    <div className="col-md-3">
                                        <label className="form-label fw-bold text-muted small">Metodo de pago (Opcional):</label>
                                        <input
                                            type="text"
                                            className="form-control bg-white text-dark small"
                                            placeholder="Ej. Tarjeta"
                                            value={filterValues.paymentMethod}
                                            onChange={(e) => handleInputChange('paymentMethod', e.target.value)}
                                            style={{ fontSize: '0.875rem' }}
                                        />
                                    </div>
                                </>
                            )}
                        </div>
                    </div>
                </div>
            )}

            {/* CABECERA STICKY UNIFORME */}
            <div className="fb-section-header d-flex justify-content-between align-items-center mb-3 mt-2 p-2 px-3 bg-white rounded shadow-sm"
                 style={{ position: "sticky", top: "0", zIndex: "100" }}>
                <span className="text-secondary fw-bold small" style={{ fontSize: '0.95rem' }}>
                    {isLoading ? "Sincronizando con el servidor..." : `Mostrando ${dataList.length} registros de ventas.`}
                </span>

                {/* GRUPO DE BOTONES DE EXPORTACIÓN */}
                <div className="d-flex gap-2">
                    <button
                        onClick={() => downloadReportFile('/api/sales-report/export/excel', 'Reporte_Ventas.xlsx')}
                        className="btn btn-outline-success btn-sm d-flex align-items-center gap-1 px-2 fw-semibold"
                        style={{ fontSize: '0.8rem' }}
                        disabled={isLoading || dataList.length === 0}
                    >
                        <i className="bi bi-file-earmark-excel-fill"></i> Excel
                    </button>
                    <button
                        onClick={() => downloadReportFile('/api/sales-report/export/pdf', 'Reporte_Ventas.pdf')}
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
                                <span>No se encontraron registros de ventas</span>
                            </div>
                        </div>
                    ) : (
                        <div className="table-responsive">
                            <table className="table table-hover align-middle mb-0" style={{ fontSize: '0.85rem' }}>
                                <thead className="table-dark text-uppercase" style={{ fontSize: '0.75rem', letterSpacing: '0.5px' }}>
                                <tr>
                                    <th scope="col" className="text-center py-2" style={{ width: '80px' }}>ID</th>
                                    <th scope="col" className="text-start py-2 ps-3">FECHA VENTA</th>
                                    <th scope="col" className="text-center py-2">MONTO TOTAL</th>
                                    <th scope="col" className="text-center py-2">METODO PAGO</th>
                                    <th scope="col" className="text-center py-2">ESTADO</th>
                                    <th scope="col" className="text-start py-2 ps-3">EMPLEADO</th>
                                </tr>
                                </thead>
                                <tbody>
                                {dataList.map((sale) => (
                                    <tr key={sale.id}>
                                        <td className="fw-bold text-secondary text-center py-2">{sale.id}</td>
                                        <td className="text-start text-dark ps-3 py-2">
                                            {sale.saleDate ? new Date(sale.saleDate).toLocaleString('es-US', {
                                                year: 'numeric', month: '2-digit', day: '2-digit',
                                                hour: '2-digit', minute: '2-digit'
                                            }) : '---'}
                                        </td>
                                        <td className="text-center fw-bold text-success py-2">
                                            ${Number(sale.totalAmount || 0).toLocaleString('es-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                                        </td>
                                        <td className="text-center fw-semibold text-secondary py-2">
                                            {sale.paymentMethod}
                                        </td>
                                        <td className="text-center py-2">
                                            <span className={`badge px-2.5 py-1.5 rounded-pill ${getStatusBadgeColor(sale.status)}`} style={{ fontSize: '0.75rem' }}>
                                                {sale.status || 'Completada'}
                                            </span>
                                        </td>
                                        <td className="text-start py-2 ps-3">
                                            <div className="fw-semibold text-primary">{sale.employeeName}</div>
                                            <div className="text-muted" style={{ fontSize: '0.75rem' }}>{sale.employeeEmail}</div>
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