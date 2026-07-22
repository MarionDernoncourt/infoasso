# INFO ASSO - Plateforme de gestion et de recherche d'activités pour les associations

**Projet personnel**  
Réalisation complète de A à Z : Conception, architecture, développement Backend/Frontend et mise en place d'une expérience utilisateur accessible et sécurisée.

INFO ASSO est une application web permettant de centraliser les informations des associations, de gérer les plannings d'activités et de faciliter la recherche pour les citoyens, avec une gestion stricte des rôles et des droits de modification (propriétaires d'associations vs visiteurs).

---

## 🛠️ Technologies utilisées

* **Backend :** Java, Spring Boot (API REST sécurisée avec Spring Security & JWT) 
* **Frontend :** Vue.js 3 (Composition API, Vue Router)
* **Styles & Accessibilité :** CSS moderne, conformité aux normes d'accessibilité (Attributs ARIA, gestion du focus, contrastes)
* **Outils & Versioning :** Git, GitHub

---

## 🚀 Architecture et Fonctionnalités principales

L'application s'articule autour de plusieurs grands modules :
* **Gestion des Associations & Authentification :** Inscription, connexion sécurisée avec tokens JWT, gestion centralisée des sessions et déconnexion automatique en cas d'expiration du token (401).
* **Planning et Horaires des Activités :** Interface de consultation des plannings par association, filtres dynamiques de recherche.
* **Saisie et Contrôle Rigoureux :** Formulaires sécurisés avec sélection par listes déroulantes des quarts d'heure (00, 15, 30, 45) pour éviter les erreurs de saisie des horaires.
* **Sécurité contextuelle (`isOwner`) :** Affichage dynamique des boutons de modification et d'administration réservés uniquement au propriétaire de l'association connectée.
* **Accessibilité (a11y) :** Intégration poussée des attributs ARIA (`aria-label`, `aria-describedby`, `aria-modal`, `role`) sur l'ensemble des composants interactifs, modaux et formulaires.

---

## ⚙️ Configuration et Variables d'Environnement

Le projet utilise des fichiers de configuration sécurisés pour les variables sensibles :
1. À la racine du projet backend, un fichier `.env.example` est fourni comme modèle.
2. Dupliquez ce fichier et renommez-le en `.env`.
3. Renseignez les variables requises (identifiants de base de données, clés secrètes JWT, etc.) selon votre environnement local.

> *Note : Le fichier `.env` contenant vos informations personnelles et sensibles est ignoré par Git et ne doit jamais être versionné.*

---

## 📦 Installation et Lancement

### Pré-requis
* **Java** (pour le Backend Spring Boot) 
* **Node.js et npm** (pour le Frontend Vue.js)

### 1. Backend (Spring Boot)
* Clonez le dépôt du projet. 
* Configurez votre fichier `.env` à partir du modèle `.env.example`.
* Lancez le serveur backend depuis votre IDE ou via votre terminal :  
  ```bash
  ./mvnw spring-boot:run
