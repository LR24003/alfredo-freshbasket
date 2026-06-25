import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "../services/axiosConfig.js";
import toast from "react-hot-toast";

export function useRegister(redirectPath = "/login") {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [countriesList, setCountriesList] = useState([]);

    // Carga automática de países al usar el hook
    useEffect(() => {
        const fetchCountries = async () => {
            try {
                const response = await axios.get("/api/countries");
                setCountriesList(response.data || []);
            } catch (error) {
                console.error("No se pudo cargar la lista de países:", error);
            }
        };
        fetchCountries();
    }, []);

    const registerUser = async (formDataObject, onSuccessCallback = null) => {
        setLoading(true);

        const { countryName, ...restData } = formDataObject;
        const countryValue = countryName?.trim();

        if (!countryValue) {
            toast.error("Por favor, introduce o selecciona un país.");
            setLoading(false);
            return false;
        }

        const newUser = {
            ...restData,
            role: formDataObject.role || "CLIENTE",
            countryName: countryValue
        };
        try {
            await axios.post("/api/auth/register", newUser);
            toast.success("¡Cuenta creada correctamente!");

            if (onSuccessCallback) {
                onSuccessCallback(newUser);
            } else if (redirectPath) {
                setTimeout(() => {
                    navigate(redirectPath);
                }, 1500);
            }
            return true;
        } catch (error) {
            const errorMessage = error.response?.data?.message || "Verifique los datos ingresados";
            toast.error("Error al registrar: " + errorMessage);
            return false;
        } finally {
            setLoading(false);
        }
    };

    return {
        countriesList,
        loading,
        registerUser
    };
}