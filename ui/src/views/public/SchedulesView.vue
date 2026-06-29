<template>
  <div class="public-schedule-page">

    <ScheduleFilter
    @filter-change="updateFilters" />

    <ScheduleTable
    :schedules="filteredSchedules"
    :loading="isLoading"
    />


  </div>
  </template>

  <script setup>
import {ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import ScheduleTable from '@/components/schedules/ScheduleTable.vue';
import ScheduleFilter from '@/components/schedules/ScheduleFilter.vue';
import schedulesService from '@/services/schedules.service';

const route = useRoute();

const allSchedules = ref([]);

onMounted(async () => {
  allSchedules.value = await schedulesService.getAll(route.params.id)
})
</script>
