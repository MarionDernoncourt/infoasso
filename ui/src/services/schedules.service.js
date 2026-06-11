import apiClient from "./api";

export default {
  // Récupération de tous les schedules d'une asso via son id
  async getAll(assoId, filters = {}) {
    const response = await apiClient.get(`/associations/${assoId}/schedules`, { params: filters });
    return response.data;
  },

  // Création d'un schedule
  async create(assoId, scheduleCreateDto) {
    const response = await apiClient.post(`/associations/${assoId}/schedules`, scheduleCreateDto);
    return response.data;
  },

  // Mise à jour d'un schedule via son id
  async update(assoId, scheduleId, scheduleUpdateDto) {
    const response = await apiClient.put(
      `/associations/${assoId}/schedules/${scheduleId}`,
      scheduleUpdateDto,
    );
    return response.data;
  },

  // Suppression d'un schedule
  async delete(assoId, scheduleId) {
    const response = await apiClient.delete(`/associations/${assoId}/schedules/${scheduleId}`);
    return response.data;
  },

  // Récupération dayOfWeek
  async getDaysOfWeek() {
    const response = apiClient.get("/schedules/dayOfWeek");
    return response.data;
  },
};
