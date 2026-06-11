<template>
  <form @submit.prevent="handleSubmit" class="contact-form">
    <div class="form-group">
      <label for="name">Nom / Nom de l'association</label>
      <input type="text" id="name" v-model="form.name" required />
    </div>

    <div class="form-group">
      <label for="email">Adresse Email</label>
      <input type="email" id="email" v-model="form.email" required />
    </div>

    <div class="form-group">
      <label for="message">Votre message</label>
      <textarea id="message" v-model="form.message" rows="5" required></textarea>
    </div>

    <button type="submit" class="submit-btn">Envoyer le message</button>
  </form>
</template>

<script setup>
import { ref } from 'vue';

const form = ref({
  name: '',
  email: '',
  message: ''
});

const handleSubmit = () => {
  console.log('Formulaire de contact soumis :', form.value);
};
</script>


<style scoped>
.contact-form {
  width: 100%; /* 👈 Prend toute la largeur accordée par le parent */
  max-width: 650px; /* 👈 Augmenté pour un rendu plus large et spacieux */
  margin: 0 auto;
  padding: 40px; /* Un poil plus de padding pour respirer avec la nouvelle largeur */
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: left;
}

.form-group label {
  font-weight: 600;
  font-size: 0.95rem;
  color: #2c1a14; /* Rappel de ta couleur principale sombre */
}

.form-group input,
.form-group textarea {
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px; /* Raccord avec tes menu-items */
  font-size: 1rem;
  font-family: inherit;
  transition: all 0.2s ease;
  background-color: #fafafa;
}

/* Effet au survol et au clic sur les champs */
.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: darksalmon; /* Ton accent de couleur */
  background-color: #fff;
  box-shadow: 0 0 0 3px rgba(233, 150, 122, 0.15); /* Halos discrets couleur darksalmon */
}

.form-group textarea {
  resize: vertical; /* Permet d'agrandir seulement en hauteur */
}

/* Bouton d'envoi */
.submit-btn {
  background-color: #2c1a14; /* Fond marron comme ta sidebar */
  color: white;
  border: none;
  padding: 14px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-top: 10px;
}

.submit-btn:hover {
  background-color: darksalmon; /* Le bouton passe en rose/saumon au survol */
  color: #2c1a14; /* Le texte s'assombrit pour rester lisible */
  transform: translateY(-1px); /* Léger effet de surélévation */
}

.submit-btn:active {
  transform: translateY(0);
}
</style>
