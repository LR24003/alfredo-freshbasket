import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import axios from '../services/axiosConfig';
import { toast } from 'react-hot-toast';

export const useSales = () => {
    const queryClient = useQueryClient();
    const [cart, setCart] = useState([]);
    const [search, setSearch] = useState("");

    // Estados del cliente en el POS
    const [selectedCustomer, setSelectedCustomer] = useState({ id: 1, name: "Cliente General / Mostrador" });
    const [customerSearch, setCustomerSearch] = useState("Cliente General / Mostrador");
    const [showCustomerDropdown, setShowCustomerDropdown] = useState(false);

    // Canales de Pago aceptados
    const [paymentMethod, setPaymentMethod] = useState("EFECTIVO");

    // Descuento en porcentaje (0 a 100)
    const [discount, setDiscount] = useState(0);

    // Extraer ID del empleado desde el Token JWT
    let employeeId = null;
    const token = localStorage.getItem('token');
    if (token && token.includes('.')) {
        try {
            const decoded = JSON.parse(decodeURIComponent(window.atob(token.split('.')[1]).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')));
            if (decoded?.userId) employeeId = Number(decoded.userId);
        } catch (e) { console.error("Error JWT", e); }
    }

    // Query: Catálogo de Productos
    const productsQuery = useQuery({
        queryKey: ['products'],
        queryFn: async () => { const { data } = await axios.get('/api/products'); return data; },
        staleTime: 1000 * 30,
    });

    // Query: Catálogo de Usuarios del Sistema
    const usersQuery = useQuery({
        queryKey: ['users'],
        queryFn: async () => {
            const { data } = await axios.get('/api/users');
            return data;
        },
        // 🌟 CAMBIO CRÍTICO: Bajamos el staleTime a 0 y habilitamos refetch para que al
        // regresar de registrar un cliente, React Query dispare el GET inmediatamente.
        staleTime: 0,
        refetchOnMount: true,
    });

    const products = productsQuery.data || [];
    const users = usersQuery.data || [];

    // Filtro de catálogo de productos en el buscador superior del POS
    const filteredProducts = products.filter(p =>
        p.active && p.currentStock > 0 &&
        (p.name.toLowerCase().includes(search.toLowerCase()) || String(p.id).includes(search))
    );

    // Filtro predictivo de clientes (Busca por nombre o apellido)
    const filteredCustomers = users.filter(u => {
        const isCliente = u.role?.toUpperCase() === "CLIENTE" || u.rol?.toUpperCase() === "CLIENTE" || u.roleId === 3;
        const nombreCompleto = `${u.name || ''} ${u.lastName || u.apellido || ''}`.trim().toLowerCase();
        const busquedaLimpia = customerSearch.toLowerCase();

        return isCliente && (nombreCompleto.includes(busquedaLimpia) || String(u.id).includes(busquedaLimpia));
    });

    // LÓGICA DE CÁLCULO DE TOTALES (Subtotal, Deducción y Total Neto final)
    const subtotalAmount = cart.reduce((acc, item) => acc + (item.price * item.quantity), 0);
    const discountAmount = (subtotalAmount * discount) / 100;
    const totalAmount = subtotalAmount - discountAmount;

    // 🚀 MUTACIÓN PRINCIPAL: Procesa la venta incluyendo el descuento mapeado
    const processSaleMutation = useMutation({
        mutationFn: async () => {
            if (!employeeId) throw new Error("Empleado no identificado");

            const salePayload = {
                employeeId: employeeId,
                customerId: Number(selectedCustomer.id),
                paymentMethod: paymentMethod,
                date: new Date().toISOString(),
                status: "COMPLETADA",

                // ATRIBUTOS ENVIADOS: Se añade el total recalculado y el descuento
                discount: discount,
                totalAmount: totalAmount,

                details: cart.map(item => ({
                    productId: item.id,
                    productName: item.name,
                    quantity: item.quantity,
                    price: item.price,
                    unitCost: item.price,
                }))
            };

            const { data } = await axios.post('/api/sales', salePayload);
            return data;
        },
        onSuccess: () => {
            toast.success("¡Transacción completada e inventario actualizado!");
            setCart([]);
            setSelectedCustomer({ id: 1, name: "Cliente General / Mostrador" });
            setCustomerSearch("Cliente General / Mostrador");
            setPaymentMethod("EFECTIVO");
            setDiscount(0); // Limpiamos el descuento para la próxima venta
            queryClient.invalidateQueries({ queryKey: ['products'] }); // Sincroniza stock disminuido
            queryClient.invalidateQueries({ queryKey: ['users'] });    // Limpia caché de usuarios por seguridad
        },
        onError: (error) => {
            console.error("Error capturado de Spring Boot:", error.response?.data);
            toast.error(error.response?.data?.message || "Error de validación al procesar el cobro.");
        }
    });

    // Controladores funcionales del Carrito del POS
    const addToCart = (product) => {
        const itemInCart = cart.find(item => item.id === product.id);
        if (itemInCart) {
            if (itemInCart.quantity >= product.currentStock) return toast.error("Stock máximo alcanzado");
            setCart(cart.map(item => item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item));
        } else { setCart([...cart, { ...product, quantity: 1 }]); }
    };

    const updateQuantity = (id, newQty, maxStock) => {
        if (newQty < 1) return;
        if (newQty > maxStock) return toast.error("Stock insuficiente en almacén");
        setCart(cart.map(item => item.id === id ? { ...item, quantity: parseInt(newQty) } : item));
    };

    const removeFromCart = (id) => setCart(cart.filter(item => item.id !== id));

    return {
        cart,
        search,
        setSearch,
        filteredProducts,
        paymentMethod,
        setPaymentMethod,
        selectedCustomer,
        setSelectedCustomer,
        customerSearch,
        setCustomerSearch,
        filteredCustomers,
        showCustomerDropdown,
        setShowCustomerDropdown,
        addToCart,
        updateQuantity,
        removeFromCart,

        // RETORNOS DE DESCUENTO Y VALORES CALCULADOS
        discount,
        setDiscount,
        subtotalAmount,
        discountAmount,
        totalAmount,

        handleProcessSale: (e) => { e?.preventDefault(); processSaleMutation.mutate(); },
        isLoadingProducts: productsQuery.isLoading || usersQuery.isLoading,
        isProcessingSale: processSaleMutation.isPending
    };
};