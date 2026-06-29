export default [
  {
    path: "/dashboard",
    name: "dashboard",
    component: () => import("@/views/protected/DashboardView.vue"),
  },
  {
    path: "/association/:id/updateSchedule/:scheduleId",
    name: "updateSchedule",
    component: () => import("@/views/protected/UpdateScheduleView.vue"),
  },
  {
    path: "/association/:id/createSchedule",
    name: "createSchedule",
    component: () => import("@/views/protected/CreateScheduleView.vue"),
  },
  {
    path: "/association/create",
    name: "create-asso",
    component: () => import("@/views/protected/CreateAssoView.vue"),
  },
  {
    path: "/association/update/:id",
    name: "update-asso",
    component: () => import("@/views/protected/UpdateAssoView.vue"),
  },
];
