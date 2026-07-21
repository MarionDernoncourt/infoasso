<template>
  <form @submit.prevent="handleSubmit" class="schedule-form">
    <div class="header-title">
      <h2 class="form-title">
        {{ initialData ? " Modifier l'horaire" : "Ajouter une activité " }}
      </h2>
      <button class="back-btn" @click="goToDashboard">Retour à la fiche</button>
    </div>
    <div class="form-section">
      <h3>Nom de l'activité</h3>

      <div class="form-group">
        <label for="activityName">Nom de l'activité</label>
        <input type="text" id="activityName" v-model="formData.activityName" placeholder="Ex: Cours de baby gym"
          required />
        <p v-if="errors.activityName" class="error-text">
          {{ errors.activityName }}
        </p>
      </div>
      <div class="form-group">
        <label for="description">Description de l'activité</label>
        <input type="text" id="description" v-model="formData.description"
          placeholder="Ex: Initiation à la gym, parcours de motricité, ...." />
        <p v-if="errors.description" class="error-text">
          {{ errors.description }}
        </p>
      </div>
    </div>

    <div class="form-section">
      <h3>Horaire de l'activité</h3>

      <div class="form-row" style="display: flex; gap: 20px;">
        <div class="form-group" style="flex: 1;">
          <label for="dayOfWeek">Jour</label>
          <select id="dayOfWeek" v-model="formData.dayOfWeek" required>
            <option value="" disabled>--Sélectionner le jour--</option>
            <option v-for="day in dayOfWeek" :key="day" :value="day">{{ day }}</option>
          </select>
        </div>

        <div class="form-group" style="flex: 1;">
          <label for="startTime">Début</label>
          <input type="time" id="startTime" v-model="formData.startTime" required />
          <p v-if="errors.startTime" class="error-text">
            {{ errors.startTime }}
          </p>
        </div>

        <div class="form-group" style="flex: 1;">
          <label for="endTime">Fin</label>
          <input type="time" id="endTime" v-model="formData.endTime" required />
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
          <input type="number" id="ageMin" v-model="formData.ageMin" min="0" max="100" placeholder="Ex : 8" required />
          <p v-if="errors.ageMin" class="error-text">
            {{ errors.ageMin }}
          </p>
        </div>
        <div class="form-group">
          <label for="ageMax">Age maximum</label>
          <input type="number" id="ageMax" v-model="formData.ageMax" min="0" max="100" placeholder="Ex: 10" required />
          <p v-if="errors.ageMax" class="error-text">
            {{ errors.ageMax }}
          </p>
        </div>
      </div>

    </div>

    <div class="form-section">
      <h3>Lieu de l'activité</h3>
      <div class="form-row" style="display: flex; gap: 20px">
        <div class="form-group">
          <label for="name">Nom du lieu</label>
          <input type="text" id="name" v-model="formData.location.name" placeholder="Ex: Gymnase">
        </div>
        <div class="form-group">
          <label for="address">Adresse</label>
          <input type="text" id="address" v-model="formData.location.address" placeholder="Ex: 12 rue Paradis" />
        </div>
        <div class="form-group">
          <label for="city">Ville</label>
          <input type="text" id="city" v-model="formData.location.city" placeholder="Ex: Lille" required />
          <p v-if="errors.city" class="error-text">
            {{ errors.city }}
          </p>
        </div>
        <div class="form-group">
          <label for="zipCode">Code Postal</label>
          <input type="text" id="zipCode" v-model="formData.location.zipCode" placeholder="Ex: 59000" pattern="[0-9]{5}"
            title="Entrez 5 chiffres" />
        </div>
      </div>
    </div>

    <div class="forms-action">
      <button type="button" class="cancel-btn" @click="$emit('cancel')">Annuler</button>
      <button type="submit" class="submit-btn" :disabled="isSubmitting">
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
  console.log("Watch déclenché, nouvelle valeur :", newVal);
  if (newVal) {
    formData.value = {
      activityName: newVal.activityName,
      dayOfWeek: newVal.dayOfWeek,
      startTime: newVal.startTime?.substring(0, 5),
      endTime: newVal.endTime?.substring(0, 5),
      ageMin: newVal.ageMin,
      ageMax: newVal.ageMax,
      associationId: newVal.association.id,
      description: newVal.description,
      // Pour la Location
   location: {
        name: newVal.location?.name || "",
        address: newVal.location?.address || "",
        city: newVal.location?.city || "",
        zipCode: newVal.location?.zipCode || ""
      }
    };
  }
}, { immediate: true });

// SOUMISSION FORMULAIRE
const handleSubmit = () => {
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
