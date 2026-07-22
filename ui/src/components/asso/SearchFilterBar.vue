<template>
  <div class="search-filters" role="search" aria-label="Filtres de recherche d'associations">
    <div class="filter-group">
      <label for="search-name" class="sr-only">Rechercher par nom d'association</label>
      <input
        id="search-name"
        v-model="filters.q"
        placeholder="Nom association..."
        @keyup.enter="handleSearch"
        aria-label="Rechercher par nom d'association"
      />
    </div>

    <div class="filter-group">
      <label for="search-city" class="sr-only">Filtrer par ville</label>
      <input
        id="search-city"
        v-model="filters.city"
        placeholder="Ville..."
        @keyup.enter="handleSearch"
        aria-label="Filtrer par ville"
      />
    </div>

    <div class="filter-group">
      <label for="categoryTypes" class="sr-only">Sélectionner une catégorie</label>
      <select
        id="categoryTypes"
        v-model="filters.categoryTypes"
        @change="handleTypeChange"
        aria-label="Sélectionner une catégorie d'activité"
      >
        <option value="" disabled>-- Choisissez une catégorie --</option>
        <option v-for="type in categoryTypes" :key="type" :value="type">
          {{ type }}
        </option>
      </select>
    </div>

    <div class="filter-group">
      <label for="search-age" class="sr-only">Filtrer par âge du participant</label>
      <input
        id="search-age"
        v-model="filters.age"
        type="number"
        placeholder="Âge du participant"
        @keyup.enter="handleSearch"
        aria-label="Filtrer par âge du participant"
      />
    </div>

    <button
      class="search-btn"
      @click="handleSearch"
      aria-label="Lancer la recherche avec les filtres sélectionnés"
    >
      Rechercher
    </button>
    <button
      class="reset-btn"
      @click="cancelFilters"
      aria-label="Réinitialiser tous les filtres de recherche"
    >
      Réinitialiser les filtres
    </button>
  </div>
</template>

<script setup>
import categoryService from '@/services/category.service';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const categoryTypes = ref([]);

const filters = ref({
  q: "",
  city: "",
  categoryTypes: "",
  age: ""
})

onMounted((async () => {
  try {
    const response = await categoryService.getCategoryTypes();
    categoryTypes.value = response.data;
    console.log(categoryTypes.value);
  } catch (error) {
    console.error("Erreur lors de la récupération des catégories: ", error)
  }

}))

const handleSearch = () => {
  // Nettoyage des filtres vides pour URL propre
  const query = Object.fromEntries(
    // eslint-disable-next-line no-unused-vars
    Object.entries(filters.value).filter(([_, v]) => v !== '')
  );

  router.push({ path: '/search', query });
}

const cancelFilters = () => {
  filters.value = {
    q: "",
    city: "",
    categoryTypes: "",
    age: ""
  };
  router.push("/search");
}
</script>

<style scoped>
.search-filters {
  display: flex;
  gap: 10px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

input,
select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
}


</style>
