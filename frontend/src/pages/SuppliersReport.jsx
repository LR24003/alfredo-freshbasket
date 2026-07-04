import React, { useState, useEffect } from "react";
import { useSuppliersReport } from "../hooks/useSuppliersReport";
import { downloadReportFile } from "../utils/downloadHelper";

export default function SuppliersReport() {
    const [filterType, setFilterType] = useState('all');
    const [filterValues, setFilterValues] = useState({
        id: '',
        supplierName: '',
        country: '',
        totalProducts: ''
    });

    const [searchName, setSearchName] = useState('');
    const [searchCountry, setSearchCountry] = useState('');
    const [mostrarFiltros, setMostrarFiltros] = useState(false);

    // Petición al Hook de TanStack Query
    const { data: suppliers, isLoading } = useSuppliersReport(filterType, filterValues);
    const dataList = suppliers || [];

    // 1. Debounce para Nombre del Proveedor
    useEffect(() => {
        if (filterType === 'suppliername') {
            const delayDebounce = setTimeout(() => {
                setFilterValues(prev => ({ ...prev, supplierName: searchName }));
            }, 450);
            return () => clearTimeout(delayDebounce);
        }
    }, [searchName, filterType]);

    // 2. Debounce para País de Origen
    useEffect(() => {
        if (filterType === 'country') {
            const delayDebounce = setTimeout(() => {
                setFilterValues(prev => ({ ...prev, country: searchCountry }));
            }, 450);
            return () => clearTimeout(delayDebounce);
        }
    }, [searchCountry, filterType]);

    const handleFilterTypeChange = (e) => {
        setFilterType(e.target.value);
        setSearchName('');
        setSearchCountry('');
        setFilterValues({
            id: '',
            supplierName: '',
            country: '',
            totalProducts: ''
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
                                    <option value="suppliername">Filtrar por nombre proveedor</option> {/* CORREGIDO: productname -> suppliername */}
                                    <option value="country">Filtrar por país de origen</option> {/* CORREGIDO: reason -> country */}
                                    <option value="totalproductos">Filtrar por productos en catálogo</option> {/* CORREGIDO: units -> totalproductos */}
                                </select>
                            </div>

                            {/* Búsqueda por Id */}
                            {filterType === 'id' && (
                                <div className="col-md-8">
                                    <label className="form-label fw-bold text-muted small">Id del proveedor:</label>
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

                            {/* Búsqueda por nombre del proveedor */}
                            {filterType === 'suppliername' && (
                                <div className="col-md-8">
                                    <label className="form-label fw-bold text-muted small">Nombre del proveedor:</label>
                                    <div className="position-relative w-100">
                                        <i className="bi bi-search text-muted position-absolute" style={{ left: "12px", top: "50%", transform: "translateY(-50%)", zIndex: "5", fontSize: "0.85rem" }} />
                                        <input
                                            type="text"
                                            className="form-control bg-white text-dark small"
                                            placeholder="Escribe el nombre del proveedor..."
                                            value={searchName}
                                            onChange={(e) => setSearchName(e.target.value)}
                                            style={{ paddingLeft: "35px", fontSize: '0.875rem' }}
                                        />
                                    </div>
                                </div>
                            )}

                            {/* Búsqueda por país de origen */}
                            {filterType === 'country' && (
                                <div className="col-md-8">
                                    <label className="form-label fw-bold text-muted small">País de origen:</label>
                                    <div className="position-relative w-100">
                                        <i className="bi bi-search text-muted position-absolute" style={{ left: "12px", top: "50%", transform: "translateY(-50%)", zIndex: "5", fontSize: "0.85rem" }} />
                                        <input
                                            type="text"
                                            className="form-control bg-white text-dark small"
                                            placeholder="Ej: El Salvador, México, España..."
                                            value={searchCountry}
                                            onChange={(e) => setSearchCountry(e.target.value)}
                                            style={{ paddingLeft: "35px", fontSize: '0.875rem' }}
                                        />
                                    </div>
                                </div>
                            )}

                            {/* Búsqueda por rango de total de productos en catálogo */}
                            {filterType === 'totalproductos' && (
                                <div className="col-md-8">
                                    <label className="form-label fw-bold text-muted small">Selecciona rango de productos:</label>
                                    <select
                                        className="form-select bg-white text-dark small"
                                        value={filterValues.totalProducts}
                                        onChange={(e) => handleInputChange('totalProducts', e.target.value)}
                                        style={{ fontSize: '0.875rem' }}
                                    >
                                        <option value="">-- Selecciona un rango --</option>
                                        <option value="1 a 5">1 a 5 productos</option>
                                        <option value="6 a 10">6 a 10 productos</option>
                                        <option value="11+">Más de 10 productos</option>
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
                    {isLoading ? "Sincronizando con el servidor..." : `Mostrando ${dataList.length} registros de proveedores.`}
                </span>

                {/* GRUPO DE BOTONES DE EXPORTACIÓN */}
                <div className="d-flex gap-2">
                    <button
                        onClick={() => downloadReportFile('/api/suppliers-report/export/excel', 'Reporte_proveedores.xlsx')}
                        className="btn btn-outline-success btn-sm d-flex align-items-center gap-1 px-2 fw-semibold"
                        style={{ fontSize: '0.8rem' }}
                        disabled={isLoading || dataList.length === 0}
                    >
                        <i className="bi bi-file-earmark-excel-fill"></i> Excel
                    </button>
                    <button
                        onClick={() => downloadReportFile('/api/suppliers-report/export/pdf', 'Reporte_proveedores.pdf')}
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
                                    <th scope="col" className="text-start py-2 ps-3">NOMBRE PROVEEDOR</th>
                                    <th scope="col" className="text-center py-2">PAÍS</th>
                                    <th scope="col" className="text-start py-2">PRODUCTO PRINCIPAL</th>
                                    <th scope="col" className="text-center py-2">VOLUMEN PROD.</th>
                                    <th scope="col" className="text-center py-2">PROD. CATÁLOGO</th>
                                    <th scope="col" className="text-center py-2">STOCK TOTAL</th>
                                    <th scope="col" className="text-end py-2 pe-4">VALOR TOTAL</th>
                                </tr>
                                </thead>
                                <tbody>
                                {dataList.map((supp) => (
                                    <tr key={supp.id}>
                                        <td className="fw-bold text-secondary text-center py-2">{supp.id}</td>

                                        {/* NOMBRE DEL PROVEEDOR */}
                                        <td className="text-start py-2 ps-3">
                                            <div className="fw-semibold text-primary">{supp.provider_name || supp.supplierName}</div>
                                        </td>

                                        {/* PAÍS */}
                                        <td className="text-center fw-semibold text-secondary py-2">
                                            <span className="text-dark badge bg-light border px-2 py-1">
                                                {supp.country}
                                            </span>
                                        </td>

                                        {/* PRODUCTO PRINCIPAL */}
                                        <td className="text-start fw-semibold text-dark py-2">
                                            {supp.main_product || supp.mainProduct}
                                        </td>

                                        {/* VOLUMEN DEL PRODUCTO PRINCIPAL */}
                                        <td className="text-center fw-semibold text-secondary py-2">
                                            {supp.supplied_volume || supp.suppliedVolume} u.
                                        </td>

                                        {/* TOTAL PRODUCTOS DISTINTOS EN CATÁLOGO */}
                                        <td className="text-center fw-semibold text-secondary py-2">
                                            <span className="badge bg-secondary-subtle text-secondary border px-2 py-1">
                                                {supp.total_products_catalog || supp.totalProducts} art.
                                            </span>
                                        </td>

                                        {/* SUMA DE TODO EL STOCK FÍSICO */}
                                        <td className="text-center fw-semibold text-secondary py-2">
                                            {supp.total_units_stock || supp.totalStock} u.
                                        </td>

                                        {/* VALOR MONETARIO TOTAL */}
                                        <td className="text-end fw-bold text-success pe-4 py-2">
                                            ${Number(supp.total_purchased || supp.totalPurchased || 0).toLocaleString('es-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
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