import "../styles/forms.css";
import React, { useState, useMemo } from "react";
import toast from "react-hot-toast";
import { apiService } from "../services/apiService.js";
import { useEntity } from "../hooks/useEntity.js";
import { tieneAcceso } from "../Config/permissions.js";

function FormLayout({
                        resource,
                        title,
                        icon,
                        fields,
                        searchField,
                        renderCard,
                        userLogin,
                        article = "el",
                        forcedMode,
                        onBeforeSave,
                        onSuccessHook
                    }) {
    const userRole = localStorage.getItem("userRole") || "USUARIO";

    const esClienteRol = userRole?.toUpperCase().trim() === "CLIENTE" || userRole?.toUpperCase().trim() === "CLIENT";

    const puedeConsultar = esClienteRol
        ? (resource === "products" || resource === "productos")
        : (resource === "users" || resource === "usuarios"
            ? tieneAcceso(userRole, "verModuloUsuarios")
            : tieneAcceso(userRole, "verTabsConsulta"));

    const puedeCrear     = tieneAcceso(userRole, "crear");
    const puedeActualizar = tieneAcceso(userRole, "actualizar");
    const puedeEliminar   = tieneAcceso(userRole, "eliminar");

    const [filtroTipo, setFiltroTipo] = useState("all");
    const [busqueda, setBusqueda] = useState("");

    const [modalOpen, setModalOpen] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({});

    const isGenuineClient = userRole === "CLIENTE" || userRole === "CLIENT" || userRole === "USUARIO";
    const shouldLoadResource = !isGenuineClient || resource === "products" || resource === "productos";

    const entity = useEntity(resource, { enabled: shouldLoadResource });
    const dataList = entity.list.data || [];

    const fieldsForCreate = useMemo(() => typeof fields === "function" ? fields(false) : fields, [fields]);
    const fieldsForUpdate = useMemo(() => typeof fields === "function" ? fields(true) : fields, [fields]);

    const [mostrarFiltros, setMostrarFiltros] = useState(false);

    const getItemId = (item) => {
        if (!item) return null;
        const singularResource = resource.endsWith('s') ? resource.slice(0, -1) : resource;
        return item.id ?? item[`${resource}_id`] ?? item[`${singularResource}Id`] ?? item.productId ?? item.userId ?? item.countryId ?? item.categoryId ?? item.exitId ?? item.entryId;
    };

    const registrosFiltrados = useMemo(() => {
        const cleanQuery = busqueda.trim().toLowerCase();
        if (!cleanQuery || filtroTipo === "all") return dataList;

        if (filtroTipo === "id") {
            return dataList.filter(item => String(getItemId(item)) === cleanQuery);
        }
        if (filtroTipo === "name") {
            return dataList.filter(item => {
                const valorCampo = item[searchField] || item.name || item.productName || item.userName || "";
                return String(valorCampo).toLowerCase().includes(cleanQuery);
            });
        }
        if (filtroTipo === "category") {
            return dataList.filter(item => {
                const valorCat = item.category || item.categoryName || item.categoria || "";
                return String(valorCat).toLowerCase().includes(cleanQuery);
            });
        }
        return dataList;
    }, [dataList, filtroTipo, busqueda, searchField]);

    const openCreateModal = () => {
        setIsEditing(false);
        setFormData({});
        setModalOpen(true);
    };

    const openEditModal = (item) => {
        setIsEditing(true);
        setFormData({ ...item, id: getItemId(item) });
        setModalOpen(true);
    };

    const handleFormSubmit = async (e) => {
        e.preventDefault();
        let datosAEnviar = { ...formData };
        if (typeof onBeforeSave === "function") {
            datosAEnviar = onBeforeSave(datosAEnviar, isEditing ? "update" : "create");
        }

        if (isEditing) {
            try {
                const { id, ...dataWithoutId } = datosAEnviar;
                const updatedData = {
                    ...dataWithoutId,
                    userName: userLogin || localStorage.getItem("userName") || localStorage.getItem("userEmail") || "Sistema"
                };

                await entity.update.mutateAsync({ id: formData.id, data: updatedData });
                toast.success(`¡${article === "la" ? "La" : "El"} ${title} se ha actualizado correctamente!`);
                setModalOpen(false);
                if (typeof onSuccessHook === "function") onSuccessHook();
            } catch (error) {
                toast.error(error.response?.data?.message || `Error al actualizar ${article === "la" ? "la" : "el"} ${title}`);
            }
        } else {
            const formElements = e.target.elements;
            const payload = {};
            fieldsForCreate.forEach(f => {
                if (formElements[f.name]) {
                    let valor = formElements[f.name].value;
                    if (f.type === "number") valor = valor ? parseFloat(valor) : null;
                    payload[f.name] = valor !== "" ? valor : null;
                }
            });

            const finalPayload = typeof onBeforeSave === "function" ? onBeforeSave(payload, "create") : payload;

            try {
                await entity.create.mutateAsync(finalPayload);
                toast.success(`¡${article === "la" ? "La" : "El"} ${title} se ha creado con éxito!`);
                setModalOpen(false);
                if (typeof onSuccessHook === "function") onSuccessHook();
            } catch {
                toast.error(`Error al registrar ${article === "la" ? "la" : "el"} ${title}`);
            }
        }
    };

    const handleDeleteClick = (item) => {
        const idValue = getItemId(item);
        let nombreVisual = "";

        const modulo = title.toLowerCase();

        if (modulo.includes("producto")) {
            nombreVisual = item.productName || item.product?.name || item.producto?.name || item.name;
        } else if (modulo.includes("proveedor")) {
            const nombreProv = item.supplierName || item.supplier?.name || item.proveedor?.name || item.name || "";
            const apellidoProv = item.lastName || item.last_name || item.supplier?.lastName || "";

            nombreVisual = `${nombreProv} ${apellidoProv}`.trim();
        } else if (modulo.includes("salida") || modulo.includes("entrada") || modulo.includes("inventario")) {
            nombreVisual = item.product?.name || item.producto?.name || item.productName || item.name;
        }

        if (!nombreVisual && (item.name || item.userName || item.lastName || item.last_name)) {
            const nombreUsuario = item.name || item.userName || "";
            const apellidoUsuario = item.lastName || item.last_name || "";
            nombreVisual = `${nombreUsuario} ${apellidoUsuario}`.trim();
        }

        if (!nombreVisual) {
            nombreVisual = "Este registro";
        }

        toast((t) => (
            <div className="text-center" style={{ minWidth: "250px" }}>
                <p className="text-dark m-0 mb-2">
                    ¿Seguro que deseas eliminar permanentemente {article} {title.toLowerCase()} <strong>"{nombreVisual}"</strong> con ID: {idValue}?
                </p>
                <div className="d-flex justify-content-center gap-2">
                    <button
                        className="btn btn-danger btn-sm"
                        onClick={async () => {
                            toast.dismiss(t.id);
                            try {
                                await entity.remove.mutateAsync(idValue);
                                toast.success(`Registro eliminado correctamente.`);
                            } catch {
                                toast.error(`Error al actualizar ${article === "la" ? "la" : "el"} ${title.toLowerCase()}`);
                            }
                        }}
                    >
                        Eliminar
                    </button>
                    <button className="btn btn-light btn-sm" onClick={() => toast.dismiss(t.id)}>
                        Cancelar
                    </button>
                </div>
            </div>
        ), { duration: Infinity, position: "top-center" });
    };

    return (
        <div className="container-fluid px-3 pb-3 pt-0 fb-form-container position-relative">

            {/* 1. BOTÓN HAMBURGUESA FLOTANTE FIJO */}
            {puedeConsultar && (
                <button
                    className="btn btn-primary rounded-circle shadow position-fixed d-flex align-items-center justify-content-center"
                    style={{
                        bottom: "25px",
                        right: "25px",
                        width: "55px",
                        height: "55px",
                        zIndex: "990",
                        fontSize: "1.5rem"
                    }}
                    onClick={() => setMostrarFiltros(!mostrarFiltros)}
                    title={mostrarFiltros ? "Cerrar filtros" : "Abrir filtros"}
                    type="button"
                >
                    <i className={`bi ${mostrarFiltros ? "bi-x-lg" : "bi-sliders"}`}></i>
                </button>
            )}

            {/* 2. PANEL DE FILTROS DESPLEGABLE */}
            {puedeConsultar && mostrarFiltros && (
                <div
                    className="position-sticky bg-transparent pb-2 mb-3 w-100"
                    style={{
                        top: "-24px",

                        paddingTop: "4px"
                    }}
                >
                    <div className="card p-3 mb-0 shadow border-0 bg-white">
                        <div className="row g-3 align-items-end p-2 bg-light rounded shadow-inner">

                            {/* Selector de Filtro */}
                            <div className="col-md-4">
                                <label className="form-label fw-bold text-muted small">Filtrar por:</label>
                                <select
                                    className="form-select bg-white text-dark"
                                    value={filtroTipo}
                                    onChange={(e) => { setFiltroTipo(e.target.value); setBusqueda(""); }}
                                >
                                    <option value="all">Mostrar todos los registros</option>
                                    <option value="name">Buscar por nombre</option>
                                    <option value="id">Buscar por ID</option>
                                    {resource === "products" && <option value="category">Buscar por categoría</option>}
                                </select>
                            </div>

                            {/* Input de Búsqueda Dinámico */}
                            {filtroTipo !== "all" && (
                                <div className="col-md-8">
                                    <label className="form-label fw-bold text-muted small">Valor de búsqueda:</label>
                                    <div className="position-relative w-100">
                                        <i
                                            className="bi bi-search text-muted position-absolute"
                                            style={{ left: "12px", top: "50%", transform: "translateY(-50%)", zIndex: "5", fontSize: "0.95rem" }}
                                        />
                                        <input
                                            type={filtroTipo === "id" ? "number" : "text"}
                                            className="form-control bg-white text-dark"
                                            placeholder={`Escribe para buscar por ${filtroTipo}...`}
                                            value={busqueda}
                                            onChange={(e) => setBusqueda(e.target.value)}
                                            style={{ paddingLeft: "35px" }}
                                        />
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            )}
            <div
                className="fb-section-header d-flex justify-content-between align-items-center mb-3 mt-2 p-3 bg-white rounded shadow-sm"
                style={{ position: "sticky", top: "0", zIndex: "100",
                }}
            >
            <span className="text-muted fw-semibold">
             {entity.list.isLoading ? "Sincronizando con el servidor..." : `Mostrando ${registrosFiltrados.length} registros`}
              </span>
                {puedeCrear && (
                    <button
                        className="btn btn-success btn-sm d-flex align-items-center gap-2 px-3 py-2 shadow-sm"
                        style={{ fontSize: "0.85rem", height: "38px" }}
                        onClick={openCreateModal}
                        type="button"
                    >
                        <i className="bi bi-plus-circle-fill"></i> Registrar {title}
                    </button>
                )}
            </div>

            {/* 4. GRID DE TARJETAS */}
            <div className="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-xxl-4 g-4">
                {registrosFiltrados.length > 0 ? (
                    registrosFiltrados.map(item => (
                        <div className="col" key={getItemId(item)}>
                            <div className="card h-100 shadow-sm hover-card border-0 position-relative">
                                <div className="card-body p-3">
                                    {renderCard(item)}
                                </div>
                                <div className="card-footer bg-light border-0 d-flex justify-content-center gap-1 py-2">
                                    {puedeActualizar && (
                                        <button className="btn btn-sm btn-outline-success d-flex align-items-center gap-1" onClick={() => openEditModal(item)} title="Editar">
                                            <i className="bi bi-pencil-square"></i> Editar
                                        </button>
                                    )}
                                    {puedeEliminar && (
                                        <button className="btn btn-sm btn-outline-danger d-flex align-items-center gap-1" onClick={() => handleDeleteClick(item)} title="Eliminar">
                                            <i className="bi bi-trash3-fill"></i> Eliminar
                                        </button>
                                    )}
                                </div>
                            </div>
                        </div>
                    ))
                ) : (
                    <div className="col-12 w-100 d-flex justify-content-center align-items-center py-5" style={{ minHeight: "50vh" }}>
                        <div className="text-center text-muted fs-4">
                            <i className="bi bi-inbox display-4 d-block mb-2" style={{ lineHeight: "1" }}></i>
                            <span>No se encontraron registros</span>
                        </div>
                    </div>
                )}
            </div>

            {/* 5. MODAL DE CREACIÓN / EDICIÓN */}
            {modalOpen && (
                <>
                    <div className="modal-backdrop fade show" style={{ zIndex: 1040 }}></div>
                    <div className="modal d-block" tabIndex="-1" style={{ zIndex: 1050 }}>
                        <div className="modal-dialog modal-dialog-centered modal-lg">
                            <div className="modal-content shadow-lg border-0 bg-white">
                                <div className="modal-header bg-dark text-white p-3">
                                    <h5 className="modal-title d-flex align-items-center gap-2">
                                        <i className={`bi ${isEditing ? "bi-pencil-square" : "bi-plus-circle"}`}></i>
                                        {isEditing ? `Actualizar ${title}` : `Registrar ${title}`}
                                    </h5>
                                    <button type="button" className="btn-close btn-close-white" onClick={() => setModalOpen(false)} aria-label="Close"></button>
                                </div>
                                <form onSubmit={handleFormSubmit}>
                                    <div className="modal-body p-4 bg-white text-dark">
                                        <div className="row g-3">
                                            {(isEditing ? fieldsForUpdate : fieldsForCreate).map(f => {
                                                if (isEditing && f.hideOnUpdate) return null;
                                                const isUserNameField = f.name === "userName";
                                                const currentUser = userLogin || localStorage.getItem("userName") || localStorage.getItem("userEmail") || "Sistema";
                                                const inputValue = isEditing
                                                    ? (isUserNameField ? currentUser : (formData[f.name] ?? ""))
                                                    : undefined;
                                                return (
                                                    <div key={f.name} className={f.hideOnUpdate && isEditing ? "" : "col-md-6"}>
                                                        <label className="form-label fw-semibold small text-secondary">{f.label}</label>
                                                        <div className="input-group">
                                                            <span className="input-group-text bg-light"><i className={`bi ${f.icon} text-muted`}></i></span>
                                                            {f.type === "select" ? (
                                                                <select
                                                                    name={f.name}
                                                                    className="form-select bg-white text-dark"
                                                                    required={f.required !== false}
                                                                    disabled={f.disabled || (isEditing && f.disabledOnUpdate) || entity.create.isPending}
                                                                    value={inputValue}
                                                                    defaultValue={!isEditing ? (f.defaultValue ?? "") : undefined}
                                                                    onChange={(e) => isEditing && setFormData({...formData, [f.name]: e.target.value})}
                                                                >
                                                                    <option value="" disabled>{f.placeholder || "Seleccione una opción"}</option>
                                                                    {f.options && f.options.map((opt, idx) => (
                                                                        <option key={idx} value={opt.value ?? opt}>{opt.label ?? opt}</option>
                                                                    ))}
                                                                </select>
                                                            ) : (
                                                                <>
                                                                    <input
                                                                        type={f.type || "text"}
                                                                        name={f.name}
                                                                        className="form-control bg-white text-dark"
                                                                        placeholder={f.placeholder}
                                                                        required={f.required !== false}
                                                                        disabled={f.disabled || (isEditing && f.disabledOnUpdate) || entity.create.isPending}
                                                                        readOnly={f.readOnly || isUserNameField}
                                                                        value={inputValue}
                                                                        defaultValue={!isEditing ? (f.defaultValue ?? "") : undefined}
                                                                        onChange={(e) => isEditing && !isUserNameField && setFormData({...formData, [f.name]: e.target.value})}
                                                                        step={f.step}
                                                                        list={f.list}
                                                                    />
                                                                    {f.list && f.options && (
                                                                        <datalist id={f.list}>
                                                                            {f.options.map((opt, idx) => {
                                                                                const val = opt?.value ?? opt;
                                                                                const lbl = opt?.label ?? opt;
                                                                                return (
                                                                                    <option key={idx} value={val}>
                                                                                        {lbl}
                                                                                    </option>
                                                                                );
                                                                            })}
                                                                        </datalist>
                                                                    )}
                                                                </>
                                                            )}
                                                        </div>
                                                    </div>
                                                );
                                            })}
                                        </div>
                                    </div>
                                    <div className="modal-footer bg-light p-3 border-0">
                                        <button type="button" className="btn btn-secondary px-4" onClick={() => setModalOpen(false)}>Cancelar</button>
                                        <button type="submit" className="btn btn-success px-4" disabled={entity.create.isPending || entity.update.isPending}>
                                            <i className="bi bi-cloud-arrow-up-fill me-1"></i>
                                            {isEditing ? "Registrar cambios" : "Registrar"}
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </>
            )}
        </div>
    );
}

export default FormLayout;