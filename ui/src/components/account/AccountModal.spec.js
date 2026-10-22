import { describe, it, expect, vi, beforeEach } from "vitest";
import { mount, flushPromises } from "@vue/test-utils";
import AccountModal from "./AccountModal.vue";
import userService from "@/services/user.service";

vi.mock("@/services/user.service", () => ({
  default: {
    getMyProfile: vi.fn().mockResolvedValue({ email: "user@example.com" }),
    updateMyProfile: vi.fn().mockResolvedValue({}),
  },
}));

vi.mock("vue-router", () => ({
  useRouter: () => ({ push: vi.fn() }),
}));

describe("AccountModal", () => {
  beforeEach(() => vi.clearAllMocks());

  it("charge le profil puis envoie la mise à jour", async () => {
    const wrapper = mount(AccountModal);
    await flushPromises();

    expect(wrapper.find("#email").element.value).toBe("user@example.com");

    await wrapper.find("#old-password").setValue("Ancien123");
    await wrapper.find("#new-password").setValue("Secret123");
    await wrapper.find("form").trigger("submit");
    await flushPromises();

    expect(userService.updateMyProfile).toHaveBeenCalledWith({
      email: "user@example.com",
      oldPassword: "Ancien123",
      newPassword: "Secret123",
    });
  });
});
