<template>
  <form @submit.prevent="handleSubmit" class="schedule-form" aria-label="Formulaire d'activité et d'horaires">
    <div class="header-title">
      <h2 class="form-title">
        {{ initialData ? " Modifier l'horaire" : "Ajouter une activité " }}
      </h2>
      <button
      type="button"
      class="back-btn"
      @click="goToDashboard"
      aria-label="Retourner à la fiche de l'association"
      >Retour à la fiche</button>
    </div>


    <div class="form-section">
      <h3>Nom de l'activité</h3>

      <div class="form-group">
        <label for="activityName">Nom de l'activité</label>
        <input
        type="text"
        id="activityName"
        v-model="formData.activityName"
        placeholder="Ex: Cours de baby gym"
        required
        aria-required="true"
        aria-describedby="activityNameError"
         />
        <p v-if="errors.activityName" id="activityNameError" class="error-text">
          {{ errors.activityName }}
        </p>
      </div>

      <div class="form-group">
        <label for="description">Description de l'activité</label>
        <input
        type="text"
        id="description"
        v-model="formData.description"
        placeholder="Ex: Initiation à la gym, parcours de motricité, ...."
        aria-describedby="descriptionError"
        />
        <p v-if="errors.description" id="descriptionError" class="error-text">
          {{ errors.description }}
        </p>
      </div>
    </div>


    <div class="form-section">
      <h3>Horaire de l'activité</h3>

      <div class="form-row" style="display: flex; gap: 20px;">
        <div class="form-group" style="flex: 1;">
          <label for="dayOfWeek">Jour</label>
          <select id="dayOfWeek" v-model="formData.dayOfWeek" required aria-required="true" aria-label="Sélectionner le jour de l'activité">
            <option value="" disabled>--Sélectionner le jour--</option>
            <option v-for="day in dayOfWeek" :key="day" :value="day">{{ day }}</option>
          </select>
        </div>

 <!-- HEURE DE DÉBUT -->
        <div class="form-group" style="flex: 1;">
          <label>Début</label>
          <div style="display: flex; gap: 5px; align-items: center;" aria-label="Heure de début">
            <!-- Select Heures (00 à 23) -->
            <select v-model="startHour" required aria-label="Heure de début (heures)">
              <option value="" disabled>Heure</option>
              <option v-for="h in 24" :key="h-1" :value="String(h-1).padStart(2, '0')">
                {{ String(h-1).padStart(2, '0') }}
              </option>
            </select>
            <span>:</span>
            <!-- Select Minutes (00, 15, 30, 45) -->
            <select v-model="startMinute" required aria-label="Heure de début (minutes)">
              <option value="" disabled>Min</option>
              <option value="00">00</option>
              <option value="15">15</option>
              <option value="30">30</option>
              <option value="45">45</option>
            </select>
          </div>
          <p v-if="errors.startTime" class="error-text">
            {{ errors.startTime }}
          </p>
        </div>

        <!-- HEURE DE FIN -->
        <div class="form-group" style="flex: 1;">
          <label>Fin</label>
          <div style="display: flex; gap: 5px; align-items: center;" aria-label="Heure de fin">
            <!-- Select Heures (00 à 23) -->
            <select v-model="endHour" required aria-label="Heure de fin (heures)">
              <option value="" disabled>Heure</option>
              <option v-for="h in 24" :key="h-1" :value="String(h-1).padStart(2, '0')">
                {{ String(h-1).padStart(2, '0') }}
              </option>
            </select>
            <span>:</span>
            <!-- Select Minutes (00, 15, 30, 45) -->
            <select v-model="endMinute" required aria-label="Heure de fin (minutes)">
              <option value="" disabled>Min</option>
              <option value="00">00</option>
              <option value="15">15</option>
              <option value="30">30</option>
              <option value="45">45</option>
            </select>
          </div>
          <p v-if="errors.endTime" class="error-text">
            {{ errors.endTime }}
          </p>
        </div>
      </div>
    </div>


    <div class="form-section">
      <h3>Age requis</h3>

      <div class="form-row" style="display: flex; gap: 20px">
        <div class="form-group">
          <label for="ageMin">Age minimum</label>
          <input
          type="number"
          id="ageMin"
          v-model="formData.ageMin"
          min="0" max="100"
          placeholder="Ex : 8"
          required
          aria-required="true"
          aria-describedby="ageMinError"
          />
          <p v-if="errors.ageMin" id="ageMinError" class="error-text">
            {{ errors.ageMin }}
          </p>
        </div>
        <div class="form-group">
          <label for="ageMax">Age maximum</label>
          <input
          type="number"
          id="ageMax"
          v-model="formData.ageMax"
          min="0" max="100"
          placeholder="Ex: 10"
          required
          aria-required="true"
          aria-describedby="ageMaxError"
          />
          <p v-if="errors.ageMax" id="ageMaxError" class="error-text">
            {{ errors.ageMax }}
          </p>
        </div>
      </div>
    </div>


    <div class="form-section">
      <h3>Lieu de l'activité</h3>

      <div class="form-row" style="display: flex; gap: 20px">
        <div class="form-group">
          <label for="locationName">Nom du lieu</label>
          <input type="text" id="locationName" v-model="formData.location.name" placeholder="Ex: Gymnase" aria-label="Nom du lieu de l'activité">
        </div>

        <div class="form-group">
          <label for="address">Adresse</label>
          <input type="text" id="address" v-model="formData.location.address" placeholder="Ex: 12 rue Paradis" aria-label="Adresse du lieu de l'activité" />
        </div>
        <div class="form-group">
          <label for="city">Ville</label>
          <input
          type="text"
          id="city"
          v-model="formData.location.city"
          placeholder="Ex: Lille"
          required
          aria-required="true"
          aria-describedby="cityError"
          aria-label="Ville du lieu de l'activité"
          />
          <p v-if="errors.city" id="cityError" class="error-text">
            {{ errors.city }}
          </p>
        </div>

        <div class="form-group">
          <label for="zipCode">Code Postal</label>
          <input
          type="text"
          id="zipCode"
          v-model="formData.location.zipCode"
          placeholder="Ex: 59000" pattern="[0-9]{5}"
          title="Entrez 5 chiffres"
          aria-label="Code postal du lieu de l'activité"
          />
        </div>
      </div>
    </div>


    <div class="forms-action">
      <button
      type="button"
      class="cancel-btn"
      @click="$emit('cancel')"
      aria-label="Annuler l'enregistrement de l'activité"
      >Annuler
    </button>
      <button
      type="submit"
      class="submit-btn"
      :disabled="isSubmitting"
      aria-label="Enregistrer l'activité"
      >
        {{ isSubmitting ? 'Enregistrement...' : submitButtonText || "Enregistrer" }}</button>

    </div>
  </form>
</template>


<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRoute } from "vue-router"
import schedulesService from "@/services/schedules.service";
import router from '@/router';

const route = useRoute();

// PROPS & EMITS (comm avec les views)
const props = defineProps({
  initialData: {
    type: Object,
    default: () => null
  },
  isSubmitting: {
    type: Boolean,
    default: false
  },
  errors: {
    type: Object,
    default: () => ({})
  },
  submitButtonText: {
    type: String,
    default: "Enregistrer"
  },
});

const emit = defineEmits(['cancel', 'submit']);

const startHour = ref("");
const startMinute = ref("");
const endHour = ref("");
const endMinute = ref("");

//VARIABLE REACTIVE
const dayOfWeek = ref([]);
const formData = ref({
  activityName: "",
  dayOfWeek: "",
  startTime: "",
  endTime: "",
  ageMin: "",
  ageMax: "",
  description: "",
  // Champs pour la Location
  location: {
    name: "",
    address: "",
    city: "",
    zipCode: "",
  }
})

const goToDashboard = () => {
  router.push("/dashboard");
}


onMounted(async () => {

  console.log("Route actuelle :", route.name);

  //Récupération des ENUMs pour le Select
  try {
    const response = await schedulesService.getDaysOfWeek();
    dayOfWeek.value = response.data;
  } catch (error) {
    console.error("Erreur lors de la récupération des jours de la semaine: ", error);
  }

});

watch(() => props.initialData, (newVal) => {
  if (newVal) {
    formData.value = {
      activityName: newVal.activityName,
      dayOfWeek: newVal.dayOfWeek,
      ageMin: newVal.ageMin,
      ageMax: newVal.ageMax,
      associationId: newVal.association?.id,
      description: newVal.description,
      location: {
        name: newVal.location?.name || "",
        address: newVal.location?.address || "",
        city: newVal.location?.city || "",
        zipCode: newVal.location?.zipCode || ""
      }
    };

    // Découpage de l'heure de début existante (ex: "14:30" -> "14" et "30")
    if (newVal.startTime) {
      const [h, m] = newVal.startTime.split(':');
      startHour.value = h || "";
      startMinute.value = m || "";
    }
    // Découpage de l'heure de fin
    if (newVal.endTime) {
      const [h, m] = newVal.endTime.split(':');
      endHour.value = h || "";
      endMinute.value = m || "";
    }
  }
}, { immediate: true });

// SOUMISSION FORMULAIRE
const handleSubmit = () => {
  // On reconstruit les chaînes "HH:mm" attendues par ton API
  formData.value.startTime = `${startHour.value}:${startMinute.value}`;
  formData.value.endTime = `${endHour.value}:${endMinute.value}`;

  console.log("Formulaire soumis, envoi au parent...");
  emit("submit", formData.value);
}

</script>

<style scoped>
.header-title {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}

.schedule-form h2 {
  margin-bottom: 1.5rem;
  color: #333;
}

.form-section {
  margin-bottom: 2rem;
  border-bottom: 1px solid #eee;
  padding-bottom: 1rem;
}

.form-group {
  margin-bottom: 1rem;
  display: flex;
  flex-direction: column;
}

label {
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #555;
}

input,
select {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
}

.error-text {
  color: #e74c3c;
  font-size: 0.85rem;
  margin-top: 0.25rem;
}

.forms-action {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.cancel-btn {
   background-color: transparent;
  color: #7c6d64;
  border: 1px solid #dcd1ca;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
    padding: 8px 16px;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
}
</style>
