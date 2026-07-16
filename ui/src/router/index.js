import { createRouter, createWebHistory } from "vue-router";
import publicRoutes from "./public.routes.js";
import authRoutes from "./auth.routes.js";
import protectedRoutes from "./protected.routes.js";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [...publicRoutes, ...authRoutes, ...protectedRoutes],
});

/// le garde fou global
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token");
  const isAuth = !!token; //devien true si token présent, false sinon

  // si la route est protégée mais pas de token:
  if (to.meta.requiresAuth && !isAuth) {
    next({ name: "login" });
  } else {
    next();
  }
});

export default router;
