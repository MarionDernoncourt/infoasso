export default [
  {
    path: "/dashboard",
    name: "dashboard",
    component: () => import(".../views/protected/DashboardView.vue"),
  },
  {
    path: "/assocation/create",
    name: "create-asso",
    component: () => import(".../views/protected/CreateAssoView.vue"),
  },
];
