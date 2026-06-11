export default [
  {
    path: "/",
    name: "home",
    component: () => import("@/views/public/HomeView.vue"),
  },
  {
    path: "/search",
    name: "search-results",
    component: () => import("../views/public/SearchResultsView.vue"),
  },
  {
    path: "/association/:id",
    name: "asso-detail",
    component: () => import("../views/public/AssoDetailView.vue"),
    props: true,
  },
  // {
  //   path: "/contact",
  //   name: "contact",
  //   component: () => import("../views/public/ContactView.vue"),
  // },
];
