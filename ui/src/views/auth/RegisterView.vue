<template>
  <div class="main-container">
    <div class="register-wrapper">

      <div class="info-sidebar">
        <h2>INFO ASSO</h2>
        <p class="intro-text">
          La plateforme de référence pour découvrir la vie associative de votre ville.
        </p>

        <div class="audience-section citizen-section">
          <h3>Pour les Citoyens 🏙️</h3>
          <p class="section-intro">
            <strong>Envie d'explorer ?</strong> Pas besoin de s'inscrire !
            <router-link to="/" class="inline-link-salmon">Trouvez votre bonheur ici</router-link> pour découvrir le tissu associatif local.
          </p>
        </div>

        <div class="audience-section asso-section">
          <h3>Pour les Associations 📢</h3>
          <p class="section-intro">
            <strong>Responsable d'association ?</strong> Créez votre compte pour piloter votre page et booster votre visibilité locale.
          </p>
          <ul class="audience-features">
            <li>✨ <strong>Visibilité maximale :</strong> Présentez votre activité et vos valeurs aux habitants de votre ville.</li>
            <li>📅 <strong>Planning en ligne :</strong> Partagez vos horaires et vos événements associatifs en temps réel.</li>
            <li>📍 <strong>Contact & Adresse :</strong> Permettez aux futurs adhérents de vous trouver et de vous joindre facilement.</li>
          </ul>
        </div>

        <div class="cta-section">
          <p class="login-prompt">
            Votre association a déjà un compte ?
            <router-link to="/login" class="inline-link">Identifiez-vous ici</router-link>.
          </p>
        </div>
      </div>

      <div class="form-container">
        <div class="form-header-badge">Espace Responsables d'Association</div>
        <h3>Inscription Association</h3>
        <p class="form-subtitle">Réservé aux membres du bureau ou gestionnaires de l'association.</p>

        <form @submit.prevent="register">

          <!-- EMAIL -->
          <div class="form-group">
            <label class="form-line">
              E-mail de gestion (ou de l'association)
              <input type="email" v-model="userData.email" placeholder="contact@votre-asso.fr" required>
            </label>
            <p v-if="fieldErrors.email" class="error-text"> {{ fieldErrors.email }}</p>
          </div>

          <!-- MOT DE PASSE AVEC OEIL -->
          <div class="form-group">
            <label class="form-line">
              Mot de passe
              <div class="password-input-wrapper">
                <input
                  :type="showPassword ? 'text' : 'password'"
                  v-model="userData.password"
                  placeholder="••••••••"
                  required
                >
                <button type="button" class="toggle-password-btn" @click="showPassword = !showPassword" tabindex="-1">
                  {{ showPassword ? '👁️' : '👁️‍🗨️' }}
                </button>
              </div>
            </label>

            <p class="password-hints">
              Doit contenir au moins 8 caractères, 1 majuscule et 1 caractère spécial.
            </p>

            <p v-if="fieldErrors.password" class="error-text">
              {{ fieldErrors.password }}
            </p>
          </div>

          <!-- GDPR -->
          <div class="form-group gdpr-group">
            <label class="gdpr-label">
              <input type="checkbox" v-model="userData.gdprConsent" required>
              <span>
                J'accepte la <a href="#" @click.prevent="isModalOpen = true" class="gdpr-link">politique de confidentialité</a>.
              </span>
            </label>
            <p v-if="fieldErrors.gdprConsent" class="error-text">
              {{ fieldErrors.gdprConsent }}
            </p>
          </div>

          <button type="submit" :disabled="isLoading">
            {{ isLoading ? 'Création du compte...' : "Inscrire mon association" }}
          </button>

          <p v-if="fieldErrors.message" class="error-text global-error">
            {{ fieldErrors.message }}
          </p>
        </form>
      </div>

    </div>

    <!-- MODAL GDPR -->
    <div v-if="isModalOpen" class="modal-overlay" @click="isModalOpen = false">
      <div class="modal-content" @click.stop>
        <h3>Politique de Confidentialité</h3>
        <hr>
        <p><strong>1. Données collectées :</strong> Nous collectons uniquement l'adresse email de gestion et le mot de passe (haché de manière sécurisée).</p>
        <p><strong>2. Finalité :</strong> Ces données sont nécessaires pour créer le compte de votre structure et sécuriser l'accès à la gestion de votre page INFO ASSO.</p>
        <p><strong>3. Partage :</strong> Vos données restent strictement confidentielles et ne seront jamais partagées ou vendues à des tiers.</p>
        <p><strong>4. Vos droits :</strong> Vous pouvez demander la suppression du compte de l'association et de ses données à tout moment.</p>

        <button type="button" class="close-modal-btn" @click="isModalOpen = false">Fermer</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue"
import { useRouter } from "vue-router"
import authService from '@/services/auth.service';

const router = useRouter();

const userData = ref({
  email: "",
  password: "",
  gdprConsent: false,
});

const showPassword = ref(false);
const fieldErrors = ref({});
const isLoading = ref(false);
const isModalOpen = ref(false);

const register = async () => {
  isLoading.value = true;
  fieldErrors.value = {};

  try {
    await authService.register(userData.value);

    await authService.login({
      email: userData.value.email,
      password: userData.value.password
    });

    router.push("/dashboard");
  } catch (error) {
    console.log("Erreur de connexion", error);

    if (error.response) {
      if (error.response.status === 400 && error.response.data.errors) {
        fieldErrors.value = error.response.data.errors;
      } else if (error.response.status === 404 || error.response.status === 409) {
        fieldErrors.value = { message: error.response.data.message || "Cet email est déjà utilisé." };
      } else {
        fieldErrors.value = { message: "Une erreur est survenue sur le serveur." };
      }
    } else {
      if (error.response?.data?.message) {
         fieldErrors.value = { message: error.response.data.message };
      } else {
         fieldErrors.value = { message: "Connexion impossible. Le serveur ne répond pas." };
      }
    }
  } finally {
    isLoading.value = false;
  }
}
</script>

<style scoped>
/* --- CONTENEUR PRINCIPAL & LAYOUT --- */
.main-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: darksalmon;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  padding: 20px;
  box-sizing: border-box;
}

.register-wrapper {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  max-width: 1000px;
  width: 100%;
  gap: 60px;
}

/* --- COLONNE INFOS (GAUCHE - 60%) --- */
.info-sidebar {
  flex: 6;
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
  margin-bottom: 30px;
  opacity: 0.9;
}

.audience-section h3 {
  font-size: 1.3rem;
  margin-top: 0;
  margin-bottom: 15px;
  color: #2c1a14;
  border-bottom: 2px solid rgba(44, 26, 20, 0.1);
  padding-bottom: 5px;
  display: inline-block;
}

.section-intro {
  font-size: 1rem;
  margin-bottom: 12px;
  line-height: 1.4;
}

.audience-features {
  margin: 0;
  padding-left: 0;
  list-style: none;
  font-size: 0.95rem;
  display: flex;
  flex-direction: column;
  gap: 10px;
  line-height: 1.4;
}

.citizen-section {
  margin-top: 10px;
  margin-bottom: 30px;
}

.asso-section {
  background-color: rgba(255, 255, 255, 0.25);
  padding: 20px;
  border-radius: 12px;
}

.inline-link-salmon {
  color: #b34e36;
  font-weight: bold;
  text-decoration: underline;
}

.cta-section {
  border-top: 1px solid rgba(44, 26, 20, 0.15);
  padding-top: 20px;
  font-size: 0.95rem;
  margin-top: 25px;
}

.inline-link {
  color: #2c1a14;
  font-weight: bold;
  text-decoration: underline;
}

/* --- COLONNE FORMULAIRE (DROITE - 40%) --- */
.form-container {
  flex: 4;
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

.form-container h3 {
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

.form-group {
  margin-bottom: 20px;
  text-align: left;
}

.form-line {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-weight: 600;
  font-size: 0.9rem;
  color: #555;
}

input[type="email"],
input[type="password"],
input[type="text"] {
  padding: 12px 14px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s;
  background-color: #fafafa;
  width: 100%;
  box-sizing: border-box;
}

input:focus {
  outline: none;
  border-color: darksalmon;
  background-color: white;
}

/* --- CHAMP MOT DE PASSE AVEC ŒIL INTÉGRÉ --- */
.password-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.password-input-wrapper input {
  width: 100%;
  padding-right: 45px; /* Laisse l'espace pour ne pas écrire sous l'œil */
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

.gdpr-group {
  margin: 15px 0 25px 0;
}

.gdpr-label {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  font-size: 0.85rem;
  color: #666;
  cursor: pointer;
  line-height: 1.4;
}

.gdpr-label input {
  width: auto;
  margin-top: 3px;
}

.gdpr-link {
  color: darksalmon;
  text-decoration: underline;
}

button[type="submit"] {
  width: 100%;
  padding: 14px;
  background-color: darksalmon;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: transform 0.1s, background-color 0.2s;
}

button[type="submit"]:hover:not(:disabled) {
  background-color: #e38466;
}

button[type="submit"]:active:not(:disabled) {
  transform: scale(0.98);
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* --- ERREURS --- */
.error-text {
  color: #d9534f;
  font-size: 0.8rem;
  margin: 4px 0 0 0;
  font-weight: 500;
}

.global-error {
  margin-top: 15px;
  text-align: center;
  font-size: 0.9rem;
  background-color: #fdf2f2;
  padding: 10px;
  border-radius: 6px;
  border: 1px solid #f5c6cb;
}

/* --- OVERLAY MODAL --- */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal-content {
  background-color: white;
  padding: 30px;
  border-radius: 12px;
  max-width: 500px;
  width: 90%;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  text-align: left;
}

.modal-content h3 {
  margin: 0 0 10px 0;
  color: #2c1a14;
}

.modal-content hr {
  border: 0;
  border-top: 1px solid #eee;
  margin-bottom: 15px;
}

.modal-content p {
  font-size: 0.9rem;
  line-height: 1.5;
  color: #444;
  margin-bottom: 12px;
}

.close-modal-btn {
  background-color: #2c1a14;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
  margin-top: 10px;
  float: right;
}

/* --- RESPONSIVE MOBILE --- */
@media (max-width: 850px) {
  .register-wrapper {
    flex-direction: column;
    gap: 40px;
  }
  .info-sidebar {
    text-align: center;
  }
  .audience-features li {
    text-align: center;
  }
  .cta-section {
    text-align: center;
  }
}
</style>
