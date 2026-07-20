<template>
  <div class="createAsso-container">
    <TheSidebar :userEmail="userEmail" />

    <div class="createAsso-main">

      <TheHeader title="Tableau de bord" subtitle="" />

      <div class="form-wrapper">
        <AssoForm
        submit-button-text="Créer la fiche de votre assocation"
        :is-submitting="isSubmitting"
          :errors="backendErrors"
          form-title="Créer une nouvelle association"
          @submit="handleCreateAsso"
          @cancel="handleCancel" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import TheSidebar from '@/components/layout/TheSidebar.vue';
import TheHeader from '@/components/layout/TheHeader.vue';
import AssoForm from '@/components/asso/AssoForm.vue';
import { useRouter } from 'vue-router'
import assoService from '@/services/asso.service';

const router = useRouter();
const isSubmitting = ref(false);
const backendErrors = ref({});

const handleCancel = () => {
  router.push('/dashboard');
}

const handleCreateAsso = async (formData) => {
  try {
    isSubmitting.value = true;

    await assoService.create(formData);

    alert("Association crée avec succès !");
    router.push("/dashboard");

  } catch (error) {
    console.error("Erreur lors de la création : ", error);
    if (error.response && error.response.status === 400) {
      backendErrors.value = error.response.data;
    } else {
      alert("Une erreur inattendue est survenue.");

    }
  } finally {
    isSubmitting.value = false;
  }


}
</script>
<style scoped>
.createAsso-container {
  display: flex;
  width: 100%;
  min-height: 100vh;
  background-color: #faf8f6;
}

.createAsso-main {
  flex: 1;
  padding: 40px;
  padding-left: calc(260px + 40px);
  /* Place pour la Sidebar */

  display: flex;
  flex-direction: column;
  gap: 32px;
  box-sizing: border-box;

  /* 💡 On retire le "align-items: center".
     Par défaut, le Header va donc s'aligner sagement tout à gauche. */
}

.form-wrapper {
  max-width: 1000px;
  width: 100%;

  /* ✨ LA MAGIE EST ICI ✨
     En mettant 'margin: 0 auto', on dit au formulaire :
     "Prends tout l'espace disponible à gauche et à droite pour te centrer tout seul" */
  margin: 0 auto;
}

/* 📱 Version mobile / tablette */
@media (max-width: 768px) {
  .createAsso-container {
    flex-direction: column;
  }

  .createAsso-main {
    padding: 20px;
  }

  .form-wrapper {
    margin: 0;
    /* On retire le centrage sur mobile pour prendre toute la largeur */
  }
}
</style>
