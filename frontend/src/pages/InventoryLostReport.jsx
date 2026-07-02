import React, { useState, useEffect } from 'react';
import { useInventoryLost } from "../hooks/useInventoryLost";
import { downloadReportFile } from '../utils/downloadHelper';

export default function InventoryLostReport() {
    const [filterType, setFilterType] = useState('all');
    const [filterValues, setFilterValues] = useState({
        id: '',
        productName: '',
        exitReason: '',
        unitsLost: ''
    });

    const [searchName, setSearchName] = useState('');
    const [searchReason, setSearchReason] = useState('');
    const [mostrarFiltros, setMostrarFiltros] = useState(false);

    // Petición al Hook
    const { data: products, isLoading } = useInventoryLost(filterType, filterValues);
    const dataList = products || [];

    useEffect(() => {
        if (filterType === 'productname') {
            const delayDebounce = setTimeout(() => {
                setFilterValues(prev => ({ ...prev, productName: searchName }));
            }, 450);
            return () => clearTimeout(delayDebounce);
        }
    }, [searchName, filterType]);

    useEffect(() => {
        if (filterType === 'reason') {
            const delayDebounce = setTimeout(() => {
                setFilterValues(prev => ({ ...prev, exitReason: searchReason }));
            }, 450);
            return () => clearTimeout(delayDebounce);
        }
    }, [searchReason, filterType]);

    const handleFilterTypeChange = (e) => {
        setFilterType(e.target.value);
        setSearchName('');
        setSearchReason('');
        setFilterValues({
            id: '',
            productName: '',
            exitReason: '',
            unitsLost: ''
        });
    };

    const handleInputChange = (field, value) => {
        setFilterValues(prev => ({
            ...prev,
            [field]: value
        }));
    };

    return (
        <div className="container-fluid px-3 pb-3 pt-0 fb-form-container position-relative">
            <button
                className="btn btn-primary rounded-circle shadow position-fixed d-flex align-items-center justify-content-center"
                style={{ bottom: "25px", right: "25px", width: "55px", height: "55px", zIndex: "990", fontSize: "1.5rem" }}
                onClick={() => setMostrarFiltros(!mostrarFiltros)}
                title={mostrarFiltros ? "Cerrar filtros" : "Abrir filtros"}
                type="button"
            >
                <i className={`bi ${mostrarFiltros ? "bi-x-lg" : "bi-sliders"}`}></i>
            </button>

            {/* SECCIÓN DE FILTROS */}
            {mostrarFiltros && (
                <div className="position-sticky bg-transparent pb-2 mb-3 w-100" style={{ top: "-24px", paddingTop: "4px", zIndex: "105" }}>
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
                                    <option value="id">Filtrar por Id</option>
                                    <option value="productname">Filtrar por nombre</option>
                                    <option value="reason">Filtrar por razón de salida</option>
                                    <option value="units">Filtrar por unidades perdidas</option>
                                </select>
                            </div>

                            {/* Búsqueda por Id */}
                            {filterType === 'id' && (
                                <div className="col-md-8">
                                    <label className="form-label fw-bold text-muted small">Id del producto:</label>
                                    <input
                                        type="number"
                                        className="form-control bg-white text-dark small"
                                        placeholder="Ej. 1"
                                        value={filterValues.id}
                                        onChange={(e) => handleInputChange('id', e.target.value)}
                                        style={{ fontSize: '0.875rem' }}
                                    />
                                </div>
                            )}

                            {/* Búsqueda por nombre del producto */}
                            {filterType === 'productname' && (
                                <div className="col-md-8">
                                    <label className="form-label fw-bold text-muted small">Nombre del producto:</label>
                                    <div className="position-relative w-100">
                                        <i className="bi bi-search text-muted position-absolute" style={{ left: "12px", top: "50%", transform: "translateY(-50%)", zIndex: "5", fontSize: "0.85rem" }} />
                                        <input
                                            type="text"
                                            className="form-control bg-white text-dark small"
                                            placeholder="Escribe el nombre a buscar..."
                                            value={searchName}
                                            onChange={(e) => setSearchName(e.target.value)}
                                            style={{ paddingLeft: "35px", fontSize: '0.875rem' }}
                                        />
                                    </div>
                                </div>
                            )}

                            {/* Búsqueda por razón de salida */}
                            {filterType === 'reason' && (
                                <div className="col-md-8">
                                    <label className="form-label fw-bold text-muted small">Razón de la salida:</label>
                                    <div className="position-relative w-100">
                                        <i className="bi bi-search text-muted position-absolute" style={{ left: "12px", top: "50%", transform: "translateY(-50%)", zIndex: "5", fontSize: "0.85rem" }} />
                                        <input
                                            type="text"
                                            className="form-control bg-white text-dark small"
                                            placeholder="Ej. Merma, Daño, Caducidad"
                                            value={searchReason}
                                            onChange={(e) => setSearchReason(e.target.value)}
                                            style={{ paddingLeft: "35px", fontSize: '0.875rem' }}
                                        />
                                    </div>
                                </div>
                            )}

                            {/* Búsqueda por rango de unidades perdidas */}
                            {filterType === 'units' && (
                                <div className="col-md-8">
                                    <label className="form-label fw-bold text-muted small">Selecciona rango de unidades:</label>
                                    <select
                                        className="form-select bg-white text-dark small"
                                        value={filterValues.unitsLost}
                                        onChange={(e) => handleInputChange('unitsLost', e.target.value)}
                                        style={{ fontSize: '0.875rem' }}
                                    >
                                        <option value="">-- Selecciona un rango --</option>
                                        <option value="1 a 15">1 a 15 unidades</option>
                                        <option value="16 a 25">16 a 25 unidades</option>
                                        <option value="26+">Más de 25 unidades</option>
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
                    {isLoading ? "Sincronizando con el servidor..." : `Mostrando ${dataList.length} registros de productos.`}
                </span>

                {/* GRUPO DE BOTONES DE EXPORTACIÓN */}
                <div className="d-flex gap-2">
                    <button
                        onClick={() => downloadReportFile('/api/inventory-lost-report/export/excel', 'Reporte_inventario_perdido.xlsx')}
                        className="btn btn-outline-success btn-sm d-flex align-items-center gap-1 px-2 fw-semibold"
                        style={{ fontSize: '0.8rem' }}
                        disabled={isLoading || dataList.length === 0}
                    >
                        <i className="bi bi-file-earmark-excel-fill"></i> Excel
                    </button>
                    <button
                        onClick={() => downloadReportFile('/api/inventory-lost-report/export/pdf', 'Reporte_inventario_perdido.pdf')}
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
                    {dataList.length === 0 && !isLoading ? (
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
                                    <th scope="col" className="text-start py-2 ps-3">NOMBRE PRODUCTO</th>
                                    <th scope="col" className="text-center py-2">UNIDADES PERDIDAS</th>
                                    <th scope="col" className="text-center py-2">RAZON SALIDA</th>
                                    <th scope="col" className="text-center py-2 pe-3">MONTO PERDIDA($)</th>
                                </tr>
                                </thead>
                                <tbody>
                                {dataList.map((inv) => (
                                    <tr key={inv.id}>
                                        <td className="fw-bold text-secondary text-center py-2">{inv.id}</td>
                                        <td className="text-start py-2 ps-3">
                                            <div className="fw-semibold text-primary">{inv.productName}</div>
                                        </td>
                                        <td className="text-center fw-semibold text-secondary py-2">
                                            <span className="text-dark px-2 py-1">
                                                {inv.unitsLost} u.
                                            </span>
                                        </td>
                                        <td className="text-center fw-semibold text-secondary py-2">
                                            <span className="text-dark px-2 py-1">
                                                {inv.exitReason}
                                            </span>
                                        </td>
                                        <td className="text-center fw-bold text-success pe-3 py-2">
                                            ${Number(inv.totalLost || 0).toLocaleString('es-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
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