import "../styles/forms.css";
import React, { useState, useEffect, useMemo } from "react";
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
                        article = "el"
                    }) {
    const userRole = localStorage.getItem("userRole") || "USUARIO";
    const [activeTab, setActiveTab] = useState(() => localStorage.getItem(`active_${resource}_Tab`) || "all");
    const [showWelcome, setShowWelcome] = useState(activeTab === "home");

    // Filtros de UI locales
    const [search, setSearch] = useState("");
    const [searchId, setSearchId] = useState("");
    const [filteredByName, setFilteredByName] = useState([]);
    const [filteredById, setFilteredById] = useState(null);
    const [searchCategory, setSearchCategory] = useState("");
    const [filteredByCategory, setFilteredByCategory] = useState([]);

    // Estado para el formulario de actualización
    const [formData, setFormData] = useState({});
    const [idToLoad, setIdToLoad] = useState("");

    // Verifica si el usuario actual es un cliente con permisos limitados
    const isGenuineClient = userRole === "CLIENTE" || userRole === "CLIENT" || userRole === "USUARIO";
    const shouldLoadResource = !isGenuineClient || resource === "products" || resource === "productos";

    // Instanciación de TanStack Query
    const entity = useEntity(resource, {
        enabled: shouldLoadResource && activeTab !== "home"
    });

    const dataList = entity.list.data || [];

    // Memorizar la resolución de los fields para evitar 
    const fieldsForCreate = useMemo(() => typeof fields === "function" ? fields(false) : fields, [fields]);
    const fieldsForUpdate = useMemo(() => typeof fields === "function" ? fields(true) : fields, [fields]);

    // Helper para extraer el ID de forma segura
    const getItemId = (item) => {
        if (!item) return null;
        const singularResource = resource.endsWith('s') ? resource.slice(0, -1) : resource;
        return item.id ??
            item[`${resource}_id`] ??
            item[`${singularResource}Id`] ??
            item.productId ??
            item.userId ??
            item.countryId ??
            item.categoryId ??
            item.exitId ??
            item.entryId;
    };

    // Sincronización y escucha de pestañas por eventos globales
    useEffect(() => {
        const handleTabChange = () => {
            const tab = localStorage.getItem(`active_${resource}_Tab`) || "home";
            setActiveTab(tab);
            setShowWelcome(tab === "home");

            setFilteredByName([]);
            setFilteredById(null);
            setSearch("");
            setSearchId("");
            setFormData({});
            setIdToLoad("");
        };

        window.addEventListener(`${resource}TabChanged`, handleTabChange);
        return () => window.removeEventListener(`${resource}TabChanged`, handleTabChange);
    }, [resource]);

    // Seguridad de la matriz de permisos
    useEffect(() => {
        if (activeTab === "create" && !tieneAcceso(userRole, "crear")) setActiveTab("all");
        if (activeTab === "update" && !tieneAcceso(userRole, "actualizar")) setActiveTab("all");
        if (activeTab === "delete" && !tieneAcceso(userRole, "eliminar")) setActiveTab("all");
    }, [activeTab, userRole]);

    // Carga los datos del registro en el formulario para poder editar
    const handleLoadUpdateData = () => {
        const cleanId = String(idToLoad).trim();
        if (!cleanId) return toast.error("Por favor, ingresa un ID válido.");

        const encontrado = dataList.find(item => String(getItemId(item)) === cleanId);

        if (!encontrado) {
            toast.error(`No se encontró ${article === "la" ? "la" : "el"} ${title} con el ID ${cleanId}.`);
            setFormData({});
        } else {
            setFormData({
                ...encontrado,
                id: getItemId(encontrado)
            });
            toast.success("Datos cargados correctamente en el formulario.");
        }
    };

    // Búsqueda por nombre
    const handleSearchByName = (e) => {
        e.preventDefault();
        const cleanSearch = search.trim().toLowerCase();
        if (!cleanSearch) return setFilteredByName([]);

        const filtrados = dataList.filter(item => {
            const valorCampo = item[searchField] || item.name || item.productName || item.userName || "";
            return String(valorCampo).toLowerCase().includes(cleanSearch);
        });

        if (filtrados.length === 0) {
            toast.error(`${article === "la" ? "La" : "El"} ${title} con ese nombre no existe.`);
        }
        setFilteredByName(filtrados);
    };

    // Búsqueda por ID en Caché
    const handleSearchById = (e) => {
        e.preventDefault();
        const cleanId = searchId.trim();
        if (!cleanId) return setFilteredById(null);

        const encontrado = dataList.find(item => String(getItemId(item)) === cleanId);
        if (!encontrado) {
            toast.error(`${article === "la" ? "La" : "El"} ${title} no existe con ese ID.`);
            setFilteredById(null);
        } else {
            setFilteredById(encontrado);
        }
    };

    // Buscar por categoría
    const handleSearchByCategory = async (e) => {
        e.preventDefault();
        const cleanCategory = searchCategory.trim();
        if (!cleanCategory) return setFilteredByCategory([]);

        try {
            const productService = apiService("products");
            const resultado = await productService.getByCategory(cleanCategory);

            if (!resultado || resultado.length === 0) {
                toast.error(`No se encontraron productos en la categoría "${cleanCategory}".`);
                setFilteredByCategory([]);
            } else {
                setFilteredByCategory(resultado);
            }
        } catch (error) {
            toast.error("Error al buscar productos por esa categoría.");
            setFilteredByCategory([]);
        }
    };

    // Crear Registro
    const handleCreateSubmit = async (e) => {
        e.preventDefault();
        const formElements = e.target.elements;
        const payload = {};

        fieldsForCreate.forEach(f => {
            if (formElements[f.name]) {
                let valor = formElements[f.name].value;
                if (f.type === "number") {
                    valor = valor ? parseFloat(valor) : null;
                }
                payload[f.name] = valor !== "" ? valor : null;
            }
        });

        try {
            await entity.create.mutateAsync(payload);
            toast.success(`¡${title.charAt(0).toUpperCase() + title.slice(1)} creado con éxito!`);
            e.target.reset();
        } catch (error) {
            toast.error(`Error al registrar ${article === "la" ? "la" : "el"} ${title}`);
        }
    };

    // Actualizar Registro
    const handleUpdateSubmit = async (e) => {
        e.preventDefault();
        const currentId = formData.id;
        if (!currentId) return toast.error("Por favor, carga un ID válido usando el botón 'Cargar' antes de guardar.");

        try {
            const updatedData = {
                ...formData,
                userName: userLogin || localStorage.getItem("userName") || localStorage.getItem("userEmail") || "Sistema"
            };
            await entity.update.mutateAsync({ id: currentId, data: updatedData });
            toast.success(`¡${title.charAt(0).toUpperCase() + title.slice(1)} actualizado correctamente!`);
            setFormData({});
            setIdToLoad("");
        } catch (error) {
            const mensajeError = error.response?.data?.message || `Error al actualizar ${article === "la" ? "la" : "el"} ${title}`;
            toast.error(mensajeError);
        }
    };

    return (
        <div className={`fb-form-container ${activeTab === "home" && showWelcome ? "fb-no-scroll" : ""}`}>
            {activeTab === "home" && showWelcome && (
                <div className="fb-photo-section">
                    <img src="/logo1.png" alt="Logo FreshBasket" className="fb-photo"/>
                </div>
            )}

            {/* MOSTRAR TODOS */}
            {activeTab === "all" && !showWelcome && (
                <div className="fb-form-section">
                    <div className="fb-section-header">
                        <h3 className="fb-table-title"><i className={`bi ${icon}`}/> Mostrando todos los registros</h3>
                        <span className="fb-badge">
                            {entity.list.isLoading ? "Sincronizando..." : `${dataList.length} registros`}
                        </span>
                    </div>
                    <div className="fb-results-grid fb-users-cards-margin">
                        {dataList.length > 0 ? dataList.map(item => renderCard(item)) : (
                            <div className="fb-empty fb-grid-full-width">
                                <i className="bi bi-inbox"/><p>No hay registros disponibles</p>
                            </div>
                        )}
                    </div>
                </div>
            )}

            {/* BUSCAR POR NOMBRE */}
            {activeTab === "name" && !showWelcome && (
                <div className="fb-form-section">
                    <div className="fb-form-card">
                        <h3 className="fb-form-title"><i className="bi bi-search"/> Buscar por nombre</h3>
                        <form onSubmit={handleSearchByName} className="fb-search-form">
                            <div className="fb-search-input-wrap">
                                <i className="bi bi-fonts fb-search-icon"/>
                                <input type="text" className="fb-search-input" placeholder="Escriba un nombre"
                                       value={search} onChange={(e) => setSearch(e.target.value)}/>
                            </div>
                            <button type="submit" className="fb-search-btn">Buscar</button>
                        </form>
                    </div>
                    {filteredByName.length > 0 && (
                        <div className="fb-results-grid fb-users-cards-margin mt-4">
                            {filteredByName.map(item => renderCard(item))}
                        </div>
                    )}
                </div>
            )}

            {/* BUSCAR POR ID */}
            {activeTab === "id" && !showWelcome && (
                <div className="fb-form-section">
                    <div className="fb-form-card">
                        <h3 className="fb-form-title"><i className="bi bi-search"/> Buscar por ID</h3>
                        <form onSubmit={handleSearchById} className="fb-search-form">
                            <div className="fb-search-input-wrap">
                                <i className="bi bi-hash fb-search-icon"/>
                                <input type="number" className="fb-search-input" placeholder="Ingrese ID"
                                       value={searchId} onChange={(e) => setSearchId(e.target.value)}/>
                            </div>
                            <button type="submit" className="fb-search-btn">Buscar</button>
                        </form>
                    </div>
                    {filteredById && <div className="fb-results-grid fb-users-cards-margin mt-4">{renderCard(filteredById)}</div>}
                </div>
            )}

            {/* BUSCAR POR CATEGORÍA */}
            {activeTab === "category" && resource === "products" && !showWelcome && (
                <div className="fb-form-section">
                    <div className="fb-form-card">
                        <h3 className="fb-form-title"><i className="bi bi-grid-3x3-gap"/> Buscar por categoría</h3>
                        <form onSubmit={handleSearchByCategory} className="fb-search-form">
                            <div className="fb-search-input-wrap">
                                <i className="bi bi-tag fb-search-icon"/>
                                <input
                                    type="text"
                                    className="fb-search-input"
                                    placeholder="Ej: Lácteos, Verduras, Frutas..."
                                    value={searchCategory}
                                    onChange={(e) => setSearchCategory(e.target.value)}
                                />
                            </div>
                            <button type="submit" className="fb-search-btn">Buscar</button>
                        </form>
                    </div>

                    <div className="fb-results-grid fb-users-cards-margin mt-4">
                        {filteredByCategory.length > 0 &&
                            filteredByCategory.map(item => renderCard(item))
                        }
                    </div>
                </div>
            )}

            {/* FORMULARIO CREAR */}
            {activeTab === "create" && (
                <div className="fb-form-section fb-tab-create">
                    <div className="fb-form-card">
                        <h3 className="fb-form-title"><i className="bi bi-plus-circle"/> Registrar {title}</h3>
                        <form onSubmit={handleCreateSubmit} className="fb-crud-form">
                            <div className="fb-crud-grid">
                                {fieldsForCreate.map(f => (
                                    <div key={f.name} className="fb-crud-field">
                                        <label className="fb-crud-label">{f.label}</label>
                                        <div className="fb-crud-input-wrap">
                                            <i className={`bi ${f.icon} fb-crud-input-icon`}/>

                                            {f.type === "select" ? (
                                                <select
                                                    name={f.name}
                                                    className="fb-crud-input"
                                                    required={f.required !== false}
                                                    disabled={f.disabled || entity.create.isPending}
                                                    defaultValue={f.defaultValue ?? ""}
                                                >
                                                    <option value="" disabled>{f.placeholder || "Seleccione una opción"}</option>
                                                    {f.options && f.options.map((opt, idx) => (
                                                        <option key={idx} value={opt.value ?? opt}>{opt.label ?? opt}</option>
                                                    ))}
                                                </select>
                                            ) : (
                                                <input
                                                    type={f.type || "text"}
                                                    name={f.name}
                                                    className="fb-crud-input"
                                                    placeholder={f.placeholder}
                                                    required={f.required !== false}
                                                    disabled={f.disabled || entity.create.isPending}
                                                    readOnly={f.readOnly || f.name === "userName"}
                                                    defaultValue={f.defaultValue ?? ""}
                                                    step={f.step}
                                                    list={f.list}
                                                    autoComplete={f.list ? "off" : "on"}
                                                />
                                            )}

                                            {f.type !== "select" && f.list && f.options && (
                                                <datalist id={f.list}>
                                                    {f.options.map((opt, idx) => <option key={idx} value={opt}/>)}
                                                </datalist>
                                            )}
                                        </div>
                                    </div>
                                ))}
                            </div>
                            <button
                                type="submit"
                                className="fb-action-btn"
                                style={{background: "linear-gradient(135deg, #1a6b3a, #2ecc71)", marginTop: "1.5rem"}}
                                disabled={entity.create?.isPending}
                            >
                                <i className="bi bi-check-circle-fill" style={{marginRight: "0.5rem"}}/>
                                {entity.create?.isPending ? "Registrando..." : `Registrar ${title}`}
                            </button>
                        </form>
                    </div>
                </div>
            )}

            {/* FORM ACTUALIZAR */}
            {activeTab === "update" && !showWelcome && (
                <div className="fb-form-section fb-tab-update">
                    <div className="fb-form-card">
                        <h3 className="fb-form-title"><i className="bi bi-pencil-square"/> Actualizar {title}</h3>
                        <form onSubmit={handleUpdateSubmit} className="fb-crud-form">
                            <div className="fb-crud-grid">
                                <div className="fb-crud-field fb-id-field-full">
                                    <label className="fb-crud-label">ID del {title}</label>
                                    <div className="fb-inline-search-wrapper">
                                        <div className="fb-input-relative-container">
                                            <i className="bi bi-hash fb-crud-input-icon"/>
                                            <input
                                                type="number"
                                                className="fb-crud-input"
                                                placeholder="Ingrese un ID"
                                                value={idToLoad}
                                                onChange={(e) => setIdToLoad(e.target.value)}
                                                required
                                            />
                                        </div>
                                        <button type="button" className="fb-load-inline-btn" onClick={handleLoadUpdateData}>
                                            Cargar
                                        </button>
                                    </div>
                                </div>
                                {fieldsForUpdate.map(f => {
                                    if (f.hideOnUpdate) return null;
                                    const isUserNameField = f.name === "userName";
                                    const currentUser = userLogin || localStorage.getItem("userName") || localStorage.getItem("userEmail") || "Sin usuario";
                                    const inputValue = isUserNameField ? currentUser : (formData[f.name] ?? "");

                                    return (
                                        <div key={f.name} className="fb-crud-field">
                                            <label className="fb-crud-label">{f.label}</label>
                                            <div className="fb-crud-input-wrap">
                                                <i className={`bi ${f.icon} fb-crud-input-icon`}/>

                                                {f.type === "select" ? (
                                                    <select
                                                        name={f.name}
                                                        className="fb-crud-input"
                                                        value={inputValue}
                                                        onChange={(e) => {
                                                            setFormData({...formData, [f.name]: e.target.value});
                                                        }}
                                                        required={f.requiredOnUpdate !== false && f.required !== false}
                                                        disabled={f.disabled || f.disabledOnUpdate || entity.update.isPending}
                                                    >
                                                        <option value="" disabled>{f.placeholder || "Seleccione una opción"}</option>
                                                        {f.options && f.options.map((opt, idx) => (
                                                            <option key={idx} value={opt.value ?? opt}>{opt.label ?? opt}</option>
                                                        ))}
                                                    </select>
                                                ) : (
                                                    <input
                                                        type={f.type || "text"}
                                                        name={f.name}
                                                        className="fb-crud-input"
                                                        placeholder={f.placeholder}
                                                        value={inputValue}
                                                        onChange={(e) => {
                                                            if (!isUserNameField) {
                                                                setFormData({...formData, [f.name]: e.target.value});
                                                            }
                                                        }}
                                                        required={f.requiredOnUpdate !== false && f.required !== false}
                                                        disabled={f.disabled || f.disabledOnUpdate || entity.update.isPending}
                                                        readOnly={f.readOnly || isUserNameField}
                                                        step={f.step}
                                                        list={f.list}
                                                        autoComplete={f.list ? "off" : "on"}
                                                    />
                                                )}

                                                {f.type !== "select" && f.list && f.options && (
                                                    <datalist id={f.list}>
                                                        {f.options.map((opt, idx) => <option key={idx} value={opt}/>)}
                                                    </datalist>
                                                )}
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                            <button
                                type="submit"
                                className="fb-action-btn"
                                style={{background: "linear-gradient(135deg, #1a6b3a, #2ecc71)", marginTop: "1.5rem"}}
                                disabled={entity.update.isPending || !formData.id}
                            >
                                <i className="bi bi-arrow-clockwise" style={{marginRight: "0.5rem"}}/>
                                {entity.update.isPending ? "Actualizando..." : "Guardar Cambios"}
                            </button>
                        </form>
                    </div>
                </div>
            )}

            {/* ELIMINAR */}
            {activeTab === "delete" && !showWelcome && (
                <div className="fb-form-section">
                    <div className="fb-form-card" style={{borderTop: "4px solid #dc3545"}}>
                        <h3 className="fb-form-title" style={{color: "#dc3545", marginBottom: "0.5rem"}}>
                            <i className="bi bi-trash3-fill" style={{marginRight: "0.5rem"}}/> Eliminar {title}
                        </h3>
                        <div className="alert alert-danger d-flex align-items-center gap-2" style={{fontSize: "0.9rem", padding: "0.75rem 1rem", borderRadius: "8px", marginBottom: "1.5rem"}}>
                            <i className="bi bi-exclamation-triangle-fill" style={{fontSize: "1.1rem"}}/>
                            <span>
                                <strong>Atención:</strong> Al eliminar {article === "la" ? "la" : "el"} {title}, se borrará permanentemente de la base de datos.
                            </span>
                        </div>
                        <form onSubmit={async (e) => {
                            e.preventDefault();
                            const idValue = e.target.id.value;
                            const registroAEliminar = dataList.find(i => String(getItemId(i)) === String(idValue));
                            if (!registroAEliminar) return toast.error(`No existe el ${title} con ID ${idValue}`);

                            const apellidoStr = registroAEliminar.lastName || registroAEliminar.last_name || "";
                            const nombreVisual =
                                (registroAEliminar.name ? `${registroAEliminar.name} ${apellidoStr}`.trim() : null) ||
                                registroAEliminar.productName ||
                                registroAEliminar.supplierName ||
                                registroAEliminar.userName ||
                                registroAEliminar.categoryName ||
                                registroAEliminar.countryName ||
                                "Elemento";

                            toast((t) => (
                                <div className="text-center" style={{minWidth: "250px"}}>
                                    <p className="text-dark">
                                        ¿Seguro que deseas eliminar {article === "la" ? "la" : "el"} {title}{" "}
                                        <strong>"{nombreVisual}"</strong> con{" "}
                                        <strong>ID {idValue}</strong> permanentemente?
                                    </p>
                                    <div className="d-flex justify-content-center gap-2 mt-2">
                                        <button className="btn btn-danger btn-sm" disabled={entity.remove.isPending}
                                                onClick={async () => {
                                                    toast.dismiss(t.id);
                                                    try {
                                                        await entity.remove.mutateAsync(idValue);
                                                        toast.success(`${article === "la" ? "La" : "el"} ${title} ha sido eliminado correctamente.`);
                                                        e.target.reset();
                                                    } catch {
                                                        toast.error("Error al eliminar el registro.");
                                                    }
                                                }}>Eliminar
                                        </button>
                                        <button className="btn btn-light btn-sm" onClick={() => toast.dismiss(t.id)}>Cancelar</button>
                                    </div>
                                </div>
                            ), {duration: Infinity, position: "top-center"});
                        }} className="fb-search-form">
                            <div className="fb-search-input-wrap">
                                <i className="bi bi-hash fb-search-icon"/>
                                <input type="number" name="id" className="fb-search-input" placeholder="Ingrese el ID a eliminar" required/>
                            </div>
                            <button type="submit" className="fb-search-btn" style={{background: "linear-gradient(135deg, #a61a1a, #e74c3c)", marginTop: 0}} disabled={entity.remove.isPending}>
                                <i className="bi bi-trash-fill" style={{marginRight: "0.3rem"}}/> Eliminar
                            </button>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

export default FormLayout;
