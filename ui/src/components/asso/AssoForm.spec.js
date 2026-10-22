import { describe, it, expect, vi, beforeEach } from "vitest";
import { mount, flushPromises } from "@vue/test-utils";
import AssoForm from "./AssoForm.vue";
import rnaService from "@/services/rna.service";

vi.mock("@/services/category.service", () => ({
  default: {
    getCategoryTypes: vi.fn().mockResolvedValue({ data: ["Sport"] }),
    searchCategories: vi.fn(),
  },
}));

vi.mock("@/services/rna.service", () => ({
  default: { checkRnaNumber: vi.fn() },
}));

describe("AssoForm", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.spyOn(window, "alert").mockImplementation(() => {});
  });

  it("n'émet pas submit tant que le RNA n'est pas vérifié", async () => {
    const wrapper = mount(AssoForm);
    await wrapper.find("form").trigger("submit");

    expect(wrapper.emitted("submit")).toBeUndefined();
    expect(window.alert).toHaveBeenCalled();
  });

  it("émet submit avec les données du formulaire", async () => {
    rnaService.checkRnaNumber.mockResolvedValue({ officialName: "Asso Test" });

    const wrapper = mount(AssoForm);
    await wrapper.find("#rnaNumber").setValue("W123456789");
    await wrapper.find(".verify-btn").trigger("click");
    await flushPromises();

    await wrapper.find("#displayName").setValue("Club Test");
    await wrapper.find("#email").setValue("contact@asso.fr");
    await wrapper.find("form").trigger("submit");

    expect(wrapper.emitted("submit")[0][0]).toMatchObject({
      rnaNumber: "W123456789",
      officialName: "Asso Test",
      displayName: "Club Test",
      email: "contact@asso.fr",
    });
  });
});
