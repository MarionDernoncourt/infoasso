export default [
  {
    path: "/dashboard",
    name: "dashboard",
    component: () => import("@/views/protected/DashboardView.vue"),
  },
  {
    path: "/association/create",
    name: "create-asso",
    component: () => import("@/views/protected/CreateAssoView.vue"),
  },
  {
    path: "/association/update/:id",
    name: "update-asso",
    component: () => import("@/views/protected/UpdateView.vue"),
  },
];
