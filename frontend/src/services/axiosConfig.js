import axios from "axios";
import toast from "react-hot-toast";

const api = axios.create({
    baseURL: "", // Mantenlo vacío si manejas el proxy desde vite.config.js
    timeout: 10000
});

// Interceptor de peticiones global y automático
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token");
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Interceptor de Respuestas: Control global de errores HTTP
api.interceptors.response.use(
    (response) => response,
    (error) => {
        // Extraemos url y method de forma segura arriba del todo para que existan en todo el scope del error
        const url = error.config?.url || "";
        const method = error.config?.method || "";

        // Flag para identificar si la petición pertenece al módulo del carrito
        const isCartRequest = url.includes("/api/cart") || url.includes("/cart");

        if (error.response) {
            const status = error.response.status;
            const serverMessage = error.response.data?.message;

            const isAuthRequest = url.includes("/auth/login") || url.includes("/login");
            const isIdRequest = /\/\d+$/.test(url) && method.toLowerCase() === "get" && !isCartRequest;

            // 🌟 Si es del carrito, del Auth, o consulta por ID individual, delegamos el error al hook local
            if (isAuthRequest || isIdRequest || isCartRequest || (status === 403 && url.includes("/users"))) {
                return Promise.reject(error);
            }

            // Sistema centralizado de control de errores con toast (Para el resto de la app)
            switch (status) {
                case 401:
                    toast.error(serverMessage || "Sesión expirada. Por favor, inicia sesión de nuevo.");
                    localStorage.clear();
                    setTimeout(() => {
                        window.location.href = "/login";
                    }, 1500);
                    break;
                case 403:
                    toast.error(serverMessage || "No tienes los permisos necesarios para realizar esta acción.");
                    break;
                case 404:
                    toast.error(serverMessage || "El recurso solicitado no fue encontrado.");
                    break;
                case 500:
                    toast.error(serverMessage || "Error interno en el servidor. Inténtalo más tarde.");
                    break;
                default:
                    toast.error(serverMessage || "Ocurrió un error inesperado.");
            }
        } else if (error.request) {
            // Evaluado de forma segura gracias al scope superiorizado de la variable url
            if (!isCartRequest) {
                toast.error("No se pudo conectar con el servidor. Verifica tu conexión.");
            }
        } else {
            if (!isCartRequest) {
                toast.error("Error al procesar la solicitud.");
            }
        }
        return Promise.reject(error);
    }
);

export default api;