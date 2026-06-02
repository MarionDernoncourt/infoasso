import apiClient from "./api";

export default {
  // Connexion de l'utilisateur
  async login(credentials) {
    //credentials = {email : '...', password: '...' }
    const response = await apiClient.post("/auth/login", credentials);
    if (response.data && response.data.token) {
      localStorage.setItem("token", response.data.token);
    }
    return response.data;
  },

  // Inscription nouvel utilisateur
  async register(userData) {
    const response = await apiClient.post("/auth/register", userData);
    return response.data;
  },

  logout() {
    localStorage.removeItem("token");
  },
};
