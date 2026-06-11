<template>
  <aside class="dashboard-sidebar">
    <div class="sidebar-brand">
      <h2>INFO ASSO</h2>
    </div>

    <nav class="sidebar-menu">
      <router-link to="/dashboard" class="menu-item" active-class="active">Fiche et Créneaux</router-link>
      <a href="#" class="menu-item disabled-future">Fil d'actualité <span class="badge-v2">V2</span></a>
      <a href="#" class="menu-item">Mon compte</a>
    </nav>

    <div class="sidebar-footer">
      <!--  Section Profil & Déconnexion -->
      <div class="user-section">
        <span class="user-email">👤 {{ userEmail || 'Mon Compte' }}</span>
        <button class="logout-btn" @click="logout">Déconnexion</button>
      </div>

      <!-- séparation discrète -->
      <hr class="footer-divider" />

      <div class="legal-section">
        <span class="menu-item disabled-future">
          Aide & Support
          <span class="badge-v2">V2</span>
        </span>

        <p class="copyright">© 2026 Tous droits réservés</p>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { useRouter } from "vue-router";
// 💡 Importe ton vrai service d'authentification (ajuste le chemin si besoin, ex: @/services/auth.service)
import authService from "@/services/auth.service";

defineProps({
  userEmail: String
});

const router = useRouter();

const logout = async () => {
  try {
    await authService.logout();
    router.push("/login");
  } catch (error) {
    console.error("Erreur lors de la déconnexion :", error);
    localStorage.removeItem("token");
    router.push("/login");
  }
};
</script>

<style scoped>
.dashboard-sidebar {
  width: 260px;
  background-color: #2c1a14;
  color: white;
  display: flex;
  flex-direction: column;
  padding: 30px 20px;
  box-sizing: border-box;
  position: fixed;
  height: 100vh;
  left: 0;
  top: 0;
}

.sidebar-brand h2 {
  margin: 0 0 40px 0;
  font-size: 1.6rem;
  font-weight: 900;
  letter-spacing: -0.5px;
  text-align: center;
  color: darksalmon;
}

.sidebar-menu {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
}

.menu-item {
  color: #ddd;
  text-decoration: none;
  padding: 12px 15px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s;
  text-align: left;
}

.menu-item:hover,
.menu-item.active {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
}

.menu-item.active {
  border-left: 4px solid darksalmon;
  background-color: rgba(255, 255, 255, 0.05);
}

.disabled-future {
  opacity: 0.6;
  cursor: not-allowed;
  display: flex;
  align-items: center;
}

.badge-v2 {
  background: #555;
  font-size: 0.7rem;
  padding: 2px 6px;
  border-radius: 10px;
  margin-left: auto;
}

.sidebar-footer {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.user-email {
  font-size: 0.85rem;
  color: #bbb;
  text-align: center;
}

.logout-btn {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.2s;
}

.logout-btn:hover {
  background: #d9534f;
  border-color: #d9534f;
}

/* Ligne de séparation optionnelle si tu utilises la balise <hr class="footer-divider"> */
.footer-divider {
  border: 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  margin: 5px 0;
}

/* Le nouveau lien Aide & Support */
.support-link {
  display: block;
  font-size: 0.85rem;
  color: darksalmon;
  /* Rappel de ta couleur fétiche */
  text-decoration: none;
  text-align: center;
  font-weight: 500;
  transition: color 0.2s;
}

.support-link:hover {
  color: white;
  /* Brille au survol */
  text-decoration: underline;
}

/* Le petit texte de Copyright */
.copyright {
  font-size: 0.75rem;
  color: #888;
  /* Un gris discret pour ne pas gêner la lecture */
  text-align: center;
  margin: 0;
}
</style>
