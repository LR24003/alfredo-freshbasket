import axios from '../services/axiosConfig.js';

export const downloadReportFile = async (endpoint, fileName) => {
    try {
        const response = await axios.get(endpoint, {
            responseType: 'blob',
        });

        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', fileName);
        document.body.appendChild(link);
        link.click();

        link.parentNode.removeChild(link);
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error("Error descargando el archivo:", error);
        alert("Ocurrió un error al generar la descarga del reporte.");
    }
};