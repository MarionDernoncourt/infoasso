<template>
  <div class="public-detail-page" role="main" aria-label="Page fiche association">
    <AssoCard v-if="association" :asso="association" />

    <div v-else class="loading" role="status" aria-live="polite">
      Chargement de la fiche association...
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import AssoCard from '@/components/asso/AssoCard.vue';
import assoService from '@/services/asso.service';
import { useRoute } from 'vue-router';

const route = useRoute();
const association = ref(null);

onMounted(async () => {
  const assoId = route.params.id;
  try {
    association.value = await assoService.getById(assoId);
  } catch (error) {
    console.error("Erreur chargement association:", error);
  }
});


</script>

<style scoped>
.public-detail-page {
  padding: 40px;
  max-width: 800px;
  margin: 0 auto;
}


</style>
