<template>
  <div class="createSchedule-container">
    <TheSidebar :userEmail="userEmail" />

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
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import TheSidebar from '@/components/layout/TheSidebar.vue';
import ScheduleForm from '@/components/schedules/ScheduleForm.vue';
import TheHeader from '@/components/layout/TheHeader.vue';
import schedulesService from '@/services/schedules.service';

const router = useRouter();
const route = useRoute();

const isSubmitting = ref(false);
const backendErrors = ref({});

const handleCancel = () => {
  router.push("/dashboard");
}

const handleCreateSchedule = async (formData) => {
  try {
    isSubmitting.value = true;

  const payload = {
    ...formData,
    associationId : route.params.id,
  }

    await schedulesService.create(payload);

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
