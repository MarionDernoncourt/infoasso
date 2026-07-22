<template>
  <article class="info-card">
    <div class="card-header">
      <h3>Informations Générales</h3>
      <button v-if="!isOwner" class="schedules-btn" @click="goToSchedules">Voir le planning</button>
      <slot name="actions"></slot>
    </div>

    <div class="card-body">
      <div class="asso-profile">
        <h4>{{ asso?.displayName }}</h4>
        <span class="category-badge">
          🏷️ {{ asso?.category?.name || 'Association' }}
        </span>
        <p class="asso-official-name">Nom officiel : {{ asso?.officialName }}</p>
        <p class="asso-rna">RNA : {{ asso?.rnaNumber }}</p>
      </div>
    </div>

    <hr class="divider">
    <div class="asso-details">
      <p>
        <strong>Adresse : </strong>
        {{ asso?.streetAddress }} {{ asso?.zipCode }} {{ asso?.city }}</p>

   <p>
        <strong>Téléphone :</strong>
        <a v-if="asso?.phoneNumber" :href="`tel:${asso.phoneNumber}`">{{ asso.phoneNumber }}</a>
        <span v-else>Non renseigné</span>
      </p>

      <p>
        <strong>Email public : </strong>
        <a v-if="asso?.email" :href="`mailto:${asso.email}`">{{ asso.email }}</a>
        <span v-else>Non renseigné</span>
      </p>

      <p class="asso-description"><strong>Description : </strong>{{ asso?.description || "Aucune description pour le moment."}}
        </p>
    </div>
  </article>
</template>

<script setup>
import { useRouter, useRoute } from "vue-router"
import { ref, onMounted } from 'vue';
import assoService from "@/services/asso.service";

const router = useRouter();
const route = useRoute();
const currentLoggedEmail = localStorage.getItem('user_email');
const isOwner = ref(false);

const props = defineProps({
  asso: { type: Object, required: true }
});
const goToSchedules = () => {
  router.push(`/association/${route.params.id}/scheduleView`);
};

const checkOwnerShip = async () => {
  try {
    const asso = await assoService.getById(props.asso.id);
    isOwner.value = (currentLoggedEmail && asso?.ownerEmail === currentLoggedEmail);
    console.log(isOwner.value, currentLoggedEmail);
  } catch (error) {
    console.error("Erreur de vérification propriétaire: ", error);
    isOwner.value = false;
  }
}

onMounted(async () => {
  checkOwnerShip();
})



</script>

<style scoped>
.info-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(44, 26, 20, 0.02);
  border: 1px solid #f5ebe7;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.card-header h3 {
  margin: 0;
  color: #2c1a14;
  font-size: 1.25rem;
  font-weight: 800;
}



.asso-profile h4 {
  margin: 0 0 8px 0;
  font-size: 1.6rem;
  font-weight: 800;
  color: #2c1a14;
}

.category-badge {
  display: inline-block;
  background-color: #fdf0eb;
  color: darksalmon;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 700;
  margin-bottom: 15px;
  border: 1px solid rgba(233, 150, 122, 0.15);
}

.asso-official-name,
.asso-rna {
  margin: 4px 0;
  font-size: 0.9rem;
  color: #7c6a63;
}

.divider {
  border: 0;
  border-top: 1px solid #f5ebe7;
  margin: 25px 0;
}

.asso-details p {
  margin: 14px 0;
  color: #4a3b35;
  font-size: 0.95rem;
  line-height: 1.5;
}

.asso-details strong {
  color: #2c1a14;
  font-weight: 600;
}

.asso-details a {
  color: darksalmon;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
}

.asso-details a:hover {
  text-decoration: underline;
}

.asso-description {
  background: #fdfbfb;
  padding: 16px;
  border-left: 4px solid darksalmon;
  border-radius: 0 8px 8px 0;
  font-style: italic;
  color: #5c4b44 !important;
  margin-top: 20px !important;
}
</style>
