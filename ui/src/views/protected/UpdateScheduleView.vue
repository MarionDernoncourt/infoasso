<template>
  <div class="updateSchedule-container">
    <TheSidebar :userEmail="userEmail" />

    <div class="updateSchedule-main">

      <TheHeader title="Tableau de bord" subtitle="Modifier l'horaire" />

      <div class="form-wrapper">
        <div v-if="!initialData">Chargement des données...</div>
        <ScheduleForm
        v-if="initialData"
        :initialData="initialData"
        :is-submitting="isSubmitting"
        :errors="backendErrors"
          submit-button-text="Modifier"

          @submit="handleUpdateSchedule"
          @cancel="handleCancel" />
      </div>
    </div>
  </div>
</template>




<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import TheSidebar from '@/components/layout/TheSidebar.vue';
import ScheduleForm from '@/components/schedules/ScheduleForm.vue';
import TheHeader from '@/components/layout/TheHeader.vue';
import schedulesService from '@/services/schedules.service';
import assoService from '@/services/asso.service';

const router = useRouter();
const route = useRoute();

const isSubmitting = ref(false);
const backendErrors = ref({});

const initialData = ref(null);

const handleCancel = () => {
  router.push("/dashboard");
}

onMounted(async () => {
  try {
    const assoId = route.params.id;
    const scheduleId = route.params.scheduleId;

    // 1. Vérification de sécurité prioritaire
    const assoData = await assoService.getById(assoId);
    const userEmail = localStorage.getItem("user_email");

    if (assoData.ownerEmail !== userEmail) {
      alert("Vous n'êtes pas autorisé à modifier cette activité !");
      router.push({ name: 'dashboard' });
      return;
    }

    // 2. Si autorisé, on charge les données du schedule
    const response = await schedulesService.getById(assoId, scheduleId);
    initialData.value = response;

  } catch (error) {
    console.error("Erreur chargement : ", error);
    alert("Impossible de charger les données.");
    router.push({ name: 'dashboard' });
  }
});

const handleUpdateSchedule = async (formData) => {
  try {
    isSubmitting.value = true;

    const assoId = route.params.id;

    const scheduleId = route.params.scheduleId;

    const payload = {
      ...formData,
      associationId: Number(route.params.id),
    }
    console.log(assoId, scheduleId, payload);
    await schedulesService.update(assoId, scheduleId, payload);

    alert("L'horaire a été modifié avec succès");
    router.push(`/association/${route.params.id}/scheduleView`);
  } catch (error) {
    console.error("Erreur lors de la modification de l'horaire: ", error);
    if (error.response && error.response.status === 400) {
      backendErrors.value
        = error.response.data;
    } else {
      alert("Une erreur inattendue est survenue.");
    }
  } finally {
    isSubmitting.value = false;
  }
}

</script>

<style scoped>
.updateSchedule-container {
  display: flex;
  min-height: 100vh;
}

.updateSchedule-main {
  flex: 1;
  padding: 2rem;
  background-color: #f4f7f6;
  overflow-y: auto;
}

.form-wrapper {
  max-width: 800px;
  margin: 2rem auto;
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
</style>
