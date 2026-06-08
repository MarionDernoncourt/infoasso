import apiClient from "./api";

export default {
  async checkRnaNumber(rnaNumber) {
    const response = await apiClient.get("/rna/" + rnaNumber);
    return response.data;
  },
};
