<template>
  <div class="search-layout">
    <header>
      <!-- On passe la valeur actuelle pour que l'input reste rempli -->
      <SearchFilterBar :initial-query="route.query.q" />
    </header>

    <!-- Liste des résultats... -->
  </div>
</template>

<script setup>
import {ref, onMounted, watch} from 'vue';
import assoService from '@/services/asso.service';
import { useRoute } from 'vue-router';
import SearchFilterBar from '@/components/asso/SearchFilterBar.vue';
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
