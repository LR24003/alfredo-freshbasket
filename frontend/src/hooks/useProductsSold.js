import { useQuery } from '@tanstack/react-query';
import axios from '../services/axiosConfig';

const RESOURCE_URL = '/api/products-sold-report';
const getTimestamp = () => new Date().getTime();

export const useProductsSold = (filterType, filterValues) => {
    return useQuery({
        queryKey: ['products-sold-report', filterType, filterValues],
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
                    const unitsSold = values.unitsSold;
                    if (!unitsSold || String(unitsSold).trim() === '') return [];

                    const response = await axios.get(`${RESOURCE_URL}/search/units-sold`, {
                        params: { unitsSold: unitsSold, t: getTimestamp() }
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