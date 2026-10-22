import { describe, it, expect, vi, beforeEach } from "vitest";
import { mount, flushPromises } from "@vue/test-utils";
import ScheduleForm from "./ScheduleForm.vue";

vi.mock("@/services/schedules.service", () => ({
  default: {
    getDaysOfWeek: vi.fn().mockResolvedValue({ data: ["LUNDI"] }),
    getLocationsByAssociationId: vi.fn().mockResolvedValue([]),
  },
}));

vi.mock("vue-router", async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useRoute: () => ({ name: "createSchedule", params: { id: "1" } }),
  };
});

describe("ScheduleForm", () => {
  beforeEach(() => vi.clearAllMocks());

  it("affiche les erreurs de validation", () => {
    const wrapper = mount(ScheduleForm, {
      props: { errors: { activityName: "Le nom est obligatoire." } },
    });

    expect(wrapper.find("#activityNameError").text()).toBe(
      "Le nom est obligatoire.",
    );
  });

  it("émet submit avec l'horaire saisi", async () => {
    const wrapper = mount(ScheduleForm);
    await flushPromises();

    await wrapper.find("#activityName").setValue("Baby gym");
    await wrapper.find("#dayOfWeek").setValue("LUNDI");
    await wrapper.find('[aria-label="Heure de début (heures)"]').setValue("10");
    await wrapper.find('[aria-label="Heure de début (minutes)"]').setValue("00");
    await wrapper.find('[aria-label="Heure de fin (heures)"]').setValue("11");
    await wrapper.find('[aria-label="Heure de fin (minutes)"]').setValue("00");
    await wrapper.find("form").trigger("submit");

    expect(wrapper.emitted("submit")[0][0]).toMatchObject({
      activityName: "Baby gym",
      dayOfWeek: "LUNDI",
      startTime: "10:00",
      endTime: "11:00",
    });
  });
});
