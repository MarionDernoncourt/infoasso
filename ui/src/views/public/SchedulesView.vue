<template>
  <div class="public-schedule-page" role="main" aria-label="Page planning et filtre de recherche d'activité">
    <div class="filters-container">
      <ScheduleFilter
      @submitFilters="handleFilterSearch"
      @cancel="fetchAllSchedules" />
    </div>

    <div class="header">
      <h1>Planning des activités</h1>
      <div class="button-action">
        <button
        type="button"
        v-if="isOwner"
        class="add-schedule-btn"
        @click="goToCreateSchedulePage"
        aria-label="Ajouter une nouvelle activité au planning"
        >
        Ajouter une activité</button>
        <button
        type="button"
        class="back-btn"
        @click="goToAssociationCard"
        aria-label="Retourner à la fiche de l'association"
        >
        Retour à la fiche</button>
      </div>
    </div>


    <div class="main-content"
    aria-label="Planning des activité de l'association">
      <ScheduleTable
      :schedules="filteredSchedules"
      :loading="isLoading"
      @select-activity="openDetail"
      />

     <ScheduleDetails
      v-if="selectedActivity"
      :activity="selectedActivity"
      @close="closeDetail"
      @updateSchedule="updateSchedule"
      :is-owner="isOwner"
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
import assoService from '@/services/asso.service';

const route = useRoute();
const router = useRouter();
const allSchedules = ref([]);
const isLoading = ref(false);
const currentLoggedEmail = localStorage.getItem('user_email');
const selectedActivity = ref(null);
const assoId = route.params.id;
const isOwner = ref(false);


const openDetail = (activity) => {
  selectedActivity.value = activity;
console.log("activity: ", selectedActivity.value)
};

const updateSchedule = () => {
  console.log(selectedActivity.value.id);
  router.push(`/association/${assoId}/updateSchedule/${selectedActivity.value.id}`)
}

const closeDetail = () => {
  selectedActivity.value = null;
};
const filteredSchedules = computed(() => {
  return allSchedules.value;
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
const checkOwnerShip = async () => {
  try {
    const asso = await assoService.getById(assoId);
    isOwner.value = (currentLoggedEmail && asso?.ownerEmail === currentLoggedEmail);
    console.log(isOwner.value, currentLoggedEmail);
  } catch(error){
    console.error("Erreur de vérification propriétaire: ", error);
    isOwner.value = false;
  }
}
onMounted(async () => {
  await Promise.all([
    fetchAllSchedules(),
    checkOwnerShip()
  ]);
});

const handleFilterSearch = async (filters) => {
  console.log("Parent recoit: ", filters);
  isLoading.value = true;

  try {

    const data = await schedulesService.getAll(assoId, filters);

    if(data.length === 0) {
      alert("Aucune activité trouvée pour ces critères. Affichage du planning complet.");

      allSchedules.value = await schedulesService.getAll(assoId, {});

    } else {
    allSchedules.value = data;

    }

  } catch (error) {
    console.error('Erreur de chargement: ', error);
  } finally {
    isLoading.value = false
  }
}
const goToCreateSchedulePage = () => {
  router.push(`/association/${assoId}/createSchedule`);
}

const goToAssociationCard = async () => {
  await checkOwnerShip();

  console.log("Valeur de isOwner après check :", isOwner.value);
  console.log("Email stocké :", currentLoggedEmail);

  if (isOwner.value === true) {
    router.push("/dashboard");
  } else {
    router.push(`/association/${assoId}`);
  }
}
</script>

<style scoped>
.public-schedule-page {
  margin-left: 260px;
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
  padding: 20px;
}

.back-btn,
.add-schedule-btn {

  padding: 8px 16px;

  margin: 10px;
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
