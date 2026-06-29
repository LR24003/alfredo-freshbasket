import { useQuery } from '@tanstack/react-query';
import axios from '../services/axiosConfig.js';

const RESOURCE_URL = '/api/customer-loyal-report';
const getTimestamp = () => new Date().getTime();

export const useCustomerLoyal = (filterType, filterValue) => {
    return useQuery({
        queryKey: ['customer-loyal-report', filterType, filterValue],
        queryFn: async ({ queryKey }) => {
            const [_, type, value] = queryKey;

            if (!type || type === 'all' || !value || String(value).trim() === '') {
                const response = await axios.get(`${RESOURCE_URL}?t=${getTimestamp()}`);
                return response.data;
            }

            if (type === 'customername') {
                const response = await axios.get(`${RESOURCE_URL}/search/customername`, {
                    params: {
                        customerName: value.trim(),
                        t: getTimestamp(),
                    }
                });
                return response.data;
            }

            if (type === 'totalpurchases') {
                const response = await axios.get(`${RESOURCE_URL}/search/totalpurchases`, {
                    params: {
                        totalPurchases: value,
                        t: getTimestamp()
                    }
                });
                return response.data;
            }
            return [];
        },
        placeholderData: (previousData) => previousData,
    });
};