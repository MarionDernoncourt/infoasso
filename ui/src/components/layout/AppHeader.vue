<template>
<nav class="navbar">
  <div class="nav-container">
    <router-link to="/" class="logo">INFO ASSO</router-link>

      <div class="nav-links">
        <router-link to="/search">Recherche</router-link>
        <router-link to="/register" class="btn-register">Inscription Asso</router-link>
      </div>
  </div>
</nav>
  </template>


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
}


</style>
