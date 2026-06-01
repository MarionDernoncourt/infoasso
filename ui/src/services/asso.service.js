import apiClient from "./api";

export default {
  // Récupération de toutes les associations
  async getAll() {
    const response = await apiClient.get("/associations");
    return response.data;
  },
  // Récuperation d'une seule association via id
  async getById(id) {
    const response = await apiClient.get(`/associations/${id}`);
    return response.data;
  },
  // Création d'une association
  async create(assoData) {
    const response = await apiClient.post("/associations", assoData);
    return response.data;
  },
  // Mise à jour d'une association
  async update(id, assoUpdate) {
    const response = await apiClient.put(`/associations/${id}`, assoUpdate);
    return response.data;
  },
  // Suppression d'une association
  async delete(id) {
    const response = await apiClient.delete(`/associations/${id}`);
    return response.data;
  },
};
