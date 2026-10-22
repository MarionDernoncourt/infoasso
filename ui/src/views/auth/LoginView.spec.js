import { describe, it, expect, vi, beforeEach } from "vitest";
import { mount, flushPromises } from "@vue/test-utils";
import LoginView from "./LoginView.vue";
import authService from "@/services/auth.service.js";

const push = vi.fn();

vi.mock("@/services/auth.service.js", () => ({
  default: { login: vi.fn() },
}));

vi.mock("vue-router", () => ({
  useRouter: () => ({ push }),
}));

const mountForm = () =>
  mount(LoginView, {
    global: { stubs: { RouterLink: { template: "<a><slot /></a>" } } },
  });

describe("LoginView", () => {
  beforeEach(() => vi.clearAllMocks());

  it("bloque l'envoi si les champs sont vides", async () => {
    const wrapper = mountForm();
    await wrapper.find("form").trigger("submit");
    await flushPromises();

    expect(authService.login).not.toHaveBeenCalled();
    expect(wrapper.find("#emailError").exists()).toBe(true);
    expect(wrapper.find("#passwordError").exists()).toBe(true);
  });

  it("connecte l'utilisateur et redirige vers le dashboard", async () => {
    const credentials = { email: "user@example.com", password: "Secret123!" };
    authService.login.mockResolvedValue({ token: "fake-token" });

    const wrapper = mountForm();
    await wrapper.find("#email").setValue(credentials.email);
    await wrapper.find("#password").setValue(credentials.password);
    await wrapper.find("form").trigger("submit");
    await flushPromises();

    expect(authService.login).toHaveBeenCalledWith(credentials);
    expect(push).toHaveBeenCalledWith("/dashboard");
  });
});
