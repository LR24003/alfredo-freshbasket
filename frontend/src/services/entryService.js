// Servicio con el que conectamos todas nuestras peticiones
// de PRODUCTS desde el Backend

import axios from "axios";

const API_URL = "http://192.168.1.60:8080/api/entries";

// Extrae el token en tiempo real para cada petición
const getAuthHeaders = () => {
  const token = localStorage.getItem("token");
  return {
    headers: {
      Authorization: token ? `Bearer ${token}` : ""
    }
  };
};

// GET all entries
export const getAllEntries = async () => {
  const response = await axios.get(API_URL, getAuthHeaders());
  return response.data;
};

// GET entry by ID
export const getEntryById = async (entryId) => {
  const response = await axios.get(`${API_URL}/${entryId}`, getAuthHeaders());
  return response.data;
};

// CREATE ENTRY
export const createEntry = async (entryData) => {
  const response = await axios.post(API_URL, entryData, getAuthHeaders());
  return response.data;
};

// UPDATE ENTRY
export const updateEntry = async (entryId, entryData) => {
  const response = await axios.put(`${API_URL}/${entryId}`, entryData, getAuthHeaders());
  return response.data;
};

// DELETE ENTRY
export const deleteEntry = async (entryId) => {
  const response = await axios.delete(`${API_URL}/${entryId}`, getAuthHeaders());
  return response.status;
};

