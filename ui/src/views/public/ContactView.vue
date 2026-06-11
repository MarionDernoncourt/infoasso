
<template>
  <div class="contact-container">

  <TheSidebar />
<main class="contact-main">
      <h1 class="page-title">Aide et Contact</h1>

      <div class="form-wrapper">
        <ContactForm />
      </div>
    </main>
  </div>
  </template>


  <script setup lang="ts">
import ContactForm from '@/components/forms/ContactForm.vue';
import TheSidebar from '@/components/layout/TheSidebar.vue';

</script>

<style scoped>
.contact-main {
  flex: 1;
  margin-left: 260px;
  padding: 40px;
  box-sizing: border-box;
  background-color: #f8f6f5;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.page-title {
  font-size: 2rem;
  font-weight: 800;
  color: #2c1a14;
  text-align: left;
  margin: 0 0 40px 0; /* Un bon espace en bas du titre */
}

.form-wrapper {
  flex: 1;
  display: flex;
  justify-content: center; /* Centre le formulaire horizontalement */
  align-items: center;     /* Centre le formulaire verticalement dans l'espace restant */
}

/* Ton formulaire prend 50% de l'espace dans son conteneur centré */
.form-wrapper > :first-child {
  width: 50%;
  min-width: 450px;
}
</style>
