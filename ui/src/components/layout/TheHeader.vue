<template>
  <header class="main-header">
    <div class="header-title">
      <h1>{{ title }}</h1>
      <p class="subtitle">{{ subtitle }}</p>
    </div>

    <router-link v-if="showAddButton" to="/association/create" class="add-asso-btn">
      <span>＋</span> Enregistrer une structure
    </router-link>
  </header>
</template>

<script setup>
// On définit des "props" pour pouvoir changer le texte selon la page
defineProps({
  title: { type: String, default: 'Espace Gestion' },
  subtitle: { type: String, default: 'Pilotez votre structure' },
  showAddButton: { type: Boolean, default: false } // Masqué par défaut
});
</script>

<style scoped>
.main-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  border-bottom: 1px solid #eee;
  padding-bottom: 20px;
}

.main-header h1 {
  margin: 0;
  color: #2c1a14;
  font-size: 1.8rem;
  font-weight: 800;
  text-align: left;
}

.subtitle {
  margin: 5px 0 0 0;
  color: #666;
  font-size: 0.95rem;
  text-align: left;
}

.add-asso-btn {
  background-color: darksalmon;
  color: white;
  text-decoration: none;
  padding: 10px 18px;
  border-radius: 8px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(233, 150, 122, 0.3);
  transition: all 0.2s;
  font-size: 0.9rem;
}

.add-asso-btn:hover {
  background-color: #e38466;
  transform: translateY(-1px);
}
</style>
