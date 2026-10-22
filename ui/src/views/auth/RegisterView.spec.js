import { describe, it, expect, vi, beforeEach } from "vitest";
import { mount, flushPromises } from "@vue/test-utils";
import RegisterView from "./RegisterView.vue";
import authService from "@/services/auth.service";

const push = vi.fn();

vi.mock("@/services/auth.service", () => ({
  default: { register: vi.fn(), login: vi.fn() },
}));

vi.mock("vue-router", () => ({
  useRouter: () => ({ push }),
}));

const mountForm = () =>
  mount(RegisterView, {
    global: { stubs: { RouterLink: { template: "<a><slot /></a>" } } },
  });

describe("RegisterView", () => {
  beforeEach(() => vi.clearAllMocks());

  it("bloque l'envoi si les champs sont invalides", async () => {
    const wrapper = mountForm();
    await wrapper.find("form").trigger("submit");
    await flushPromises();

    expect(authService.register).not.toHaveBeenCalled();
    expect(wrapper.find("#emailError").exists()).toBe(true);
    expect(wrapper.find("#passwordError").exists()).toBe(true);
    expect(wrapper.find("#gdprError").exists()).toBe(true);
  });

  it("crée le compte puis connecte l'utilisateur", async () => {
    const userData = {
      email: "asso@example.com",
      password: "Secret123",
      gdprConsent: true,
    };
    authService.register.mockResolvedValue({ id: 1 });
    authService.login.mockResolvedValue({ token: "fake-token" });

    const wrapper = mountForm();
    await wrapper.find("#register-email").setValue(userData.email);
    await wrapper.find("#register-password").setValue(userData.password);
    await wrapper.find("#gdprConsent").setValue(true);
    await wrapper.find("form").trigger("submit");
    await flushPromises();

    expect(authService.register).toHaveBeenCalledWith(userData);
    expect(authService.login).toHaveBeenCalledWith({
      email: userData.email,
      password: userData.password,
    });
    expect(push).toHaveBeenCalledWith("/dashboard");
  });
});
