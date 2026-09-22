<template>
  <div class="main-container" role="main">
    <div class="form-container">

      <h1>INFO ASSO</h1>

      <form @submit.prevent="login" aria-label="Formulaire de connexion">

  <div class="form-group">
    <label class="form-line" for="email">
      Email
      <input
      type="email"
      v-model="credentials.email"
      id="email"
      required
      aria-required="true"
      autocomplete="email"
      aria-describedby="emailError"
      >
    </label>
    <p v-if="fieldErrors.email" id="emailError" class="error-text">
      {{ fieldErrors.email }}
    </p>
  </div>

  <div class="form-group">
    <label class="form-line" for="password">
      Mot de passe
      <div class="password-input-wrapper">
      <input
      :type="showPassword ? 'text' : 'password'"
      v-model="credentials.password"
      id="password"
      required
      aria-required="true"
      autocomplete="current-password"
      aria-describedby="passwordError"
      >
      <button
      type="button"
      class="toggle-password-btn"
      @click="showPassword = !showPassword"
      tabindex="-1"
      :aria-label="showPassword ? 'Masquer le mot de passe' : 'Afficher le mot de passe'"
      >
    {{ showPassword ? '👁️' : '👁️‍🗨️' }}
    </button>
      </div>
    </label>
    <p v-if="fieldErrors.password" id="passwordError" class="error-text">
      {{ fieldErrors.password }}
    </p>
  </div>

  <button
  type="submit"
  :disabled="isLoading"
  aria-label="Se connecter à votre compte"
  >
    {{ isLoading ? 'Connexion en cours...' : 'Se connecter' }}
  </button>

  <p v-if="fieldErrors.message" class="error-text global-error" role="alert">
    {{ fieldErrors.message }}
  </p>

</form>

      <router-link to="/register">1ère utilisation ? Je crée mon compte par ici !</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue"
import { useRouter } from "vue-router"
import authService from "@/services/auth.service.js"

const router = useRouter();

const credentials = ref({
  email: "",
  password: ""
});

const fieldErrors = ref({});

const isLoading = ref(false);
const showPassword = ref(false);

const login = async () => {
  isLoading.value = true;
  fieldErrors.value = {};
  try {
    await authService.login(credentials.value);
    router.push("/dashboard")
  } catch (error) {
    console.error("Erreur de connexion", error)
    if (error.response) {

    if (error.response.status === 400 && error.response.data.errors) {

        fieldErrors.value = error.response.data.errors;
      }

      // Cas B : Erreur 401 ou 403 (Identifiants incorrects)
     else if (error.response.status === 401 || error.response.status === 403) {
      fieldErrors.value = {
        message: error.response.data.message || "Email ou mot de passe incorrect."
      };
    }

      // Cas C : Autre erreur du serveur (ex: 500)
      else {
        fieldErrors.value = { message: "Une erreur est survenue sur le serveur. Réessaye." };
      }
    }
    // Cas D : Pas de réponse du tout (Le serveur Spring Boot est éteint)
    else {
      fieldErrors.value = { message: "Connexion impossible. Le serveur ne répond pas." };
    }

  } finally {
    isLoading.value = false;
}
}
</script>

<style scoped>
.main-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: darksalmon;
  padding: 20px;
}

h1 {
  color: #2c1a14;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 2px;
  font-size: 2rem;
  margin-top: 0;
  margin-bottom: 2rem;
  text-align: center;
}

.form-container {
  background-color: white;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  padding: 40px 35px;
  display: flex;
  flex-direction: column;
  align-items: center;

  width: 100%;
  max-width: 450px;
}

form {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.form-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  width: 100%;
  margin-bottom: 0;
}

input {
  padding: 10px 12px;
  border: 1px solid #ccc;
  border-radius: 6px;
  width: 250px;
  box-sizing: border-box;
}
/* Le wrapper s'adapte à cette même largeur */
.password-input-wrapper {
  position: relative;
  display: inline-flex; /* Pour épouser la largeur fixe de l'input */
  align-items: center;
}

/* L'input à l'intérieur hérite de la même largeur et garde la place pour l'icône */
.password-input-wrapper input {
  width: 250px;
  padding-right: 40px;
  box-sizing: border-box;
}

 .form-container .toggle-password-btn {
  position: absolute;
  right: 12px;
  background: transparent;
  border: none;
  outline: none;
  cursor: pointer;
  font-size: 1.1rem;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

.form-container .toggle-password-btn:hover {
  color: #000;
}
button[type="submit"] {
  background-color: darksalmon;
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
  margin-top: 20px;
  align-self: center;
  width: auto;
  min-width: 160px;
  transition: background-color 0.2s ease;
}

button[type="submit"]:hover {
  background-color: #fca482;
}

a {
  color: #555;
  text-decoration: none;
  font-size: 0.9rem;
  margin-top: 25px;
  transition: color 0.2s ease;
  text-align: center;
}

a:hover {
  color: darksalmon;
  text-decoration: underline;
}
.form-group {
  display: flex;
  flex-direction: column;
  width: 100%;
  margin-bottom: 1.2rem; }



.error-text {
  color: #de4747;
  font-size: 0.8rem;
  margin: 4px 0 0 0;
  align-self: flex-end;
  font-weight: 500;
}

.global-error {
  align-self: center;
  text-align: center;
  margin-top: 15px;
  max-width: 90%;
}
</style>
