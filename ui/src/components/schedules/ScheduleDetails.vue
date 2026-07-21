<template>
  <div class="overlay" @click.self="$emit('close')">
    <div class="detail-card">
      <div class="form-group">
      <button class="close-btn" @click="$emit('close')">×</button>

      <h2>{{ activity.activityName }}</h2>
      <hr />
</div>
      <div class="description-box">
        <p>{{ activity.description || 'Aucune description fournie.' }}</p>
      </div>

    <div class="info-grid">
  <div class="row">
    <p><strong>Jour :</strong> {{ activity.dayOfWeek }}</p>
    <p><strong>Horaires :</strong> {{ activity.startTime }} - {{ activity.endTime }}</p>
  </div>
  <p><strong>Âge :</strong> De {{ activity.ageMin }} à {{ activity.ageMax }} ans</p>
</div>

      <div class="location-box">
        <h4>Lieu : {{ activity.location?.name }}</h4>
        <p>{{ activity.location?.address }}</p>
        <p>{{ activity.location?.zipCode }} {{ activity.location?.city }}</p>
      </div>

<button class="update-schedule"  @click="$emit('updateSchedule')">Modifier l'activité</button>
    </div>
  </div>
</template>

<script setup>
defineProps({ activity: Object });
defineEmits(['close']);
</script>

<style scoped>
/* L'overlay couvre tout l'écran */
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

/* La carte blanche centrée */
.form-group {
     display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
    border-bottom: 1px solid #2c1a14;
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

.info-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin: 20px 0;
}
.info-grid .row {
  display: flex;
  justify-content: space-between;
  gap: 20px;
}

.info-grid p {
  margin: 0;
}

.location-box {
  background: #fdf0eb;
  padding: 15px;
  border-radius: 8px;
  margin: 15px 0;
  border: 1px solid #f4d1c1;
}

.description-box {
  margin-top: 20px;
  color: #4a4a4a;
  line-height: 1.5;
}

.close-btn {
  position: absolute;
  top: 10px;
  right: 15px;
  cursor: pointer;
  border: none;
  background: none;
  font-size: 1.8rem;
  color: #5a3e36;
  transition: transform 0.2s;
}

.close-btn:hover {
  color: darksalmon;
  margin: 0px;
}

h2 {
  color: #2c1a14;
  margin-top: 0;
}

h4 {
  margin: 0 0 8px 0;
  color: #5a3e36;
}
</style>
