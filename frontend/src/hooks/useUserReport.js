import { useQuery } from '@tanstack/react-query';
import axios from '../services/axiosConfig';

const RESOURCE_URL = '/api/user-report';
const getTimestamp = () => new Date().getTime();

export const useUserReport = (filterType, filterValues = {}) => {
    const isIdFilter = filterType === 'id';
    const idValue = String(filterValues?.id || '').trim();

    const isEnabled = !isIdFilter || (isIdFilter && idValue !== '');

    return useQuery({
        queryKey: ['user-report', filterType, filterValues],
        queryFn: async ({ queryKey }) => {
            const [_, type, values] = queryKey;

            if (!type || type === 'all') {
                const response = await axios.get(`${RESOURCE_URL}?t=${getTimestamp()}`);
                return response.data;
            }

            if (type === 'id') {
                const { id } = values;
                if (!id || String(id).trim() === '') return [];

                const response = await axios.get(`${RESOURCE_URL}/${String(id).trim()}?t=${getTimestamp()}`);

                return response.data ? [response.data] : [];
            }

            if (type === 'username') {
                const { fullName } = values;
                if (!fullName || String(fullName).trim() === '') return [];

                const response = await axios.get(`${RESOURCE_URL}/search/user-name`, {
                    params: {
                        fullName: fullName.trim(),
                        t: getTimestamp(),
                    }
                });
                return response.data;
            }

            if (type === 'role') {
                const { role } = values;
                if (!role || String(role).trim() === '') return [];

                const response = await axios.get(`${RESOURCE_URL}/search/user-role`, {
                    params: {
                        role: role.trim(),
                        t: getTimestamp(),
                    }
                });
                return response.data;
            }

            if (type === 'country') {
                const { countryName } = values;
                if (!countryName || String(countryName).trim() === '') return [];

                const response = await axios.get(`${RESOURCE_URL}/search/country-name`, {
                    params: {
                        countryName: countryName.trim(),
                        t: getTimestamp(),
                    }
                });
                return response.data;
            }

            if (type === 'state') {
                const { estado } = values;
                if (!estado || String(estado).trim() === '') return [];

                const response = await axios.get(`${RESOURCE_URL}/search/user-state`, {
                    params: {
                        estado: estado.trim(),
                        t: getTimestamp(),
                    }
                });
                return response.data;
            }

            return [];
        },
        enabled: isEnabled,
        placeholderData: (previousData) => previousData,
        retry: false
    });
};