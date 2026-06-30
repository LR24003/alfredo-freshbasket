import { useQuery } from '@tanstack/react-query';
import axios from '../services/axiosConfig';

const RESOURCE_URL = '/api/inventory-report';
const getTimestamp = () => new Date().getTime();

export const useInventoryReport = (filterType, filterValue) => {
    return useQuery({
        queryKey: ['inventory-report', filterType, filterValue],
        queryFn: async ({ queryKey }) => {
            const [_, type, value] = queryKey;

            if (!type || type === 'all' || !value || String(value).trim() === '') {
                const response = await axios.get(`${RESOURCE_URL}?t=${getTimestamp()}`);
                return response.data;
            }

            if (type === 'productname') {
                const response = await axios.get(`${RESOURCE_URL}/search/productname`, {
                    params: {
                        productName: value.trim(),
                        t: getTimestamp(),
                    }
                });
                return response.data;
            }

            if (type === 'stockavailable') {
                const response = await axios.get(`${RESOURCE_URL}/search/stockavailable`, {
                    params: {
                        stockAvailable: value,
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