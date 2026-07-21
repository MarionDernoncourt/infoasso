<template>
  <div class="search-layout">
    <header>
      <SearchFilterBar
      :initial-query="route.query.q" />
    </header>

    <div v-if="isLoading">Chargement en cours...</div>

    <div v-else class="results-grid">
      <AssoCardResult v-for="asso in results" :key="asso.id" :asso="asso" />
    </div>

    <div v-if="!isLoading && results.length === 0">
      Aucune association trouvée pour ces critères.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import assoService from '@/services/asso.service';
import { useRoute } from 'vue-router';
import SearchFilterBar from '@/components/asso/SearchFilterBar.vue';
import AssoCardResult from '@/components/asso/AssoCardResult.vue';


const route = useRoute();
const results = ref([]);
const isLoading = ref(false);


const performSearch = async () => {
  isLoading.value = true;
  try {
    // On récupère les filtres depuis l'URL
    const filters = route.query;
    results.value = await assoService.getAll(filters);
    console.log(results.value);
  } catch (error) {
    console.error("Erreur recherche:", error);
  } finally {
    isLoading.value = false;
  }
}

// Lance la recherche au chargement
onMounted(performSearch);

// Surveillance des changements URL pour relancer la recherche si user change les filtres
watch(() => route.query, performSearch);
</script>

<style scoped>
.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  padding: 20px;
  max-width: 1200px;
}

/* Responsive pour mobile/tablette */
@media (max-width: 1024px) {
  .results-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .results-grid {
    grid-template-columns: 1fr;
  }
}
</style>
