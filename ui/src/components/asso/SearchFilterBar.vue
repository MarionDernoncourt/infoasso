<template>
  <div class="search-filters">
    <input v-model="filters.q" placeholder="Nom association..." @keyup.enter="handleSearch" />

    <input v-model="filters.city" placeholder="Ville..." @keyup.enter="handleSearch" />

    <select id="categoryTypes" v-model="filters.categoryTypes" @change="handleTypeChange" required>
      <option value="" disabled>-- Choisissez une catégorie --</option>
      <option v-for="type in categoryTypes" :key="type" :value="type">
        {{ type }}
      </option>
    </select>

    <input v-model="filters.age" type="number" placeholder="Âge du participant" @keyup.enter="handleSearch" />

    <button @click="handleSearch">Rechercher</button>
    <button @click="cancelFilters">Réinitialiser les filtres</button>
  </div>>
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

button {
  background: darksalmon;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
}
</style>
