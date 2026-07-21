
<template>
  <div id="app">
    <!-- On n'affiche le header que si la route ne demande pas de le cacher -->
    <AppHeader v-if="!route.meta.hideHeader" />

    <main :class="['main-content', { 'full-width': route.meta.hideHeader }]">
      <router-view />
    </main>
  </div>
</template>


<script setup>
import { useRoute, RouterView } from 'vue-router'
import AppHeader from './components/layout/AppHeader.vue';

const route = useRoute();
</script>

<style>
/* Variable globale */
:root {
  --header-height: 70px;
  --sidebar-width: 260px;
  --primary-color: darksalmon ;
  --text-main: #2c1a14;
}
/* Variables globales pour les boutons */
button:not(.cancel-btn) {
  background: white;
  border: 1px solid darksalmon;
  color: darksalmon;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s ease;
}

button:not(.cancel-btn):hover {
  background: #fdf0eb;
}


body {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.main-content {
  padding-top: var(--header-height);
  /* Par défaut, pas de marge à gauche */
  padding-left: 0;
  transition: padding 0.3s ease; /* Pour un effet fluide */
}

/* Cas où on veut la sidebar (ex: page recherche) */
.main-content.with-sidebar {
  padding-left: var(--sidebar-width);
}

/* Cas où on est sur Login/Register (pas de header, pas de sidebar) */
.main-content.full-width {
  padding-top: 0;
  padding-left: 0;
}
</style>
