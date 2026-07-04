import { useQuery } from '@tanstack/react-query';
import axios from '../services/axiosConfig';

const RESOURCE_URL = '/api/suppliers-report';
const getTimestamp = () => new Date().getTime();

export const useSuppliersReport = (filterType, filterValues) => {
    return useQuery({
        queryKey: ['suppliers-report', filterType, filterValues],
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

                if (type === 'suppliername') {
                    const supplierName = values.supplierName;
                    if (!supplierName || String(supplierName).trim() === '') return [];

                    const response = await axios.get(`${RESOURCE_URL}/search/supplier-name`, {
                        params: { supplierName: supplierName.trim(), t: getTimestamp() }
                    });
                    return response.data;
                }

                if (type === 'country') {
                    const country = values.country;
                    if (!country || String(country).trim() === '') return [];

                    const response = await axios.get(`${RESOURCE_URL}/search/supplier-country`, {
                        params: { country: country, t: getTimestamp() }
                    });
                    return response.data;
                }

                if (type === 'totalproductos') {
                    const totalProducts = values.totalProducts;
                    if (!totalProducts || String(totalProducts).trim() === '') return [];

                    const response = await axios.get(`${RESOURCE_URL}/search/total-products`, {
                        params: { totalProducts: totalProducts, t: getTimestamp() }
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