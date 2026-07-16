<template>
  <div class="public-schedule-page">
    <div class="filters-container">
      <ScheduleFilter @submitFilters="handleFilterSearch" @cancel="fetchAllSchedules" />
    </div>

    <div class="header">
      <h1>Planning des activités</h1>
      <div class="button-action">
        <button v-if="isOwner" class="add-schedule-btn" @click="goToCreateSchedulePage">Ajouter une activité</button>
        <button class="back-btn" @click="goToAssociationCard">Retour à la fiche</button>
      </div>
    </div>


    <div class="main-content">
      <ScheduleTable
      :schedules="filteredSchedules"
      :loading="isLoading"
      @select-activity="openDetail"
      />

     <ScheduleDetails
      v-if="selectedActivity"
      :activity="selectedActivity"
      @close="closeDetail"
    />

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import ScheduleTable from '@/components/schedules/ScheduleTable.vue';
import ScheduleFilter from '@/components/schedules/ScheduleFilter.vue';
import schedulesService from '@/services/schedules.service';
import ScheduleDetails from '@/components/schedules/ScheduleDetails.vue';

const route = useRoute();
const router = useRouter();
const allSchedules = ref([]);
const isLoading = ref(false);
const currentLoggedEmail = localStorage.getItem('user_email');
const selectedActivity = ref(null);
const assoId = route.params.id;

const openDetail = (activity) => {
  selectedActivity.value = activity;
console.log("activity: ", selectedActivity.value)
};

const closeDetail = () => {
  selectedActivity.value = null;
};
const isOwner = computed(() => {
  if(filteredSchedules.value.length === 0) return false;
return currentLoggedEmail === filteredSchedules.value[0].association.ownerEmail;
});

// Récupération des données
const fetchAllSchedules = async () => {
  isLoading.value = true;
  try {
    allSchedules.value = await schedulesService.getAll(assoId);
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

const handleFilterSearch = async (filters) => {
  isLoading.value = true;

  try {
    allSchedules.value = await schedulesService.getAll(assoId, filters)
  } catch (error) {
    console.error('Erreur de chargement: ', error);
  } finally {
    isLoading.value = false
  }
}
const goToCreateSchedulePage = () => {
  router.push(`/association/${assoId}/createSchedule`);
}
const goToAssociationCard = () => {
  router.push("/dashboard");
}
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

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.back-btn,
.add-schedule-btn {
  background: white;
  border: 1px solid darksalmon;
  color: darksalmon;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s ease;
  margin: 10px;
}

.back-btn:hover,
.add-schedule-btn:hover {
  background: #fdf0eb;
  transform: translateY(-1px);
}

h1 {
  color: #2c1a14;
}

/* --- Styles pour l'Overlay (La bulle) --- */
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.detail-card {
  background: white;
  padding: 40px;
  border-radius: 16px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 20px 25px rgba(0,0,0,0.1);
  position: relative;
}
</style>
