import { useQuery } from '@tanstack/react-query';
import axios from '../services/axiosConfig.js';

// Definimos la ruta base del controlador exclusivo de auditoría
const RESOURCE_URL = '/api/audit-logs-report';

// Helper para limpiar el caché del navegador
const getTimestamp = () => `t=${new Date().getTime()}`;

export const useAuditLogs = (filterType, filterValue) => {
    return useQuery({
        queryKey: ['audit-logs-report', filterType, filterValue],
        queryFn: async () => {

            if (!filterType || filterType === 'all' || !filterValue || filterValue.trim() === '') {
                const response = await axios.get(`${RESOURCE_URL}?${getTimestamp()}`);
                return response.data;
            }

            if (filterType === 'username') {
                const response = await axios.get(`${RESOURCE_URL}/search/username`, {
                    params: {
                        userName: filterValue.trim(),
                        t: new Date().getTime()
                    }
                });
                return response.data;
            }

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
        placeholderData: (previousData) => previousData,
    });
};