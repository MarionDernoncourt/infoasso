<template>
  <div class="schedule-container">

    <!-- 1. LA GRILLE (FOND) -->
    <div class="schedule-grid">
      <div class="header-cell"></div>
      <div v-for="day in days" :key="day" class="header-cell" style="grid-column: span 4">
        {{ day }}
      </div>

      <template v-for="q in quarters" :key="q.id">
        <div :class="q.isStartOfHour ? 'time-cell' : 'time-cell-empty'">
          {{ q.isStartOfHour ? q.hour + "h" : '' }}
        </div>
        <div v-for="n in 28" :key="n" :class="[
          'slot-cell',
          {
            'day-start': (n - 1) % 4 === 0,
            'row-end': n === 28,
            'hour-line': q.isStartOfHour
          }]"></div>
      </template>
    </div>

    <!-- 2. LA COUCHE D'ACTIVITÉS (SUPERPOSÉE) -->
    <div class="activities-layer">
      <!-- C'est ICI que tu utilises positionedSchedules -->
      <div v-for="s in positionedSchedules"
      :key="s.id"
      :class="['activity-card', s.sizeClass]"
        :style="{ gridRow: s.gridRow, gridColumn: s.gridColumn }"
        :title="s.activityName"
        @click="$emit('select-activity', s)"

     >
        <span class="activity-text">{{ s.activityName }}</span>

      </div>
    </div>
  </div>
</template>


<script setup>
import { computed, watch } from "vue";

const days = ["Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"];
const props = defineProps(['schedules']);

watch(() => props.schedules, (newData) => {
  console.log("nouveaux schedules reçus: ", newData);
}, { deep: true });
const positionedSchedules = computed(() => {
  return props.schedules.map((s) => {
    // 1. Conflits
    const conflicts = props.schedules.filter(other =>
      other.dayOfWeek === s.dayOfWeek &&
      other.id !== s.id &&
      (other.startTime < s.endTime && other.endTime > s.startTime)
    );

    const fullGroup = [s, ...conflicts].sort((a, b) => a.id - b.id);
    const totalOnSlot = fullGroup.length;
    const myRank = fullGroup.findIndex(item => item.id === s.id);

    // 2. Calcul du span (largeur)
    let span = 4;
    if (totalOnSlot === 2) span = 2;
    else if (totalOnSlot >= 3) span = 1;

    // --- ICI : Le calcul de sizeClass doit être après le calcul de span ---
    const sizeClass = span === 4 ? 'size-large' : 'size-small';

    // 3. Calcul strict de la colonne de départ
    const dayIndex = days.indexOf(s.dayOfWeek);
    const baseCol = (dayIndex * 4) + 2;
    const startCol = baseCol + (myRank * span);
    const safeStartCol = Math.min(startCol, baseCol + (4 - span));

    // Calcul lignes
    const [startH, startM] = s.startTime.split(":").map(Number);
    const [endH, endM] = s.endTime.split(":").map(Number);
    const startMinutes = (startH - 6) * 60 + startM;
    const endMinutes = (endH - 6) * 60 + endM;

    return {
      ...s,
      gridRow: `${(startMinutes / 15) + 2} / span ${(endMinutes - startMinutes) / 15}`,
      gridColumn: `${safeStartCol} / span ${span}`,
      sizeClass
    };

  });

});


// Génération des 15 minutes (pour 17h: de 6 à 22h)
const quarters = computed(() => {
  const list = [];
  for (let h = 6; h < 23; h++) {
    for (let m of [0, 15, 30, 45]) {
      list.push({
        hour: h,
        minute: m,
        isStartOfHour: m === 0,
        id: `${h}-${m}`
      });
    }
  }
  return list;
})



</script>


<style scoped>
:root {
  --primary-bg: #2c1a14;
  --grid-border: #ccc;
  --hour-col-width: 80px;
}

.schedule-container {
  display: grid;
  grid-template-areas: "main";
  width: 100%;
  position: relative;
}

.schedule-grid,
.activities-layer {
  grid-area: main;
  display: grid;
  grid-template-columns: 80px repeat(28, 1fr);
grid-auto-rows: 20px;
  width: 100%;
  min-width: 800px;

}

.schedule-grid {
  position: relative;
  z-index: 1;
}

/* 3. Couche d'activités : doit être au-dessus */
.activities-layer {
  z-index: 2;
  pointer-events: none;
  /* Laisse passer les clics vers le fond */
}

/* 4. En-tête des jours */
.header-cell {
  font-weight: bold;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 10px;
  background-color: #f0f0f0;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background-color: var(--primary-bg);
  color: black;
}

.header-cell:nth-child(n + 2):nth-child(-n + 8) {
  border-bottom: 1px solid #2c1a14;
}

/* 5. Cellules des heures */
.time-cell {
  display: flex;
  justify-content: center;
  font-size: 0.8em;
  color: black;
}


/* 6. Grille de fond (Bordures uniquement pour les jours) */
.slot-cell {
  border-bottom: 1px solid #eee;
}

/* On compte seulement les éléments qui ont la classe .slot-cell.
   Puisque tu as 4 sous-colonnes par jour, la 1ère colonne de chaque jour
   est aux positions : 2, 6, 10, 14, 18, 22, 26 (parmi les .slot-cell).
*/
.day-start {
  border-left: 2px solid #2c1a14;
}

.row-end {
  border-right: 2px solid #2c1a14;
}

.slot-cell.hour-line {
  border-top: 1px solid  #2c1a14;
}

.hour-divider-top {
  border-top: 1.5px solid #2c1a14 !important;
}


/* 7. Carte d'activité */
.activity-card {
  background-color: rgba(244, 209, 193, 0.85);
  backdrop-filter: blur(2px);
  color: #5a3e36;
  border-radius: 4px;
  padding: 5px;
  font-size: 1em;
  z-index: 10;
  border: 1px solid rgba(244, 209, 193, 0.5);
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.3s ease, transform 0.3s ease;
  pointer-events: auto;
  grid-row: var(--row);
  width: 100%;
  box-sizing: border-box;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  pointer-events: auto;
  cursor: pointer;
}



/* Optionnel : ajoute un curseur "pointer" pour indiquer que c'est cliquable/survolable */
.size-small {
  cursor: help;
}

.activity-card:hover {
  background-color: rgba(244, 209, 193, 1);
  cursor: pointer;
  z-index: 100;
}

</style>
