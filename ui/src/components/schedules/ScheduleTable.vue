<template>
  <div class="schedule-container">
    <div class="schedule-grid">
      <div class="header-cell"></div>
      <div v-for="day in days" :key="day" class="header-cell">{{ day }}</div>

      <template v-for="q in quarters" :key="q.id">
        <div :class="q.isStartOfHour ? 'time-cell' : 'time-cell-empty'">
          {{ q.isStartOfHour ? q.hour + "h" : '' }}
        </div>
<div v-for="day in days" :key="day" :class="['slot-cell', {'hour-divider-top': q.isStartOfHour }]"></div>      </template>
    </div>

    <div class="activities-layer">
      <div
        v-for="s in schedules"
        :key="s.id"
        class="activity-card"
        :style="getGridPosition(s)"
      >
        {{ s.activityName }}
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
  }, {deep: true});

// Génération des 15 minutes (pour 17h: de 6 à 22h)
const quarters = computed(() => {
  const list = [];
  for (let h = 6; h < 23; h++) {
    for(let m of [0, 15, 30, 45]) {
      list.push({
        hour: h,
        minute: m,
        isStartOfHour: m ===0,
        id: `${h}-${m}`
      });
    }
  }
  return list;
})

const getColumnIndex = (dayName) => {
  const index = days.indexOf(dayName);
  return index + 2;
}

const getGridPosition = (schedule) => {
  const [startH, startM] = schedule.startTime.split(":").map(Number);
  const [endH, endM] = schedule.endTime.split(":").map(Number);

  //Calcul du temps total en minut depuis 6h00
  const startMinutes = (startH - 6) * 60 + startM;
  const endMinutes = (endH - 6) * 60 + endM;

  //Chaque row == 15 minutes -> Division par 15.
  // Ajout de +1 pour lignes CSS Grid qui commencent par 1 (et non pas 0 !!)
  const startRow = (startMinutes / 15) + 2;
  const rowSpan = (endMinutes - startMinutes) / 15;

  return {
    gridRow: `${startRow} / span ${rowSpan}`,
    gridColumn: getColumnIndex(schedule.dayOfWeek) // (2 pour lundi, 3 mardi, ...)
  };
};



</script>


<style scoped>
:root {
  --primary-bg: #2c1a14;
  --accent-color: darksalmon;
}
.schedule-container {
  display: grid;
  grid-template-areas: "main";
}
.schedule-grid {
  position: relative;
  z-index: 1;
}
.schedule-grid, .activities-layer {
  grid-area: main;
  display: grid;
  grid-template-columns: 80px repeat(7, 1fr);
  grid-auto-rows: 20px;
}
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

.time-cell {
  display: flex;
  justify-content: center;
  font-size: 0.8em;
  color: black;
  border-right: 1px solid #ddd;
}
.time-cell-empty{
  border-right: 1px solid #ddd;
}
.slot-cell:nth-last-child(-n+7),
.time-cell:last-child {
  border-bottom: 1px solid black !important;
}
.slot-cell {
  border: 1px solid #eee;
  border-right: 1px solid black;
  border-left: 1px solid black;
}



.activities-layer {
  z-index: 2;
  pointer-events: none;
}
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
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  transition: box-shadow 0.3s ease, transofrm 0.3s ease;
  pointer-events: auto;
  grid-row: var(--start-row);
  grid-column: var(--col);
  pointer-events: auto;
}
.activity-card:hover {
  cursor: pointer;
  background-color: rgba(244, 209, 193, 1);
box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}
.hour-divider{
  border-bottom: 1px solid  #555 !important;
  font-weight: bold;
}
.hour-divider-top {
  border-top: 1.5px solid #2c1a14 !important;
}

</style>
