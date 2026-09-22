import apiClient from "./api";
import { jwtDecode } from "jwt-decode";

function getCurrentUserId() {
  const token = localStorage.getItem("token");
  if (!token) return null;

  try {
    const decoded = jwtDecode(token);
    return decoded.id;
  } catch (error) {
    console.error("Erreur lors du décodage du token : ", error);
    return null;
  }
}

export default {
  async getUserById() {
    const id = getCurrentUserId();
    if (!id) throw new Error("Utilisateur non authentifié");

    const response = await apiClient.get(`/users/${id}`);
    return response.data;
  },

  async updateMyProfile(userUpdate) {
    const id = getCurrentUserId();
    if (!id) throw new Error("Utilisateur non authentifié");

    const response = await apiClient.put(`/users/${id}`, userUpdate);
    return response.data;
  },

  async getMyProfile() {
    const response = await apiClient.get("users/myprofile");
    return response.data;
  },
};
