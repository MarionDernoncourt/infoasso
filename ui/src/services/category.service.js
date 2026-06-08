import apiClient from "./api";

export default {
  // Récupératioon de toutes les catégories
  async getAllCategories() {
    const response = await apiClient.get("/category");
    return response.data;
  },
};
