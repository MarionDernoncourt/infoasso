<template>
  <div class="overlay" role="dialog" aria-labelledby="modal-account-title" aria-modal="true">
    <div class="detail-card">
      <div class="form-group">
        <h2 id="modal-account-title">
          Modifier votre compte
        </h2>
        <button class="close-btn" type="button" @click="$emit('close')"
          aria-label="Fermer la fenêtre de modification de votre compte">×</button>
        <hr aria-hidden="true" />
      </div>

      <div class="account-box">
        <form @submit.prevent="handleSubmit" class="account-form">

          <!-- Champ Email -->
          <div class="form-control">
            <label for="email">Adresse Email</label>
            <input
              id="email"
              type="email"
              v-model="form.email"
              aria-required="false"
              :aria-invalid="!!errors.email"
              aria-describedby="email-error" />
            <span id="email-error" class="error-msg" v-if="errors.email">
              {{ errors.email }}
            </span>
          </div>

          <!-- Section Mot de passe -->
          <fieldset class="password-section">
            <legend>Modifier le mot de passe (optionnel)</legend>

            <!-- Ancien mot de passe -->
            <div class="form-control">
              <label for="oldPassword">Ancien mot de passe</label>
              <div class="input-wrapper">
                <input
                  :type="showOldPassword ? 'text' : 'password'"
                  id="oldPassword"
                  v-model="form.oldPassword"
                  :aria-invalid="!!errors.oldPassword"
                  aria-describedby="old-password-error"
                />
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
              <span id="old-password-error" class="error-msg" v-if="errors.oldPassword">
                {{ errors.oldPassword }}
              </span>
            </div>

            <!-- Nouveau mot de passe -->
            <div class="form-control">
              <label for="newPassword">Nouveau mot de passe</label>
              <div class="input-wrapper">
                <input
                  id="newPassword"
                  :type="showNewPassword ? 'text' : 'password'"
                  v-model="form.newPassword"
                  aria-describedby="new-password-error"
                  :aria-invalid="!!errors.newPassword"
                />
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
              <span id="new-password-error" class="error-msg" v-if="errors.newPassword">
                {{ errors.newPassword }}
              </span>
            </div>
          </fieldset>

          <!-- Boutons d'action -->
          <div class="modal-actions">
            <button type="button" @click="$emit('close')">Annuler</button>
            <button type="submit" class="btn-primary">Enregistrer les modifications</button>
          </div>

        </form>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import userService from '@/services/user.service';

const emit = defineEmits(['close']);

const showOldPassword = ref(false);
const showNewPassword = ref(false);

const form = ref ({
  email: "",
  oldPassword: "",
  newPassword: "",
});

const errors = ref({
  email : null,
  oldPassword: null,
  newPassword : null
});

onMounted(async() => {
  try {
    const userData = await userService.getMyProfile();
    form.value.email = userData.email;
  } catch(error) {
    console.error('Erreur lors du chargement du profil', error);
  }
});

async function handleSubmit() {
  errors.value.oldPassword = null;
  errors.value.newPassword = null;

  if (form.value.newPassword && !form.value.oldPassword) {
    errors.value.oldPassword = "L'ancien mot de passe est requis pour le modifier.";
    return;
  }

  if (form.value.oldPassword && !form.value.newPassword) {
    errors.value.newPassword = "Le nouveau mot de passe est requis.";
    return;
  }

  try {
    await userService.updateMyProfile({
      email: form.value.email || null,
      oldPassword: form.value.oldPassword || null,
      newPassword: form.value.newPassword || null
    });
    alert("Le profil a bien été modifié !");
    emit('close');
  } catch(error) {
    console.error("Erreur lors de la mise à jour : ", error);
  }
}
</script>

<style scoped>
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

.detail-card {
  background: white;
  padding: 30px;
  border-radius: 16px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  position: relative;
  max-height: 80vh;
  overflow-y: auto;
}

.form-group {
  display: flex;
  flex-direction: column;
  position: relative;
  margin-bottom: 20px;
}

.form-group h2 {
  color: #2c1a14;
  margin: 0 0 15px 0;
  padding-right: 30px;
  font-size: 1.5rem;
}

.form-group hr {
  border: none;
  border-bottom: 1px solid #e2d4cf;
  margin: 0;
  width: 100%;
}

.close-btn {
  position: absolute;
  top: -5px;
  right: 0;
  cursor: pointer;
  border: none;
  background: none;
  font-size: 1.8rem;
  color: #5a3e36;
  transition: color 0.2s, transform 0.2s;
  padding: 0px;
}

.close-btn:hover {
  color: darksalmon;
  transform: scale(1.1);
  background: none;
}

.account-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
  background: #fdf0eb;
  border: 1px solid #f4d1c1;
  padding: 20px;
  border-radius: 12px;
}

.form-control {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-control label {
  font-weight: 600;
  color: #2c1a14;
  font-size: 0.95rem;
}

/* Style spécifique pour l'input email standard */
.form-control > input {
  padding: 10px 12px;
  border: 1px solid #e2d4cf;
  border-radius: 8px;
  font-size: 1rem;
  color: #2c1a14;
  background: white;
  transition: border-color 0.2s;
}

.form-control > input:focus {
  outline: none;
  border-color: #5a3e36;
}

/* Conteneur pour positionner le bouton à l'intérieur des inputs password */
.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-wrapper input {
  width: 100%;
  padding: 10px 40px 10px 12px;
  border: 1px solid #e2d4cf;
  border-radius: 8px;
  font-size: 1rem;
  color: #2c1a14;
  background: white;
  transition: border-color 0.2s;
}

.input-wrapper input:focus {
  outline: none;
  border-color: #5a3e36;
}

/* Style et centrage vertical du bouton œil */
.toggle-password-btn {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.1rem;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.password-section {
  border: none;
  padding: 0;
  margin: 5px 0 0 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.password-section legend {
  font-weight: 600;
  color: #5a3e36;
  font-size: 0.95rem;
  padding: 0;
  margin-bottom: 10px;
}

.error-msg {
  color: #d9534f;
  font-size: 0.85rem;
  margin-top: 2px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}

.modal-actions button[type="button"] {
  background: transparent;
  border: 1px solid #e2d4cf;
  color: #5a3e36;
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.modal-actions button[type="button"]:hover {
  background-color: rgba(255, 255, 255, 0.5);
}

.btn-primary {
  background-color: #5a3e36;
  color: white;
  border: none;
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-primary:hover {
  background-color: #724f46;
}
</style>
