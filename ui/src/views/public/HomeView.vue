<template>
  <div class="main-container" role="main" aria-label="Page d'accueil et de recherche d'activités">
    <div class="home-wrapper">

      <!-- COLONNE GAUCHE -->
      <div class="info-sidebar" aria-label="Informations sur la plateforme">
        <h2>INFO ASSO</h2>
        <p class="intro-text">
          La plateforme de référence pour découvrir la vie associative qui vous entoure.
        </p>

        <div class="audience-section citizen-section">
          <h3>Pour les Citoyens 🏙️</h3>
          <p class="section-intro">
            <strong>Trouvez votre activité idéale !</strong> Utilisez les filtres pour explorer les associations et les plannings près de chez vous en quelques clics.
          </p>
        </div>

        <div class="audience-section asso-section">
          <h3>Pour les Associations 📢</h3>
          <p class="section-intro">
            <strong>Responsable d'association ?</strong> Pilotez votre page et boostez votre visibilité locale.
          </p>
          <ul class="audience-features">
            <li>✨ <strong>Visibilité maximale :</strong> Présentez vos activités aux habitants.</li>
            <li>📅 <strong>Planning en ligne :</strong> Partagez vos horaires en temps réel.</li>
          </ul>
        </div>

        <div class="cta-section">
          <p class="login-prompt">
            Vous gérez une structure ?
            <router-link to="/login" class="inline-link">Identifiez-vous ici</router-link>
            ou <router-link to="/register" class="inline-link">créez un compte</router-link>.
          </p>
        </div>
      </div>

      <!-- COLONNE DROITE : La Recherche -->
      <div class="search-container-card">
        <div class="form-header-badge" role="status">Recherche rapide</div>
        <h3>Trouvez une activité</h3>
        <p class="form-subtitle">Filtrez par ville, âge ou type d'activité pour lancer votre recherche.</p>

        <!-- Conteneur qui cible ton SearchFilterBar et réorganise ses éléments en mode grille/ligne -->
        <div class="home-search-wrapper" aria-label="Zone de filtres de recherche">
          <SearchFilterBar />
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import SearchFilterBar from '@/components/asso/SearchFilterBar.vue';
</script>

<style scoped>
/* --- CONTENEUR PRINCIPAL & LAYOUT --- */
.main-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 70px);
  background-color: darksalmon;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  padding: 40px 20px;
  box-sizing: border-box;
}

.home-wrapper {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  justify-content: space-between;
  max-width: 1100px;
  width: 100%;
  gap: 50px;
}

/* --- COLONNE INFOS (GAUCHE) --- */
.info-sidebar {
  flex: 5;
  color: #2c1a14;
  text-align: left;
}

.info-sidebar h2 {
  font-size: 2.8rem;
  font-weight: 900;
  margin: 0 0 10px 0;
  letter-spacing: -1px;
}

.intro-text {
  font-size: 1.1rem;
  line-height: 1.5;
  margin-bottom: 25px;
  opacity: 0.9;
}

.audience-section h3 {
  font-size: 1.2rem;
  margin-top: 0;
  margin-bottom: 10px;
  color: #2c1a14;
  border-bottom: 2px solid rgba(44, 26, 20, 0.1);
  padding-bottom: 4px;
  display: inline-block;
}

.section-intro {
  font-size: 0.95rem;
  margin-bottom: 10px;
  line-height: 1.4;
}

.audience-features {
  margin: 0;
  padding-left: 0;
  list-style: none;
  font-size: 0.9rem;
  display: flex;
  flex-direction: column;
  gap: 6px;
  line-height: 1.4;
}

.citizen-section {
  margin-bottom: 25px;
}

.asso-section {
  background-color: rgba(255, 255, 255, 0.25);
  padding: 18px;
  border-radius: 12px;
}

.cta-section {
  border-top: 1px solid rgba(44, 26, 20, 0.15);
  padding-top: 15px;
  font-size: 0.9rem;
  margin-top: 25px;
}

.inline-link {
  color: #2c1a14;
  font-weight: bold;
  text-decoration: underline;
}

.inline-link:hover {
  opacity: 0.8;
}

/* --- COLONNE RECHERCHE (DROITE) --- */
.search-container-card {
  flex: 5;
  background-color: white;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
  width: 100%;
}

.form-header-badge {
  display: inline-block;
  background-color: #fef1ec;
  color: darksalmon;
  font-size: 0.75rem;
  font-weight: bold;
  padding: 6px 12px;
  border-radius: 20px;
  margin-bottom: 10px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.search-container-card h3 {
  margin: 0 0 5px 0;
  color: #2c1a14;
  font-size: 1.6rem;
  text-align: left;
}

.form-subtitle {
  font-size: 0.85rem;
  color: #777;
  margin: 0 0 25px 0;
  line-height: 1.3;
  text-align: left;
}

/* 🎯 ADAPTATION DU COMPOSANT SEARCHFILTERBAR ICI UNIQUEMENT */
.home-search-wrapper :deep(.search-filters) {
  display: flex;
  flex-direction: column; /* Sur la HomeView, on empile les champs joliment en colonne dans la carte de droite pour que ça respire */
  gap: 12px;
  padding: 0;             /* On enlève le padding blanc interne du composant puisqu'il est déjà dans .search-container-card */
  background: transparent;
  box-shadow: none;
}

.home-search-wrapper :deep(input),
.home-search-wrapper :deep(select) {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 0.95rem;
  background-color: #fafafa;
}

.home-search-wrapper :deep(input:focus),
.home-search-wrapper :deep(select:focus) {
  outline: none;
  border-color: darksalmon;
  background-color: white;
}

.home-search-wrapper :deep(button) {
  width: 100%;
  padding: 12px;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  border: none;
}

/* Bouton Rechercher */
.home-search-wrapper :deep(button:first-of-type) {
  background-color: darksalmon;
  color: white;
}

.home-search-wrapper :deep(button:first-of-type):hover {
  background-color: #e38466;
}

/* Bouton Réinitialiser */
.home-search-wrapper :deep(button:last-child) {
  background-color: #f3f4f6;
  color: #4b5563;
}

.home-search-wrapper :deep(button:last-child):hover {
  background-color: #e5e7eb;
}

/* --- RESPONSIVE MOBILE --- */
@media (max-width: 850px) {
  .home-wrapper {
    flex-direction: column;
    gap: 40px;
  }
  .info-sidebar {
    text-align: center;
  }
  .cta-section {
    text-align: center;
  }
}
</style>
