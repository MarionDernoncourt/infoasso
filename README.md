# INFO ASSO - Plateforme de gestion et de recherche d'activités pour les associations

**Projet personnel**  
Réalisation complète de A à Z : Conception, architecture, développement Backend/Frontend, tests automatisés et mise en place d'une expérience utilisateur accessible et sécurisée.

INFO ASSO est une application web permettant de centraliser les informations des associations, de gérer les plannings d'activités et de faciliter la recherche pour les citoyens, avec une gestion stricte des rôles et des droits de modification (propriétaires d'associations vs visiteurs).

---

## 🛠️ Technologies utilisées

### Backend
- **Java 17+** & **Spring Boot 3** (API REST)
- **Spring Security** & **JWT** (Authentification & Autorisation basée sur les rôles)
- **Spring Data JPA** & **H2 Database** (Base de données en mémoire pour le dev/test)
- **JUnit 5**, **AssertJ** & **Mockito** (Tests d'intégration et unitaires)

### Frontend
- **Vue.js 3** (Composition API, Vue Router)
- **Vitest** & **Vue Test Utils** (Tests unitaires et de composants front-end)
- **Styles & Accessibilité :** CSS moderne, conformité aux normes d'accessibilité (Attributs ARIA, gestion du focus, contrastes, `role="alert"`)

### Outils & Tooling
- **Git** & **GitHub**
- **Maven** & **npm**

---

## 🚀 Architecture et Fonctionnalités principales

L'application s'articule autour de plusieurs grands modules :

- **Gestion des Associations & Authentification :** Inscription, connexion sécurisée avec tokens JWT, gestion centralisée des sessions et déconnexion automatique en cas d'expiration du token (401).
- **Planning et Horaires des Activités :** Interface de consultation des plannings par association, filtres dynamiques de recherche.
- **Saisie et Contrôle Rigoureux :** Formulaires sécurisés avec sélection par listes déroulantes des quarts d'heure (00, 15, 30, 45) pour éviter les erreurs de saisie des horaires.
- **Sécurité contextuelle (`isOwner`) :** Affichage dynamique des boutons de modification et d'administration réservés uniquement au propriétaire de l'association connectée.
- **Accessibilité (a11y) :** Intégration poussée des attributs ARIA (`aria-label`, `aria-describedby`, `aria-modal`, `role`) sur l'ensemble des composants interactifs, modaux et formulaires.

---

## 🧪 Tests & Qualité Logicielle

Le projet intègre une suite de tests automatisés pour garantir la stabilité et la non-régression de l'application :

- **Backend (`mvn verify`) :** Tests d'intégration (`UserServiceIT`) validant le cycle de vie complet de l'utilisateur, la persistance JPA sous H2, et la régénération synchronisée des tokens JWT lors de la mise à jour du profil.
- **Frontend (`npm run test`) :** Tests unitaires de composants (`LoginView.spec.js`, `RegisterView.spec.js`) sous **Vitest** simulant le comportement utilisateur (validation de champs, gestion des erreurs d'accessibilité et mocks d'API).

---

## ⚙️ Configuration et Variables d'Environnement

Le projet utilise des fichiers de configuration sécurisés pour les variables sensibles :

1. À la racine du projet backend, un fichier `.env.example` est fourni comme modèle.
2. Dupliquez ce fichier et renommez-le en `.env`.
3. Renseignez les variables requises (identifiants de base de données, clés secrètes JWT, etc.) selon votre environnement local.

---

## 📦 Installation et Lancement

### Pré-requis
- **Java 17+** et **Maven**
- **Node.js** (v18+) et **npm**

### 1. Backend (Spring Boot)

```bash
# Clonez le dépôt
git clone <URL_DU_REPO>

# Accédez au répertoire backend
cd backend

# Configurez le .env
cp .env.example .env

# Lancer la suite de tests backend
./mvnw verify

# Lancer l'application
./mvnw spring-boot:run
```

### 2. Frontend (Vue.js)
```bash
# Accédez au répertoire frontend
cd frontend

# Installation des dépendances
npm install

# Lancer les tests unitaires front-end
npm run test

# Lancer l'application en mode développement
npm run dev
```

## 🔒 Sécurité et Bonnes Pratiques

- **Gestion des Sessions (JWT) :** Stockage des tokens et nettoyage automatique du localStorage (token, user_email, rôle) en cas d'interception d'une réponse 401 Unauthorized.

- **Contrôle d'accès :** Vérification systématique de la propriété d'une structure (isOwner) combinée aux rôles côté Back et Front pour interdire toute modification non autorisée.

- **Accessibilité Numérique :** Respect des bonnes pratiques d'ergonomics et d'accessibilité (ARIA) pour garantir une navigation fluide aux utilisateurs en situation de handicap (lecteurs d'écran, navigation au clavier).

### 💻 2. Lance ces 3 commandes dans ton terminal :

1. **Sauvegarde** le fichier dans IntelliJ.
2. Exécute :

```bash
git add README.md
git commit -m "docs: clean up merge markers in README"
git push origin main
```