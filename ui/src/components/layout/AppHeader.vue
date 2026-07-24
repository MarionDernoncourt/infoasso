<template>
<nav class="navbar" aria-label="Navigation principale">
  <div class="nav-container">
    <router-link to="/" class="logo" aria-label="Info Asso, retour à l'accueil">INFO ASSO</router-link>

    <div class="nav-links">
      <!-- CAS 1 : Utilisateur CONNECTÉ -->
      <template v-if="isLoggedIn">
        <router-link to="/dashboard" aria-label="Accéder au tableau de bord">Tableau de bord</router-link>
<a href="#" @click.prevent="openAccountModal" aria-label="Gérer mon compte utilisateur">Mon compte</a>      </template>

      <!-- CAS 2 : Visiteur / Citoyen NON CONNECTÉ -->
      <template v-else>
        <router-link to="/search" aria-label="Rechercher une association ou une activité">Recherche</router-link>
        <router-link to="/register" class="btn-register" aria-label="Inscrire une nouvelle association">Inscription Asso</router-link>
      </template>
    </div>
  </div>
</nav>
<AccountModal v-if="isModalOpen" @close="closeAccountModal" />
</template>

<script setup>
import { ref, onMounted } from 'vue';
import AccountModal from '../account/AccountModal.vue';

const isLoggedIn = ref(false);

const isModalOpen = ref(false);

function openAccountModal() {
  isModalOpen.value = true;
}

function closeAccountModal() {
  isModalOpen.value = false;
}

onMounted(() => {
  // Vérifie si un token est présent dans le localStorage
  const token = localStorage.getItem('token');
  isLoggedIn.value = !!token;
});
</script>

<style scoped>
.navbar {
  padding: 15px 0;
  border-bottom: 1px solid #f5ebe7;
  background: var(--text-main);
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 40px; /* Donne une hauteur fixe majorée de 15px + 15px de padding en var app.vue*/
  z-index: 1000; /* S'assure qu'il passe au-dessus de tout */
}

.nav-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 100%;
}

.logo {
  font-size: 1.5rem;
  font-weight: 800;
  color: white;
}


.nav-links {
  display: flex;
  gap: 20px;
  align-items: center;
}

a {
  text-decoration: none;
  color: var(--primary-color);
  font-weight: 500;
  font-size: 1.5rem;
}
a:hover {
  text-decoration: none;
}

</style>
