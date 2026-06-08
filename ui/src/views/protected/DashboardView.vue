<template>
  <div class="dashboard-container">

    <TheSidebar :userEmail="userEmail" />

    <main class="dashboard-main">

      <TheHeader title="Tableau de bord" subtitle="Pilotez les informations et les créneaux de votre structure"
        :show-add-button="hasAssociation" />
      <div v-if="isLoading" class="loading-state">
        <div class="spinner"></div>
        <p>Connexion sécurisée à vos structures en cours...</p>
      </div>

      <div v-if="!hasAssociation" class="empty-state-card">
        <div class="empty-icon">🏢</div>
        <h3>Vous n'avez pas encore enregistré d'association</h3>
        <p>Pour commencer à proposer vos horaires et à être visible par les citoyens, créer votre fiche association</p>
        <router-link to="/association/create" class="create-btn-trigger">
          + Créer la fiche de mon association
        </router-link>
      </div>

      <div v-else class="dashboard-grid">

        <div v-if="associationsList.length > 1" class="multi-asso-selector">
          <label for="asso-select">Changer d'association</label>
          <select id="asso-select" v-model="selectedAsso">
            <option v-for="asso in associationsList" :key="asso.id" :value="asso">
              {{ asso.displayName }}
            </option>
          </select>
        </div>

        <section class="info-card">
          <div class="card-header">
            <h3>Informations Générales</h3>
            <button class="edit-btn">Modifier la fiche</button>
          </div>
          <div class="card-body">
            <div class="asso-profile">
              <h4> {{ selectedAsso?.displayName }}</h4>
              <span class="category-badge">
                🏷️ {{ selectedAsso?.category?.name || 'Association' }}
              </span>
              <p class="asso-official-name">Nom officiel: {{ selectedAsso?.officialName }}</p>
              <p class="asso-rna">RNA : {{ selectedAsso?.rnaNumber }}</p>
            </div>
          </div>

          <hr class="divider">
          <div class="asso-details">
            <p><strong>Adresse :</strong>{{ selectedAsso?.streetAddress }}, {{ selectedAsso?.zipCode }}, {{
              selectedAsso?.city }}</p>
            <p><strong>Téléphone :</strong> {{ selectedAsso?.phoneNumber || 'Non renseigné' }}</p>
            <p><strong>Email public :</strong> {{ selectedAsso?.email }}</p>
            <p v-if="selectedAsso?.website"><strong>Site web :</strong><a :href="selectedAsso.website"
                target="_blank">{{ selectedAsso?.website }}</a></p>
            <p class="asso-description"><strong>Description :</strong>{{ selectedAsso?.description || "Aucune description pour le moment."}}</p>
          </div>
        </section>

        <section class="status-card">
          <h3>🛡️ Statut de la structure</h3>
          <div class="status-indicators">
            <div class="status-item" :class="{ 'status-on': selectedAsso?.isPublished }">
              <span class="indicator-dot"></span>
              <span class="status-label">Visibilité : {{ selectedAsso?.isPublished ? 'En ligne (Visible du public)' : 'Hors ligne' }}</span>
            </div>
            <div class="status-item" :class="{ 'status-verified': selectedAsso?.isVerified }">
              <span class="indicator-badge">✓</span>
              <span class="status-label">{{ selectedAsso?.isVerified ? 'Fiche Certifiée (Pastille bleue)' :   'Vérification en cours' }}</span>
            </div>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>


<script setup>
import { ref, onMounted } from 'vue';
import TheHeader from '@/components/layout/TheHeader.vue'
import TheSidebar from '@/components/layout/TheSidebar.vue';
import assoService from '@/services/asso.service'

// STATE REACTIFS //
const associationsList = ref([]);
const selectedAsso = ref(null);
const hasAssociation = ref(false);
const isLoading = ref(true);
const userEmail = ref("");

// - APPEL AU BACK LORS DU CHARGEMENT DE LA PAGE - //
onMounted(async () => {
  try {
    isLoading.value = true;
    const data = await assoService.getMyAssociations();

    if (data && data.length > 0) {
      associationsList.value = data;
      selectedAsso.value = data[0];
      hasAssociation.value = true;
      userEmail.value = data[0].owner?.email || 'Mon compte';
    }
    else {
      hasAssociation.value = false;
      userEmail.value = localStorage.getItem("userEmail") || 'Mon Compte';
    }
  } catch (error) {
    console.error("Erreur lors du chargement du dashboard: ", error);
    hasAssociation.value = false;
  } finally {
    isLoading.value = false;
  }
})

</script>

<style scoped>
/* --- 1️⃣ STRUCTURE GLOBALE (Layout) --- */
.dashboard-container {
  display: flex;
  min-height: 100vh;
  background-color: #fdfaf8; /* Un fond très légèrement chaud/crème pour le confort visuel */
  font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.dashboard-main {
  flex: 1;
  margin-left: 260px; /* Crucial : repousse le contenu pour laisser la place à la Sidebar fixe */
  padding: 40px;
  box-sizing: border-box;
  max-width: 1400px; /* Évite que le contenu s'étale trop sur les écrans géants */
}

/* --- 2️⃣ ÉTATS INTERMÉDIAIRES (Chargement & Vide) --- */
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
  to { transform: rotate(360deg); }
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

.empty-state-card h3 {
  color: #2c1a14;
  font-size: 1.4rem;
  margin-bottom: 12px;
  font-weight: 700;
}

.empty-state-card p {
  color: #6c5a53;
  margin-bottom: 30px;
  line-height: 1.6;
}

.create-btn-trigger {
  background-color: darksalmon;
  color: white;
  text-decoration: none;
  padding: 14px 28px;
  border-radius: 8px;
  font-weight: 700;
  box-shadow: 0 4px 14px rgba(233, 150, 122, 0.4);
  transition: all 0.2s ease;
  display: inline-block;
}

.create-btn-trigger:hover {
  background-color: #e38466;
  transform: translateY(-2px);
}

/* --- 3️⃣ GRILLE DES CARTES (Dashboard Actif) --- */
.dashboard-grid {
  display: grid;
  grid-template-columns: 2fr 1fr; /* 2/3 pour les infos, 1/3 pour le statut */
  gap: 30px;
  align-items: start;
}

.multi-asso-selector {
  grid-column: 1 / -1; /* Aligné tout en haut sur toute la largeur */
  background: #fdf0eb;
  padding: 15px 20px;
  border-radius: 10px;
  border: 1px solid rgba(233, 150, 122, 0.2);
  display: flex;
  align-items: center;
  gap: 15px;
  font-weight: 700;
  color: #2c1a14;
}

.multi-asso-selector select {
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid #dcd0cc;
  background-color: white;
  font-family: inherit;
  font-size: 0.95rem;
  color: #2c1a14;
  cursor: pointer;
  outline: none;
}

.multi-asso-selector select:focus {
  border-color: darksalmon;
}

/* --- 4️⃣ STYLE DES CARTES (Sections) --- */
section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(44, 26, 20, 0.02);
  border: 1px solid #f5ebe7;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.card-header h3 {
  margin: 0;
  color: #2c1a14;
  font-size: 1.25rem;
  font-weight: 800;
}

.edit-btn {
  background: white;
  border: 1px solid darksalmon;
  color: darksalmon;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s ease;
}

.edit-btn:hover {
  background: #fdf0eb;
  transform: translateY(-1px);
}

/* --- 5️⃣ PROFIL & INFOS DE L'ASSOCIATION --- */
.asso-profile h4 {
  margin: 0 0 8px 0;
  font-size: 1.6rem;
  font-weight: 800;
  color: #2c1a14;
}

.category-badge {
  display: inline-block;
  background-color: #fdf0eb;
  color: darksalmon;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 700;
  margin-bottom: 15px;
  border: 1px solid rgba(233, 150, 122, 0.15);
}

.asso-official-name, .asso-rna {
  margin: 4px 0;
  font-size: 0.9rem;
  color: #7c6a63;
}

.divider {
  border: 0;
  border-top: 1px solid #f5ebe7;
  margin: 25px 0;
}

.asso-details p {
  margin: 14px 0;
  color: #4a3b35;
  font-size: 0.95rem;
  line-height: 1.5;
}

.asso-details strong {
  color: #2c1a14;
  font-weight: 600;
}

.asso-details a {
  color: darksalmon;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
}

.asso-details a:hover {
  text-decoration: underline;
}

.asso-description {
  background: #fdfbfb;
  padding: 16px;
  border-left: 4px solid darksalmon;
  border-radius: 0 8px 8px 0;
  font-style: italic;
  color: #5c4b44 !important;
  margin-top: 20px !important;
}

/* --- 6️⃣ BLOC STATUT DE VISIBILITÉ --- */
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
  color: #7c6a63;
  font-weight: 500;
}

.indicator-dot {
  width: 10px;
  height: 10px;
  background: #dcd0cc;
  border-radius: 50%;
  transition: all 0.3s ease;
}

/* Style quand l'asso est en ligne (Vert) */
.status-on {
  background: #f4faf4;
  border-color: #d5ebd5;
  color: #2b542c;
}
.status-on .indicator-dot {
  background: #4cae4c;
  box-shadow: 0 0 8px rgba(76, 174, 76, 0.6);
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
  font-weight: bold;
}

/* Style quand l'asso est vérifiée (Bleu) */
.status-verified {
  background: #f4f8fe;
  border-color: #d6e4f7;
  color: #1d4473;
}
.status-verified .indicator-badge {
  background: #0275d8;
  box-shadow: 0 2px 6px rgba(2, 117, 216, 0.3);
}
</style>
