<template>
  <div class="overlay" @click.self="$emit('close')" role="dialog" aria-modal="true"
    aria-labelledby="modal-activity-title">

    <div class="update-card">
      <div class="form-group">
        <button type="button" class="close-btn" @click="$emit('close')"
          aria-label="Fermer la fenêtre des détails">×</button>
<h2>Modifier le mot de passe</h2>
      </div>
      <div class="form-group">
        <form @submit.prevent="updateProfile" aria-label="formulaire de modification du profil">
          <div class="form-group">
            <label class="form-line" for="email">
              Email
              <input type="email" v-model="credentials.email" id="email" required aria-required="true"
                autocomplete="email" aria-describedby="emailError">
            </label>
            <p v-if="fieldErrors.email" id="emailError" class="error-text">
              {{ fieldErrors.email }}
            </p>
          </div>

          <div class="form-group">
            <label class="form-line" for="old-password">
              Ancien mot de passe
                             <div class="password-input-wrapper">

              <input :type="showOldPassword ? 'text' : 'password'" v-model="credentials.oldPassword" id="old-password" required aria-required="true"
                autocomplete="old-password" aria-describedby="oldPasswordError">
                  <button
                type="button"
                class="toggle-password-btn"
                @click="showOldPassword = !showOldPassword"
                tabindex="-1"
                :aria-label="showOldPassword ? 'Masquer le mot de passe' : 'Afficher le mot de passe'"
                >
                  {{ showOldPassword ? '👁️' : '👁️‍🗨️' }}
                </button>
                </div>
            </label>
            <p v-if="fieldErrors.oldPassword" id="oldPasswordError" class="error-text">
              {{ fieldErrors.oldPassword }}
            </p>
          </div>

          <div class="form-group">
            <label class="form-line" for="new-password">
              Nouveau mot de passe
               <div class="password-input-wrapper">
              <input :type="showNewPassword ? 'text' : 'password'" v-model="credentials.newPassword" id="new-password" required aria-required="true"
                autocomplete="new-password" aria-describedby="newPasswordError">
                  <button
                type="button"
                class="toggle-password-btn"
                @click="showNewPassword = !showNewPassword"
                tabindex="-1"
                :aria-label="showNewPassword ? 'Masquer le mot de passe' : 'Afficher le mot de passe'"
                >
                  {{ showNewPassword ? '👁️' : '👁️‍🗨️' }}
                </button>
                </div>
            </label>
                <p class="password-hints" id="passwordHints">
              Doit contenir au moins 8 caractères, 1 majuscule et 1 caractère spécial.
            </p>
            <p v-if="fieldErrors.newPassword" id="newPasswordError" class="error-text">
              {{ fieldErrors.newPassword }}
            </p>
          </div>

          <button type="submit" :disabled="isLoading" aria-label="Modifier les informations du profil.">
            {{ isLoading ? 'Modification en cours...' : 'Modifier' }}
          </button>

          <p v-if="fieldErrors.message" class="error-text global-error" role="alert">
            {{ fieldErrors.message }}
          </p>
        </form>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import userService from '@/services/user.service';

defineEmits(['close']);

const router = useRouter();
const isLoading = ref(false);
const showOldPassword = ref(false);
const showNewPassword = ref(false);

const credentials = ref({
  email: "",
  oldPassword: "",
  newPassword: "",
});

const fieldErrors = ref({
  email: "",
  oldPassword: "",
  newPassword: "",
  message: "",
});

onMounted(async () => {
  try {
    const response = await userService.getMyProfile();

    credentials.value.email = response.email;
  } catch (error) {
    console.error("Erreur chargement : ", error);
    alert("Impossible de charger les données.");
    router.push({ name: 'dashboard' });
  }
})

const updateProfile = async () => {
  isLoading.value = true;
  fieldErrors.value.message = "";
  try {
    await userService.updateMyProfile(credentials.value);
    fieldErrors.value.message = "Le profil a été modifié avec succès !";

  } catch (error) {
    console.error("Erreur lors de la modification", error);
    if (error.response) {
      if (error.response.status === 400 && error.response.data.errors) {

        fieldErrors.value = error.response.data.errors;
      }

      // Cas B : Erreur 401 ou 403 (Identifiants incorrects)
      else if (error.response.status === 401 || error.response.status === 403) {
        fieldErrors.value = {
          message: error.response.data.message || "Email ou mot de passe incorrect."
        };


        // Cas C : Autre erreur du serveur (ex: 500)
      } else {
        fieldErrors.value = { message: "Une erreur est survenue sur le serveur. Réessaye." };
      }

      // Cas D : Pas de réponse du tout (Le serveur Spring Boot est éteint)
    } else {
      fieldErrors.value = { message: "Connexion impossible. Le serveur ne répond pas." };
    }
  } finally {
    isLoading.value = false;
  }
}



</script>

<style scoped>
/* L'overlay couvre tout l'écran avec un effet assombri/flouté */
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

/* La carte modale reprend le design .form-container */
.update-card {
  background-color: white;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  padding: 40px 35px;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 450px;
  position: relative;
  max-height: 80vh;
  overflow-y: auto;
}

/* Titre aligné sur ton style h1 */
h2 {
  color: #2c1a14;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 2px;
  font-size: 1.5rem;
  margin-top: 0;
  margin-bottom: 2rem;
  text-align: center;
  width: 100%;
}

/* Bouton de fermeture de la modale */
.close-btn {
  position: absolute;
  top: 15px;
  right: 20px;
  cursor: pointer;
  border: none;
  background: none;
  font-size: 1.8rem;
  color: #5a3e36;
  transition: transform 0.2s;
}

.close-btn:hover {
  color: darksalmon;
}

form {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.form-group {
  display: flex;
  flex-direction: column;
  width: 100%;
  margin-bottom: 1.2rem;
}

.form-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  width: 100%;
  margin-bottom: 0;
}

/* --- CHAMP MOT DE PASSE AVEC ŒIL INTÉGRÉ --- */
input {
  padding: 10px 12px;
  border: 1px solid #ccc;
  border-radius: 6px;
  width: 260px; /* Ajuste cette valeur selon l'espace souhaité */
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
  width: 260px;
  padding-right: 40px;
  box-sizing: border-box;
}

.toggle-password-btn {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.1rem;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

.toggle-password-btn:hover {
  color: #000;
}
.password-hints {
  font-size: 0.75rem;
  color: #666;
  margin: 5px 0 0 0;
  line-height: 1.3;
}


/* Bouton de soumission identique à ta charte */
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
