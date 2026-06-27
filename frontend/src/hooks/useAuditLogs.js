import { useQuery } from '@tanstack/react-query';
import axios from '../services/axiosConfig.js'; // Ajusta la ruta exacta a tu axiosConfig

// Definimos la ruta base del controlador exclusivo de auditoría
const RESOURCE_URL = '/api/audit-logs-report';

// Helper para limpiar el caché del navegador, igual que en tu apiService
const getTimestamp = () => `t=${new Date().getTime()}`;

export const useAuditLogs = (filterType, filterValue) => {
    return useQuery({
        // La clave de caché reacciona dinámicamente al tipo y valor del filtro
        queryKey: ['audit-logs-report', filterType, filterValue],
        queryFn: async () => {
            // Caso 1: Sin filtros o filtro "Ver todos"
            if (!filterType || filterType === 'all' || !filterValue || filterValue.trim() === '') {
                const response = await axios.get(`${RESOURCE_URL}?${getTimestamp()}`);
                return response.data;
            }

            // Caso 2: Filtrado por nombre de usuario (userName)
            if (filterType === 'username') {
                const response = await axios.get(`${RESOURCE_URL}/search/username`, {
                    params: {
                        userName: filterValue.trim(),
                        t: new Date().getTime()
                    }
                });
                return response.data;
            }

            // Caso 3: Filtrado por acción exacta (INSERT, UPDATE, DELETE)
            if (filterType === 'action') {
                const response = await axios.get(`${RESOURCE_URL}/search/action`, {
                    params: {
                        action: filterValue,
                        t: new Date().getTime()
                    }
                });
                return response.data;
            }

            return [];
        },
        placeholderData: (previousData) => previousData, // Mantiene los datos viejos en pantalla mientras carga el nuevo filtro (UX fluida)
    });
};