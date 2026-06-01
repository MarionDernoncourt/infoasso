import { createRouter, createWebHistory } from "vue-router";
import publicRoutes from "./public.routes.js";
import authRoutes from "./auth.routes.js";
import protectedRoutes from "./protected.routes.js";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [...publicRoutes, ...authRoutes, ...protectedRoutes],
});

export default router;
