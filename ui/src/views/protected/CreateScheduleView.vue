<template>
  <div class="createSchedule-container" role="main" aria-label="Page d'ajout d'un horaire d'activité">
    <TheSidebar  />

    <div class="createSchedule-main">

      <TheHeader title="Tableau de bord" subtitle="" />

      <div class="form-wrapper">
        <ScheduleForm
        submit-button-text="Ajouter"
        :is-submitting="isSubmitting"
        :errors="backendErrors"

        @submit="handleCreateSchedule"
        @cancel="handleCancel" />
      </div>
    </div>
  </div>
</template>




<script setup>
import { onMounted, ref } from 'vue';
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

onMounted(async () => {
  try {
    const data = await assoService.getById(route.params.id);
    const userEmail = localStorage.getItem("user_email");

    if(data.ownerEmail !== userEmail) {
      alert("Vous n'êtes pas autorisé a créer des activités !")
      router.push({name : 'dashboard'});
      return;
    }
  } catch (err) {
    console.error("Erreur: ", err);
    router.push({ name: 'dashboard'});
  }
});

const handleCancel = () => {
  router.push("/dashboard");
}

const handleCreateSchedule = async (formData) => {
  try {
    isSubmitting.value = true;

  const assoId = route.params.id;

  const payload = {
    ...formData,
    associationId : Number(route.params.id),
  }
    await schedulesService.create(assoId, payload);

    alert("L'horaire a été créé avec succès");
    router.push(`/association/${route.params.id}/scheduleView`);
  } catch (error) {
    console.error("Erreur lors de la création de l'horaire: ", error);
    if (error.response && error.response.status === 400) {
      backendErrors.value
        = error.response.data;
    } else {
      alert("Une erreur inattendue est survenue.");
    }
  } finally{
    isSubmitting.value = false;
  }
}

</script>

<style scoped>
.createSchedule-container {
  display: flex;
  min-height: 100vh; /* Prend toute la hauteur de l'écran */
}

.createSchedule-main {
  flex: 1; /* Prend tout l'espace restant à droite de la sidebar */
  background-color: #f4f7f6; /* Couleur de fond légère */
  overflow-y: auto;
  padding-top: 20px;
}

.form-wrapper {
  max-width: 800px; /* Limite la largeur pour ne pas étirer le formulaire */
  margin: 2rem auto; /* Centre le formulaire */
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
</style>
