<template>
  <div class="dashboard-container" role="main" aria-label="Page de tableau de bord du responsable d'une association">
    <TheSidebar :userEmail="userEmail" />

    <main class="dashboard-main">
      <TheHeader title="Tableau de bord" subtitle="Pilotez les informations et les créneaux de votre structure"
        :show-add-button="hasAssociation" />

      <div v-if="isLoading" class="loading-state" role="status" aria-live="polite">
        <div class="spinner"></div>
        <p>Connexion sécurisée à vos structures en cours...</p>
      </div>

      <div v-if="!isLoading && !hasAssociation" class="empty-state-card">
        <div class="empty-icon" aria-hidden="true">🏢</div>
        <h3>Vous n'avez pas encore enregistré d'association</h3>
        <p>Pour commencer à proposer vos horaires et à être visible par les citoyens, créer votre fiche association</p>
        <router-link to="/association/create" class="create-btn-trigger" aria-label="Créer la fiche de mon association">
          + Créer la fiche de mon association
        </router-link>
      </div>

      <div v-if="!isLoading && hasAssociation" class="dashboard-grid">

        <div v-if="associationsList.length > 1" class="multi-asso-selector">
          <label for="asso-select">Changer d'association</label>
          <select id="asso-select" v-model="selectedAsso" aria-label="Sélectionner l'association à administrer">
            <option v-for="asso in associationsList" :key="asso.id" :value="asso">
              {{ asso.displayName }}
            </option>
          </select>
        </div>

        <AssoCard :asso="selectedAsso">
          <template #actions>
            <div class="header-btn">
              <button
              type="button"
              class="schedule-btn"
              @click="goToSchedulePage(selectedAsso?.id)"
              aria-label="Accéder au planning de l'association"
              >Accéder le
                planning</button>
              <button
              type="button"
              class="edit-btn"
              @click="goToUpdatePage(selectedAsso?.id)"
              aria-label="Modifier la fiche de l'association"
              >Modifier la
                fiche</button>
            </div>
          </template>
        </AssoCard>
        <!--  A AJOUTER A LA V2 !!!!!

        <section class="status-card">
          <h3>🛡️ Statut de la structure</h3>
          <div class="status-indicators">
            <div class="status-item" :class="{ 'status-on': selectedAsso?.isPublished }">
              <span class="indicator-dot"></span>
              <span class="status-label">Visibilité : {{ selectedAsso?.isPublished ? 'En ligne (Visible du public)' : 'Hors ligne' }}</span>
            </div>
            <div class="status-item" :class="{ 'status-verified': selectedAsso?.isVerified }">
              <span class="indicator-badge">✓</span>
              <span class="status-label">{{ selectedAsso?.isVerified ? 'Fiche Certifiée (Pastille bleue)' : 'Vérification en cours' }}</span>
            </div>
          </div>
        </section>
        -->

      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from "vue-router"
import TheHeader from '@/components/layout/TheHeader.vue'
import TheSidebar from '@/components/layout/TheSidebar.vue';
import AssoCard from '@/components/asso/AssoCard.vue';
import assoService from '@/services/asso.service'

const router = useRouter();
const associationsList = ref([]);
const selectedAsso = ref(null);
const hasAssociation = ref(false);
const isLoading = ref(true);
const userEmail = ref("");

onMounted(async () => {
  try {
    isLoading.value = true;
    const data = await assoService.getMyAssociations();

    if (data && data.length > 0) {
      associationsList.value = data;
      selectedAsso.value = data[0];
      hasAssociation.value = true;
      userEmail.value = data[0].owner?.email || 'Mon compte';
    } else {
      hasAssociation.value = false;
      userEmail.value = localStorage.getItem("user_email") || 'Mon Compte';
    }
  } catch (error) {
    console.error("Erreur lors du chargement du dashboard: ", error);
    hasAssociation.value = false;
    if (error.response?.status === 401 || error.response?.status === 403) {
      localStorage.clear();
      router.push('/login');
    }
  } finally {
    isLoading.value = false;
  }
})

const goToUpdatePage = (id) => {
  if (!id) return;
  router.push(`/association/update/${id}`);
}

const goToSchedulePage = (id) => {
  if (!id) return;
  router.push(`/association/${id}/scheduleView`);
}
</script>

<style scoped>
.dashboard-container {
  display: flex;
  min-height: 100vh;
  background-color: #fdfaf8;
  font-family: system-ui, sans-serif;
}

.dashboard-main {
  flex: 1;
  padding: 40px;
  box-sizing: border-box;
  max-width: 1400px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
  color: #666;
}

.spinner {
  width: 45px;
  height: 45px;
  border: 4px solid rgba(233, 150, 122, 0.2);
  border-top-color: darksalmon;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.empty-state-card {
  background: white;
  border-radius: 16px;
  padding: 60px 40px;
  text-align: center;
  max-width: 550px;
  margin: 60px auto;
  box-shadow: 0 4px 20px rgba(44, 26, 20, 0.04);
  border: 1px solid #f0e6e2;
}

.empty-icon {
  font-size: 3.5rem;
  margin-bottom: 20px;
}

.create-btn-trigger {
  background-color: darksalmon;
  color: white;
  text-decoration: none;
  padding: 14px 28px;
  border-radius: 8px;
  font-weight: 700;
  transition: all 0.2s ease;
  display: inline-block;
}

.create-btn-trigger:hover {
  background-color: #e38466;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
  align-items: start;
  padding-left: calc(var(--sidebar-width) + 20px);
}

.multi-asso-selector {
  grid-column: 1 / -1;
  background: #fdf0eb;
  padding: 15px 20px;
  border-radius: 10px;
  border: 1px solid rgba(233, 150, 122, 0.2);
  display: flex;
  align-items: center;
  gap: 15px;
  font-weight: 700;
}

section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(44, 26, 20, 0.02);
  border: 1px solid #f5ebe7;
}

/* Boutons spécifiques au dashboard (injectés via slot) */
.header-btn {
  display: flex;
  gap: 10px;
}



.status-indicators {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-top: 10px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  border-radius: 10px;
  background: #fdfcfc;
  border: 1px solid #f7f1ef;
  font-size: 0.9rem;
  font-weight: 500;
}

.indicator-dot {
  width: 10px;
  height: 10px;
  background: #dcd0cc;
  border-radius: 50%;
}

.status-on {
  background: #f4faf4;
  border-color: #d5ebd5;
  color: #2b542c;
}

.status-on .indicator-dot {
  background: #4cae4c;
}

.indicator-badge {
  width: 20px;
  height: 20px;
  background: #dcd0cc;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
}

.status-verified {
  background: #f4f8fe;
  border-color: #d6e4f7;
  color: #1d4473;
}

.status-verified .indicator-badge {
  background: #0275d8;
}
</style>
