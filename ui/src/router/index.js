import { createRouter, createWebHistory } from "vue-router";
import publicRoutes from "./public.routes.js";
import authRoutes from "./auth.routes.js";
import protectedRoutes from "./protected.routes.js";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [...publicRoutes, ...authRoutes, ...protectedRoutes],
});

// Fonction pour vérifier l'expiration du JWT
function isTokenExpired(token) {
  if(!token) return true;
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
      .split("")
      .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
      .join("")
    );
    const { exp } = JSON.parse(jsonPayload);
    const now = Date.now() / 1000;
    return exp < now;
  } catch (error) {
    console.error("Erreur lors de la vérification du JWT:", error);
    return true;
  }
}

/// le garde fou global
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token");
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);

  if(requiresAuth) {
    if(!token || isTokenExpired(token)) {
      localStorage.removeItem("token");
      localStorage.removeItem("user_id");
      localStorage.removeItem("user_email");
      localStorage.removeItem("user_role");
      return next({ name: "login" });
    }
  }
  next();
});

export default router;
