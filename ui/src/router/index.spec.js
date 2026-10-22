import { describe, it, expect, beforeEach } from "vitest";
import router from "./index.js";
import protectedRoutes from "./protected.routes.js";

const toPath = (path) => path.replaceAll(":id", "1").replaceAll(":scheduleId", "2");

describe("Garde de navigation", () => {
  beforeEach(async () => {
    localStorage.clear();
    await router.replace("/").catch(() => {});
  });

  it("redirige vers /login sans token sur les routes protégées", async () => {
    for (const route of protectedRoutes) {
      localStorage.clear();
      await router.push(toPath(route.path)).catch(() => {});
      expect(router.currentRoute.value.name).toBe("login");
    }
  });
});
