<template>
  <div class="updateAsso-container" role="main" aria-label="Page de mdodification de la fiche assocation">
    <TheSidebar :userEmail="userEmail" />

    <div class="updateAsso-main">
      <TheHeader title="Tableau de bord" subtitle="" />

      <div class="form-wrapper">
        <AssoForm v-if="asso" :initialData="asso" submit-button-text="Enregistrer les modifications"
          :is-submitting="isSubmitting" :errors="backendErrors" :key="asso.id" form-title="Modifier la fiche"

          @back="handleBackToDashboard" @submit="handleUpdateAsso" @cancel="handleCancel" />
        <div v-else class="loading-state" role="status" aria-live="polite">
          <p>Chargement des informations de votre structure...</p>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import TheSidebar from '@/components/layout/TheSidebar.vue';
import TheHeader from '@/components/layout/TheHeader.vue';
import AssoForm from '@/components/asso/AssoForm.vue';
import { useRouter, useRoute } from "vue-router"
import { ref, onMounted } from "vue"
import assoService from '@/services/asso.service';


const router = useRouter();
const route = useRoute();

const isSubmitting = ref(false);
const backendErrors = ref({});
const asso = ref(null);
const userEmail = ref('Mon Compte');

// APPEL AU BACK LORS DU CHARGEMENT DE LA PAGE //
onMounted(async () => {
  try {

    const assoId = route.params.id; // Récupère l'id depuis l'URL
    const response = await assoService.getById(assoId);
    const userStockedEmail = localStorage.getItem("user_email");

    if (response.ownerEmail !== userStockedEmail) {
      alert("Vous n'êtes pas autorisé à modifier cette association.");
      router.push("/dashboard");
    }

    asso.value = response;

    if (asso.value?.ownerEmail) {
      userEmail.value = asso.value.ownerEmail;
    }

  } catch (error) {
    console.error("Erreur lors de la récupération de l'association : ", error);
    alert("Impossible de charger les informations de cette association.");
    router.push("/dashboard");
  }
})

// Envoie du formulaire modifié
const handleUpdateAsso = async (updateData) => {
  try {
    isSubmitting.value = true;
    backendErrors.value = {};

    const assoId = route.params.id;

    await assoService.update(assoId, updateData);

    alert("Association modifiée avec succès !");
    router.push("/dashboard");

  } catch (error) {
    console.error("Erreur lors de la modification : ", error);
    if (error.response && error.response.status === 400) {
      backendErrors.value = error.response.data;
    } else {
      alert("Une erreur inattendue est survenue.");
    }
  } finally {
    isSubmitting.value = false;
  }
}
const handleBackToDashboard = () => {
  router.push("/dashboard");
}
const handleCancel = () => {
  router.push("/dashboard");
}
</script>

<style scoped>
/* 🌍 Layout principal */
.updateAsso-container {
  display: flex;
  min-height: 100vh;
  background-color: #f8fafc;
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
}

/* 🚀 Zone de contenu à droite de la Sidebar */
.updateAsso-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  padding: var(--header-height) 20px;
}

/* 📦 Conteneur du formulaire */
.form-wrapper {
  flex: 1;
  padding: 2rem;
  max-width: 850px;
  width: 100%;
  margin: 0 auto;
}

/* ⏳ État de chargement (Spinner) */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  padding: 3rem;
}

.loading-state p {
  color: #64748b;
  font-size: 1rem;
  font-weight: 500;
  margin: 0;
  animation: pulse 1.5s infinite ease-in-out;
}

@keyframes pulse {

  0%,
  100% {
    opacity: 0.6;
  }

  50% {
    opacity: 1;
  }
}

@media (max-width: 768px) {
  .updateAsso-container {
    flex-direction: column;
  }

  .form-wrapper {
    padding: 1rem;
  }
}
</style>
