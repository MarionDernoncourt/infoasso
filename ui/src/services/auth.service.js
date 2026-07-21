import apiClient from "./api";

export default {
  // Connexion de l'utilisateur
  async login(credentials) {
    //credentials = {email : '...', password: '...' }
    const response = await apiClient.post("/auth/login", credentials);
    if (response.data && response.data.token) {
      localStorage.setItem("token", response.data.token);
      localStorage.setItem("user_id", response.data.id);
      localStorage.setItem("user_email", response.data.email);
      localStorage.setItem("user_role", response.data.role);
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
    localStorage.removeItem("user_id");
    localStorage.removeItem("user_email");
    localStorage.removeItem("user_role");
  },
};
