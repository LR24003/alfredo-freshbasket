import { useQuery } from '@tanstack/react-query';
import axios from '../services/axiosConfig';

const RESOURCE_URL = '/api/inventory-lost-report';
const getTimestamp = () => new Date().getTime();

export const useInventoryLost = (filterType, filterValues) => {
    return useQuery({
        queryKey: ['inventory-lost-report', filterType, filterValues],
        queryFn: async ({ queryKey }) => {
            const [_, type, values] = queryKey;

            try {
                if (!type || type === 'all' || !values) {
                    const response = await axios.get(`${RESOURCE_URL}?t=${getTimestamp()}`);
                    return response.data;
                }

                if (type === 'id') {
                    const id = values.id;
                    if (!id || String(id).trim() === '') return [];

                    const response = await axios.get(`${RESOURCE_URL}/${String(id).trim()}?t=${getTimestamp()}`, {
                        _skipGlobalError: true
                    });

                    return response.data ? (Array.isArray(response.data) ? response.data : [response.data]) : [];
                }

                if (type === 'productname') {
                    const productName = values.productName;
                    if (!productName || String(productName).trim() === '') return [];

                    const response = await axios.get(`${RESOURCE_URL}/search/product-name`, {
                        params: { productName: productName.trim(), t: getTimestamp() }
                    });
                    return response.data;
                }

                if (type === 'units') {
                    const unitsLost = values.unitsLost;
                    if (!unitsLost || String(unitsLost).trim() === '') return [];

                    const response = await axios.get(`${RESOURCE_URL}/search/units-lost`, {
                        params: { unitsLost: unitsLost, t: getTimestamp() }
                    });
                    return response.data;
                }

                if (type === 'reason') {
                    const exitReason = values.exitReason;
                    if (!exitReason || String(exitReason).trim() === '') return [];

                    const response = await axios.get(`${RESOURCE_URL}/search/exit-reason`, {
                        params: { exitReason: exitReason, t: getTimestamp() }
                    });
                    return response.data;
                }

                return [];
            } catch (err) {
                return [];
            }
        },
        placeholderData: (previousData) => previousData,
        retry: false
    });
};