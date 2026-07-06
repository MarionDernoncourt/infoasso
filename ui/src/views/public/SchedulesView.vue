<template>
  <div class="public-schedule-page">
    <TheSidebar :userEmail="userEmail" />

    <h1>Planning des activités</h1>
    <ScheduleFilter @filter-change="updateFilters" />

    <ScheduleTable :schedules="filteredSchedules" :loading="isLoading" />


  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import ScheduleTable from '@/components/schedules/ScheduleTable.vue';
import ScheduleFilter from '@/components/schedules/ScheduleFilter.vue';
import schedulesService from '@/services/schedules.service';
import  TheSidebar  from "@/components/layout/TheSidebar.vue";

const route = useRoute();
const allSchedules = ref([]);
const isLoading = ref(false);

// Récupération des données
const fetchAllSchedules = async () => {
  isLoading.value = true;
  try {
    allSchedules.value = await schedulesService.getAll(route.params.id);
  } catch (error) {
    console.error("Erreur de chargement: ", error);
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchAllSchedules);

// Le computed qui servira à filtrer tes données pour la Table
const filteredSchedules = computed(() => {
  // Pour l'instant, on renvoie tout.
  // Tu ajouteras ta logique de filtrage ici plus tard !
  return allSchedules.value;
});



</script>

<style scopep>
.public-schedule-page {
  margin-left: 260px;
  padding: 40px;
  background-color: #f9f7f6;
  min-height: 100vh;
}

h1 {
  color: #2c1a14;
  font-weight: 800;
  margin-bottom: 20px;
}
</style>
