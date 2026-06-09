import apiClient from "./api";

export default {
  getCategoryTypes() {
    return apiClient.get("/category/types");
  },

  searchCategories(type, query) {
    return apiClient.get("/category/search", {
      params: { type, query },
    });
  },
};
