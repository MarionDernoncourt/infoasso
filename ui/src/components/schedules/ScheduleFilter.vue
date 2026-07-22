<template>
  <div class="filters-area" role="search" aria-label="Filtres de recherche de créneaux">
    <h2>Filtrer ma recherche</h2>

    <div class="form-section">
      <div class="form-group">
        <label for="dayOfWeek">Jour</label>
        <select id="dayOfWeek" v-model="filters.dayOfWeek" aria-label="Filtrer par jour de la semaine">
          <option value="">---Choisir un jour---</option>
          <option v-for="day in dayOfWeek" :key="day" :value="day">{{ day }}</option>
        </select>
      </div>

      <div class="form-group">
        <label for="age">Age</label>
        <input type="number"
        id="age"
        v-model="filters.age"
        aria-label="Filtrer par âge du participant"
        /> ```
      </div>

      <div class="form-group">
        <label for="city">Ville</label>
        <input type="text" id="city" v-model="filters.city" aria-label="Filtrer par ville" />

      </div>
    </div>


    <div class="form-action">
      <button type="button" class="submit-btn" @click="applyFilters" aria-label="Appliquer les filtres de recherche">Filtrer</button>
      <button type="button" class="cancel-btn" @click="cancelFilters" aria-label="Réinitialiser tous les filtres">Réinitialiser les filtres</button>

    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import schedulesService from "@/services/schedules.service";

const emit = defineEmits(['submitFilters', 'cancel']);

const dayOfWeek = ref([]);

const filters = ref({});

onMounted(async () => {
  //Récupération des ENUMS pour le select
  try {
    const response = await schedulesService.getDaysOfWeek();
    dayOfWeek.value = response.data;
  } catch (error) {
    console.error("Erreur lors de la récupération des jours de la semaine : ", error)
  }
})

const applyFilters = () => {

  const activeFilters = {};

  // On ne prend que les champs qui sont remplis
if (filters.value.dayOfWeek) activeFilters.dayOfWeek = filters.value.dayOfWeek;

  // Utilise Number() pour t'assurer que c'est un chiffre
  if (filters.value.age !== undefined && filters.value.age !== null && filters.value.age !== "") {
    activeFilters.age = Number(filters.value.age);
  }

  if (filters.value.city) activeFilters.city = filters.value.city;

  console.log("Filtres envoyés au parent: ", activeFilters);
  emit('submitFilters', activeFilters); // Envoie l'objet nettoyé !
}
const cancelFilters = () => {
  filters.value = "";
  emit('cancel');
}
</script>


<style scoped>
.filters-area {
  width: var(--sidebar-width);
  background-color: #2c1a14;
  color: white;
  display: flex;
  flex-direction: column;
  padding: 30px 20px;
  box-sizing: border-box;
  position: fixed;
  top: var(--header-height);
  left: 0;
  height: calc(100vh - var(--header-height));
  z-index: 100;
}

.form-section {
  display: flex;
  flex-direction: column;
  justify-content: left;
  gap: 20px;
  border-bottom: 1px solid #f0e6df;
  width: 100%;
}

.form-section h2 {
  font-size: 1.2rem;
  color: var(--text-main);
  margin: 0;
  font-weight: 600;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-action {
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: left;
  align-items: start;
  margin-top: 20px;
}

button {
  padding: 10px 20px;
  border-radius: 10px;
  font-weight: 500;
  margin: 10px;
  background: #f4f0ed;

  color: var(--text-main);
  border: 1px solid #dcd1ca;
}

.submit-btn:hover,
.cancel-btn:hover {
  color: #5a4d46;
  border: 1px solid var(--primary-color);

}

/* --- Inputs, Selects, Textareas --- */
input,
select {
  padding: 10px 14px;
  border: 1px solid #e0d8d3;
  /* Bordure discrète */
  border-radius: 10px;
  /* Plus arrondi */
  background-color: #fcfbf9;
  /* Crème très clair */
  color: #4a403a;
  /* Marron foncé doux pour le texte */
  transition: all 0.3s ease;
  width: 200px;
}

input:focus {
  border-color: #d96c5b;
  background-color: #ffffff;
  box-shadow: 0 0 0 4px rgba(217, 108, 91, 0.1);
  /* Glow léger */
}
</style>
