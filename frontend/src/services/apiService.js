
import axios from "./axiosConfig.js";

// CRUD Genérico (soporta de forma nativa las 7 entidades)
export const apiService = (resource) => {
    const API_URL = `/api/${resource}`;

    // Helper para evitar el caché agresivo del navegador
    const getTimestamp = () => `t=${new Date().getTime()}`;

    return {
        // Consultar todos los registros
        getAll: async () => {
            const response = await axios.get(`${API_URL}?${getTimestamp()}`);
            return response.data;
        },

        // Búsqueda por ID
        getById: async (id) => {
            const response = await axios.get(`${API_URL}/${id}?${getTimestamp()}`);
            return response.data;
        },

        // Búsqueda de productos por categoría
        getByCategory: async (categoryName) => {
            const response = await axios.get(`${API_URL}/category/${encodeURIComponent(categoryName)}?${getTimestamp()}`);
            return response.data;
        },

        // Crear un registro
        create: async (data) => {
            const response = await axios.post(API_URL, data);
            return response.data;
        },

        // Actualizar un registro existente
        update: async ({ id, data }) => {
            const { id: _, ...cleanData } = data;
            const response = await axios.put(`${API_URL}/${id}`, cleanData);
            return response.data;
        },

        // Borrar un registro lógico/físico
        delete: async (id) => {
            const response = await axios.delete(`${API_URL}/${id}`);
            return response.data;
        },

        // Buscar por nombre
        searchByName: async (name) => {
            const response = await axios.get(`${API_URL}/search`, {
                params: { name, t: new Date().getTime() }
            });
            return response.data;
        }
    };
};

// Se obtienen los datos del perfil del usuario de la sesión
export const profileService = {
    getMyProfile: async () => {
        const response = await axios.get(`/api/users/me?t=${new Date().getTime()}`);
        return response.data;
    },

    updateMyProfile: async (payload) => {
        const response = await axios.put(`/api/users/me?t=${new Date().getTime()}`, payload);
        return response.data;
    }
};