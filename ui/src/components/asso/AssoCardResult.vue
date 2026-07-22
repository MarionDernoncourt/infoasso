<template>
  <article class="info-card">
    <div class="card-header">
      <h3>{{ asso?.displayName }}</h3>
    </div>

    <div class="card-body">
      <div class="asso-profil">
        <!-- Regrouper les infos méta -->
        <div class="meta-info">
          <div class="info-group">
            <span class="category-badge">{{ asso?.categoryType }}</span>
            <span class="category-label">{{ asso?.categoryLabel }} </span>
          </div>
          <span class="city">{{ asso?.city }}</span>
        </div>

        <p class="description">{{ asso?.description || "Aucune description disponible pour cette association." }}</p>
      </div>
    </div>

    <div class="card-footer">
      <button
      class="details-btn"
      @click.stop="goToAssociationDetails"
      :aria-label="`Voir les détails de l'association ${asso?.displayName}`"
      >
        Voir les détails
      </button>
    </div>
  </article>
</template>

<script setup>
import { useRouter } from 'vue-router';



const props = defineProps({
  asso: {
    type: Object,
    required: true
  }
});
const router = useRouter();

const goToAssociationDetails = () => {
  router.push(`/association/${props.asso.id}`);
}

</script>

<style scoped>
.info-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(44, 26, 20, 0.05);
  border: 1px solid #f5ebe7;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  height: 100%;
box-sizing: border-box;
}

.info-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(44, 26, 20, 0.1);
}

.card-header h3 {
  margin: 0 0 10px 0;
  color: #2c1a14;
  font-size: 1.1rem;
  font-weight: 800;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.meta-info {
  display: flex;
  justify-content: space-between;
  font-size: 0.85rem;
  color: #666;
  gap: 8px;
  margin-bottom: 12px;
}
.info-group{
  width: 50%;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: left;
}

.category-badge {
  background-color: #fdf0eb;
  color: darksalmon;
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 700;
  border: 1px solid rgba(233, 150, 122, 0.15);
  margin-right: 10px;
}
.category-label{
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100px;
}

.city {
  font-size: 0.85rem;
  color: #7c6a63;
  display: flex;
  align-items: left;
}

.description {
  font-size: 0.9rem;
  color: #4a3b35;
  line-height: 1.4;
  margin-bottom: 20px;
  flex-grow: 1;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.details-btn {
  width: 100%;
  padding: 10px;
  background: darksalmon;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.2s;
}

.details-btn:hover {
  background: #e9967a;
  /* Un ton un peu plus clair ou saturé */
  filter: brightness(1.1);
}
</style>
