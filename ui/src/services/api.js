import axios from "axios";
import router from "../router";

const apiClient = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

// Intercepteur pour chercher un éventuel JWT stocké lors du login
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

// Intercepteur pour chaque réponse -- Gestion erreur
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Si serveur éteint ou inaccessible
    if (!error.response) {
      alert("Le serveur Spring Boot ne répond pas, est il bien démarré ?");
      return Promise.reject(error);
    }

    const { status } = error.response;

    //Si 401 (token expiré, invalide ou absent sur route protégée)
    if (status === 401) {
      console.warn("Accès non autorisé 401. Redirection vers le Login...");
      localStorage.removeItem("token");
      router.push({ name: "login" });
    }
    // Si 403 Forbidden (pas le rôle nécessaire pour cette action)
    else if (status === 403) {
      alert("Vous n'avez pas les droits nécessaires pour effectuer cette action.");
    }
    // Si 404 Not Found
    else if (status === 404) {
      console.error("Ressource introuvable sur le serveur (404).");
    }

    return Promise.reject(error);
  },
);

export default apiClient;
