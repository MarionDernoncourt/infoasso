<template>
  <form @submit.prevent="handleSubmit" class="asso-form">
    <div class="header-main">
      <h2 class="form-title">{{ formTitle }}</h2>
      <button
        type="button"
        class="back-btn"
        @click="$emit('back')"
        aria-label="Retourner à la page précédente"
      >
        Retour
      </button>
    </div>

    <div class="form-section">
      <h3>🏢 Identité de la structure</h3>

      <div class="form-grid">
        <div class="form-group">
          <label for="rnaNumber">Numéro RNA (WXXXXXXXXX) *</label>
          <div class="rna-input-group">
            <input
              type="text"
              id="rnaNumber"
              v-model="formData.rnaNumber"
              required
              placeholder="Ex: W123456789"
              :class="{ 'input-error': errors.rnaNumber }"
              aria-required="true"
              aria-describedby="rnaError rnaSuccess rnaCustomError"
            >
            <button
              type="button"
              class="verify-btn"
              @click="checkRNA"
              :disabled="isCheckingRNA || !formData.rnaNumber"
              aria-label="Vérifier le numéro RNA auprès du répertoire national"
            >
              {{ isCheckingRNA ? '...' : '🔍 Vérifier' }}
            </button>
          </div>
          <span v-if="rnaError" id="rnaError" class="error-text">⚠️ Numéro RNA introuvable ou inactive</span>
          <span v-if="rnaSuccess" id="rnaSuccess" class="success-text">✅ Association trouvée avec succès !</span>
          <p v-if="errors.rnaNumber" id="rnaCustomError" class="error-text">{{ errors.rnaNumber }}</p>
        </div>

        <div class="form-group">
          <label for="officialName">Nom officiel (Journal Officiel)</label>
          <input
            type="text"
            id="officialName"
            v-model="formData.officialName"
            readonly
            placeholder="Cliquez sur Vérifier pour remplir ce champ"
            class="readonly-input"
            aria-readonly="true"
          >
        </div>
      </div>

      <div class="form-group">
        <label for="displayName">Nom d'usage (affiché sur le site) *</label>
        <input
          type="text"
          id="displayName"
          v-model="formData.displayName"
          required
          placeholder="Ex: Basket Club Loos"
          :class="{ 'input-error': errors.displayName }"
          aria-required="true"
          aria-describedby="displayNameError"
        >
        <p v-if="errors.displayName" id="displayNameError" class="error-text">{{ errors.displayName }}</p>
      </div>

      <div class="form-grid">
        <div class="form-group">
          <label for="categoryType">Secteur d'activité *</label>
          <select
            id="categoryType"
            v-model="formData.categoryType"
            @change="handleTypeChange"
            required
            aria-required="true"
          >
            <option value="" disabled>-- Choisissez un secteur --</option>
            <option v-for="type in categoryTypes" :key="type" :value="type">
              {{ type }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label for="categoryLabel">Activité spécifique (ex: Football, Chorale...) *</label>
          <input
            type="text"
            id="categoryLabel"
            v-model="formData.categoryLabel"
            list="category-suggestions"
            @input="fetchSuggestions"
            :disabled="!formData.categoryType"
            placeholder="Tapez pour chercher ou ajouter..."
            required
            aria-required="true"
          />
          <datalist id="category-suggestions">
            <option v-for="suggestion in labelSuggestions" :key="suggestion.id" :value="suggestion.label" />
          </datalist>
        </div>
      </div>
    </div>

    <section class="form-section">
      <h3>📍 Coordonnées et Contacts</h3>

      <div class="form-group">
        <label for="streetAddress">Adresse (rue, avenue, ...)</label>
        <input
          type="text"
          id="streetAddress"
          v-model="formData.streetAddress"
          placeholder="Ex: 42 rue de la République"
        >
      </div>

      <div class="form-grid">
        <div class="form-group">
          <label for="zipCode">Code postal</label>
          <input
            type="text"
            id="zipCode"
            v-model="formData.zipCode"
            placeholder="Ex: 59120"
          >
        </div>

        <div class="form-group">
          <label for="city">Ville</label>
          <input
            type="text"
            id="city"
            v-model="formData.city"
            placeholder="Ex: Loos"
            :class="{ 'input-error': errors.city }"
            aria-describedby="cityError"
          >
          <p v-if="errors.city" id="cityError" class="error-text">{{ errors.city }}</p>
        </div>
      </div>

      <div class="form-grid">
        <div class="form-group">
          <label for="email">Email de contact *</label>
          <input
            type="email"
            id="email"
            v-model="formData.email"
            required
            placeholder="Ex: contact@monasso.com"
            :class="{ 'input-error': errors.email }"
            aria-required="true"
            aria-describedby="emailError"
          >
          <p v-if="errors.email" id="emailError" class="error-text">{{ errors.email }}</p>
        </div>

        <div class="form-group">
          <label for="phoneNumber">Numéro de téléphone</label>
          <input
            type="tel"
            id="phoneNumber"
            v-model="formData.phoneNumber"
            placeholder="Ex: 03 20 ..."
            :class="{ 'input-error': errors.phoneNumber }"
            aria-describedby="phoneError"
          >
          <p v-if="errors.phoneNumber" id="phoneError" class="error-text">{{ errors.phoneNumber }}</p>
        </div>

        <div class="form-group">
          <label for="website">Site web</label>
          <input
            type="text"
            id="website"
            v-model="formData.website"
            placeholder="Ex: www.monasso.fr"
          />
        </div>
      </div>
    </section>

    <section class="form-section">
      <h3>📝 Présentation</h3>

      <div class="form-group">
        <label for="description">Description de l'association *</label>
        <textarea
          id="description"
          v-model="formData.description"
          rows="4"
          required
          placeholder="Présentez votre association, ses valeurs, ses horaires..."
          :class="{ 'input-error': errors.description }"
          aria-required="true"
          aria-describedby="descError"
        ></textarea>
      </div>
      <p v-if="errors.description" id="descError" class="error-text">{{ errors.description }}</p>
    </section>

    <div class="form-actions">
      <button
        type="button"
        class="cancel-btn"
        @click="$emit('cancel')"
        aria-label="Annuler les modifications et fermer le formulaire"
      >
        Annuler
      </button>
      <button
        type="submit"
        class="submit-btn"
        :disabled="isSubmitting || (!rnaSuccess && !formData.officialName)"
        aria-label="Enregistrer le formulaire de l'association"
      >
        {{ isSubmitting ? 'Enregistrement...' : submitButtonText }}
      </button>
    </div>
  </form>
</template>

<script setup>
import { ref, onMounted } from "vue";
import categoryService from '@/services/category.service';
import rnaService from "@/services/rna.service";

// PROPS & EMITS (communication avec les views)
const props = defineProps({
  initialData: {
    type: Object,
    default: () => null
  },
  submitButtonText: {
    type: String,
    default: "Créer la fiche"
  },
  isSubmitting: {
    type: Boolean,
    default: false
  },
  errors: {
    type: Object,
    default: () => ({})
  },
  formTitle: {
    type: String,
    default: "Fiche Association"
  },
});

const emit = defineEmits(["submit", "cancel", "back"]);

// VARIABLES REACTIVES
const categoryTypes = ref([]);
const labelSuggestions = ref([]);

const formData = ref({
  rnaNumber: "",
  officialName: "",
  displayName: "",
  categoryType: "",
  categoryLabel: "",
  streetAddress: "",
  zipCode: "",
  city: "",
  email: "",
  phoneNumber: "",
  description: "",
  website:"",
});

const isCheckingRNA = ref(false);
const rnaError = ref(false);
const rnaSuccess = ref(false);


// FONCTIONS POUR LES CATÉGORIES
// 1. Quand on change de secteur, on réinitialise l'activité spécifique
const handleTypeChange = () => {
  formData.value.categoryLabel = "";
  labelSuggestions.value = [];
};

// 2. Appel à API pour récupérer l'autocomplétion au fil de la saisie
const fetchSuggestions = async () => {
  const query = formData.value.categoryLabel.trim();
  const type = formData.value.categoryType;

  // On évite de surcharger le réseau si la saisie est trop courte
  if (!query || query.length < 2) {
    labelSuggestions.value = [];
    return;
  }

  try {
    // Appel de ton service en passant le type ENUM et le début du texte tapé
    const response = await categoryService.searchCategories(type, query);
    labelSuggestions.value = response.data; // Stocke la liste [{id, label, type}, ...]
  } catch (error) {
    console.error("Erreur lors de la récupération des suggestions :", error);
  }
};

onMounted(async () => {
  // 📡 Récupération des ENUMs pour le Select
  try {
    const response = await categoryService.getCategoryTypes();
    categoryTypes.value = response.data;
  } catch (error) {
    console.error("Erreur lors de la récupération des types de catégories : ", error);
  }

  // 📝 Mode Update
  if (props.initialData) {
    formData.value = { ...props.initialData };
    rnaSuccess.value = true;
  }
});

// LOGIQUE VERIFICATION RNA
const checkRNA = async () => {
  const rnaNumber = formData.value.rnaNumber.trim();
  if (!rnaNumber) return;

  try {
    isCheckingRNA.value = true;
    rnaError.value = false;
    rnaSuccess.value = false;

    const result = await rnaService.checkRnaNumber(rnaNumber);

    if (result && result.officialName) {
      formData.value.officialName = result.officialName;
      rnaSuccess.value = true;
    } else {
      rnaError.value = true;
    }
  } catch (error) {
    console.error("Erreur lors de la vérification du RNA : ", error);
    rnaError.value = true;
    rnaSuccess.value = false;
    formData.value.officialName = "";
  } finally {
    isCheckingRNA.value = false;
  }
};

// SOUMISSION DU FORMULAIRE
const handleSubmit = () => {
  if (!rnaSuccess.value && !formData.value.officialName) {
    alert("Veuillez d'abord vérifier votre numéro RNA.");
    return;
  }
  emit("submit", formData.value);
};
</script>

<style scoped>
.asso-form {
  display: flex;
  flex-direction: column;
  gap: 32px;
  background-color: #ffffff;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}
.header-main {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}
/* --- Sections du formulaire --- */
.form-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  border-bottom: 1px solid #f0e6df;
  padding-bottom: 24px;
}

.form-section:last-of-type {
  border-bottom: none;
  padding-bottom: 0;
}

.form-section h3 {
  font-size: 1.2rem;
  color: #2c1f18;
  margin: 0;
  font-weight: 600;
}

/* --- Disposition en grille (2 colonnes) --- */
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

/* --- Groupes de champs individuels --- */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 0.9rem;
  font-weight: 500;
  color: #5c4d44;
}

/* --- Inputs, Selects, Textareas --- */
input[type="text"],
input[type="email"],
input[type="tel"],
select,
textarea {
  padding: 12px 16px;
  border: 1px solid #dcd1ca;
  border-radius: 8px;
  font-size: 0.95rem;
  color: #2c1f18;
  background-color: #faf8f6;
  transition: all 0.2s ease;
  width: 100%;
  box-sizing: border-box;
}

input:focus,
select:focus,
textarea:focus {
  outline: none;
  border-color: #ff7a59;
  /* La couleur orange/corail de ta charte */
  background-color: #ffffff;
  box-shadow: 0 0 0 3px rgba(255, 122, 89, 0.1);
}

/* --- Champ spécifique en lecture seule --- */
.readonly-input {
  background-color: #f0eae6;
  color: #7c6d64;
  cursor: not-allowed;
  border-style: dashed;
}

/* --- Le groupe spécifique pour l'input RNA + Bouton --- */
.rna-input-group {
  display: flex;
  gap: 12px;
}

.verify-btn {
  padding: 0 24px;
  background-color: #2c1f18;
  /* Marron foncé/Anthracite chic */
  color: #ffffff;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
}

.verify-btn:hover:not(:disabled) {
  background-color: #4a372c;
}

.verify-btn:disabled {
  background-color: #dcd1ca;
  cursor: not-allowed;
}

/* --- Textes d'aide et d'erreur --- */
.error-text {
  font-size: 0.85rem;
  color: #dc3545;
  font-weight: 500;
}

.success-text {
  font-size: 0.85rem;
  color: #198754;
  font-weight: 500;
}

/* --- Boutons d'actions en bas --- */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 12px;
}

.cancel-btn {
  padding: 12px 28px;
  background-color: transparent;
  color: #7c6d64;
  border: 1px solid #dcd1ca;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-btn:hover {
  background-color: #faf8f6;
  color: #2c1f18;
}

.submit-btn,
.back-btn {
  background: white;
  border: 1px solid darksalmon;
  color: darksalmon;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
}

.submit-btn:hover:not(:disabled),
.back-btn:hover {
  background: #fdf0eb;
}

.submit-btn:disabled {
  background-color: #f0e6df;
  color: #bcada4;
  cursor: not-allowed;
}

input.input-error,
select.input-error,
textarea.input-error {
  border-color: #dc3545 !important;
  background-color: #fff8f8 !important;
}

.error-text {
  color: #dc3545;
  font-size: 0.85rem;
  margin: 4px 0 0 0;
  font-weight: 500;
}

.form-title {
  font-size: 1.6rem;
  color: #2c1f18;
  /* Ton marron/anthracite chic */
  margin: 0 0 8px 0;
  font-weight: 700;
  border-bottom: 2px solid #ff7a59;
  /* Petite ligne corail en dessous */
  padding-bottom: 12px;
}

/* --- Responsive (Pour les petits écrans de PC ou tablettes) --- */
@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
    /* On repasse sur une seule colonne */
    gap: 16px;
  }

  .asso-form {
    padding: 20px;
  }
}
</style>
