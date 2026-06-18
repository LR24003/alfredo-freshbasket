import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { Toaster } from "react-hot-toast";
// Importación de TanStack Query
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

// Importación de ruta de cada entidad
import Home from "./pages/home";
import Login from "./pages/Login";
import Freshbasket from "./pages/freshbasket";
import Register from './pages/register';
import Profile from "./pages/profile.jsx";
import ForgotPassword from './pages/ForgotPassword';

import Products from "./pages/products";
import Suppliers from "./pages/suppliers";
import Users from "./pages/users";
import Entries from "./pages/entries";
import Exits from "./pages/exits";
import Categories from "./pages/categories";
import Countries from "./pages/countries";
import Cart from "./pages/Carrito.jsx";

// Creación del cliente global de Queries
const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            staleTime: 1000 * 60 * 1,
            refetchOnWindowFocus: false,
            retry: (failureCount, error) => {
                if (error?.response?.status === 502 || error?.status === 502 || error?.response?.status === 403) {
                    return false; // Evitamos reintentos si cae en error de permisos o pasarela caída
                }
                return failureCount < 2;
            },
        },
    },
});

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(
        () => !!localStorage.getItem("token")
    );

    // Función centralizada para reflejar el estado de autenticación en tiempo real
    const checkAuth = () => {
        const token = localStorage.getItem("token");
        setIsAuthenticated(!!token);
        if (!token) {
            queryClient.clear(); // Limpia la caché de consultas de React Query si la sesión expira
        }
    };

    useEffect(() => {
        // Escucha cambios desde otras pestañas
        window.addEventListener("storage", checkAuth);
        // Escucha eventos del mismo hilo (por si haces login/logout directo en la app)
        window.addEventListener("local-storage-change", checkAuth);

        return () => {
            window.removeEventListener("storage", checkAuth);
            window.removeEventListener("local-storage-change", checkAuth);
        };
    }, []);

    // Manejador nativo del Logout que se pasa al layout de Freshbasket
    const handleLogout = () => {
        localStorage.clear(); // O remueve uno por uno si conservas datos independientes
        setIsAuthenticated(false);
        queryClient.clear();
    };

    return (
        <QueryClientProvider client={queryClient}>
            <Router>
                <Toaster position="top-center" reverseOrder={false} />

                <div className="app-container">
                    <nav className="menu-grid"></nav>

                    <Routes>
                        {/* Rutas públicas */}
                        <Route path="/" element={<Home />} />
                        <Route
                            path="/login"
                            element={
                                !isAuthenticated ? (
                                    <Login setIsAuthenticated={(val) => {
                                        setIsAuthenticated(val);
                                        // Dispara evento para sincronizar inmediatamente las queries activas
                                        window.dispatchEvent(new Event("local-storage-change"));
                                    }} />
                                ) : (
                                    <Navigate to="/freshbasket" replace />
                                )
                            }
                        />
                        <Route path="/register" element={<Register />} />
                        <Route path="/forgot-password" element={<ForgotPassword />} />

                        {/* Rutas privadas */}
                        <Route
                            path="/freshbasket"
                            element={isAuthenticated ? <Freshbasket onLogout={handleLogout} /> : <Navigate to="/login" replace />}
                        >
                            <Route path="productos" element={<Products />} />
                            <Route path="usuarios" element={<Users />} />
                            <Route path="proveedores" element={<Suppliers />} />
                            <Route path="entradas" element={<Entries />} />
                            <Route path="salidas" element={<Exits />} />
                            <Route path="categorias" element={<Categories />} />
                            <Route path="paises" element={<Countries />} />
                            <Route path="cart" element={<Cart />} />

                            <Route
                                path="my-profile"
                                element={
                                    <Profile
                                        loggedInUser={{
                                            id: localStorage.getItem("userId"),
                                            name: localStorage.getItem("userName"),
                                            lastName: localStorage.getItem("userLastName") || "",
                                            email: localStorage.getItem("userEmail") || "",
                                            role: localStorage.getItem("userRole") || "",
                                            countryName: localStorage.getItem("userCountry") || ""
                                        }}
                                    />
                                }
                            />
                        </Route>

                        <Route path="*" element={<Navigate to="/" replace />} />
                    </Routes>
                </div>
            </Router>
        </QueryClientProvider>
    );
}

export default App;