import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import axios from '../services/axiosConfig';
import { toast } from 'react-hot-toast';

export const useCart = () => {
    const queryClient = useQueryClient();

    let userId = null;
    const token = localStorage.getItem('token');

    if (token && token.includes('.')) {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(
                window.atob(base64)
                    .split('')
                    .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                    .join('')
            );

            const decoded = JSON.parse(jsonPayload);

            if (decoded && decoded.userId) {
                userId = Number(decoded.userId);
            }
        } catch (error) {
            console.error("Error al decodificar el token JWT en useCart:", error);
        }
    }

    const queryKey = ['cart', userId];

    const invalidateCart = () => {
        queryClient.invalidateQueries({ queryKey });
        queryClient.invalidateQueries({ queryKey: ['products'] });
    };

    const cartQuery = useQuery({
        queryKey,
        queryFn: async () => {
            const { data } = await axios.get(`/api/cart/user/${userId}`);
            return data;
        },
        enabled: userId !== null && !isNaN(userId) && userId > 0,
        staleTime: 1000 * 10,
        gcTime: 1000 * 60 * 5,
        retry: false,
    });

    const updateQuantityMutation = useMutation({
        mutationFn: async ({ productId, quantity }) => {
            if (!userId) throw new Error("Usuario no autenticado");
            const { data } = await axios.post(`/api/cart/user/${userId}/items`, {
                productId,
                quantity: Number(quantity)
            });
            return data;
        },
        onSuccess: () => {
            invalidateCart();
        },
        onError: (error) => {
            console.error(error);
            toast.error('No se pudo actualizar la cantidad');
        }
    });

    const removeItemMutation = useMutation({
        mutationFn: async (productId) => {
            if (!userId) throw new Error("Usuario no autenticado");
            const { data } = await axios.delete(`/api/cart/user/${userId}/products/${productId}`);
            return data;
        },
        onSuccess: () => {
            invalidateCart();
            toast.success('Producto eliminado del carrito');
        },
        onError: (error) => {
            console.error(error);
            toast.error('No se pudo eliminar el producto del carrito');
        }
    });

    const checkoutMutation = useMutation({
        mutationFn: async () => {
            if (!userId) throw new Error("Usuario no autenticado");
            await axios.post(`/api/cart/user/${userId}/checkout`);
        },
        onSuccess: () => {
            invalidateCart();
            toast.success('¡Compra realizada con éxito!');
        },
        onError: (error) => {
            console.error(error);
            toast.error('Hubo un problema al procesar la compra');
        }
    });

    return {
        cart: cartQuery.data,
        isLoading: cartQuery.isLoading,
        isError: cartQuery.isError || !userId,
        error: cartQuery.error,

        updateQuantity: updateQuantityMutation.mutate,
        removeItem: removeItemMutation.mutate,
        checkout: checkoutMutation.mutate,
        checkoutAsync: checkoutMutation.mutateAsync,
        isCheckingOut: checkoutMutation.isPending,
        isUpdatingQuantity: updateQuantityMutation.isPending,
        refetch: cartQuery.refetch
    };
};