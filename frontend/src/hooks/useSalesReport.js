import { useQuery } from '@tanstack/react-query';
import axios from '../services/axiosConfig';

const RESOURCE_URL = '/api/sales-report';
const getTimestamp = () => new Date().getTime();

export const useSalesReport = (filterType, filterValues = {}) => {
    return useQuery({
        queryKey: ['sales-report', filterType, filterValues],
        queryFn: async ({ queryKey }) => {
            const [_, type, values] = queryKey;

            if (!type || type === 'all') {
                const response = await axios.get(`${RESOURCE_URL}?t=${getTimestamp()}`);
                return response.data;
            }

            if (type === 'employeename') {
                const { employeeName } = values;
                if (!employeeName || String(employeeName).trim() === '') return [];

                const response = await axios.get(`${RESOURCE_URL}/search/employee-name`, {
                    params: {
                        employeeName: employeeName.trim(),
                        t: getTimestamp(),
                    }
                });
                return response.data;
            }

            if (type === 'specific-date') {
                const { day, month, paymentMethod } = values;

                if (!day && !month && (!paymentMethod || paymentMethod.trim() === '')) {
                    const response = await axios.get(`${RESOURCE_URL}?t=${getTimestamp()}`);
                    return response.data;
                }

                const response = await axios.get(`${RESOURCE_URL}/filter-sales`, {
                    params: {
                        day: day || null,
                        month: month || null,
                        paymentMethod: paymentMethod ? paymentMethod.trim() : null,
                        t: getTimestamp()
                    }
                });
                return response.data;
            }

            if (type === 'date-range') {
                const { startDate, endDate, paymentMethod } = values;

                if (!startDate && !endDate) return [];

                const response = await axios.get(`${RESOURCE_URL}/filter-by-range`, {
                    params: {
                        startDate: startDate || null,
                        endDate: endDate || null,
                        paymentMethod: paymentMethod ? paymentMethod.trim() : null,
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