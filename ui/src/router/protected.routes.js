export default [
  {
    path: "/dashboard",
    name: "dashboard",
    component: () => import("@/views/protected/DashboardView.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/association/:id/updateSchedule/:scheduleId",
    name: "updateSchedule",
    component: () => import("@/views/protected/UpdateScheduleView.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/association/:id/createSchedule",
    name: "createSchedule",
    component: () => import("@/views/protected/CreateScheduleView.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/association/create",
    name: "create-asso",
    component: () => import("@/views/protected/CreateAssoView.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/association/update/:id",
    name: "update-asso",
    component: () => import("@/views/protected/UpdateAssoView.vue"),
    meta: { requiresAuth: true },
  },
];
