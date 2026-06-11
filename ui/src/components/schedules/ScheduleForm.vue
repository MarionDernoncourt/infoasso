<template>
  <form @submit.prevent="handleSubmit" class="schedule-form">
    <h2 class="form-title">
      Ajouter une activité (horaires)
    </h2>

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
    </div>

    <div class="form-section">
      <h3>Horaire de l'activité</h3>

      <div class="form-group">
        <label for="dayOfWeek">Jour</label>
        <select id="dayOfWeek" v-model="formData.dayOfWeek" required>
          <option value="" disabled>--Sélectionner le jour--</option>
          <option v-for="day in dayOfWeek" :key="day" :value="day"> {{ day }}</option>
        </select>
        <p v-if="errors.dayOfWeek" class="error-text">
          {{ errors.dayOfWeek }}
        </p>
      </div>

      <div class="form-row" style="display: flex; gap: 20px">
        <div class="form-group" style="flex: 1">
          <label for="startTime">Début</label>
          <input type="time" id="startTime" v-model="formData.startTime" required />
          <p v-if="errors.startTime" class="error-text">
            {{ errors.startTime }}
          </p>
        </div>
        <div class="form-group" style="flex: 1">
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

    <div class="forms-action">
      <button type="button" class="cancel-btn" @click="$emit('cancel')">Annuler</button>
      <button type="submit" class="submit-btn" :disabled="isSubmitting"> {{ isSubmitting ? 'Enregistrement...' :
        "Enregistrer" }}</button>

    </div>
  </form>
</template>


<script setup>
import { useRouter, useRoute } from "vue";
import { ref, onMounted } from 'vue';
import schedulesService from "@/services/schedules.service";

const route = useRoute();
const router = useRouter();

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
  }
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
})

onMounted(async () => {
  //Récupération des ENUMs pour le Select
  try {
    const response = await schedulesService.getDaysOfWeek();
    dayOfWeek.value = response.data;
  } catch (error) {
    console.error("Erreur lors de la récupération des jours de la semaine: ", error);
  }

  // Mode update //
  if (props.initialData) {
    formData.value = { ...props.initialData };
  }
});

// SOUMISSION FORMULAIRE
const handleSubmit = () => {
  emit("submit", formData.value);
}

</script>
