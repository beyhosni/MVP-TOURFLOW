# TourFlow - Plateforme de réservation directe pour guides touristiques

<div align="center">
  <img src="https://img.shields.io/badge/Java-17-orange" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.1-brightgreen" alt="Spring Boot 3.1">
  <img src="https://img.shields.io/badge/PostgreSQL-15-blue" alt="PostgreSQL 15">
  <img src="https://img.shields.io/badge/React-18-blue" alt="React 18">
  <img src="https://img.shields.io/badge/TypeScript-5-blue" alt="TypeScript 5">
  <img src="https://img.shields.io/badge/Material%20UI-5-indigo" alt="Material-UI 5">
  <img src="https://img.shields.io/badge/Docker-20.10-blue" alt="Docker 20.10">
  <img src="https://img.shields.io/badge/Stripe-Purple" alt="Stripe">
</div>

## 📋 Description

TourFlow est une plateforme B2B SaaS de booking direct pour guides touristiques indépendants. Elle permet aux guides de créer et gérer leurs tours, définir leurs disponibilités, accepter des réservations et paiements en ligne, tout en évitant les doubles bookings.

### 🎯 Objectifs principaux

- ✅ Permettre à un guide de créer un tour, définir ses disponibilités, accepter des réservations et paiements
- ✅ Éviter les doubles bookings via un système transactionnel robuste
- ✅ Synchronisation externe via calendriers iCal (ICS)
- ✅ Interface moderne et intuitive pour guides et clients

---

## 🏗️ Architecture

<div align="center">
  <img src="https://img.shields.io/badge/Architecture-Client--Server-lightgrey" alt="Architecture">
</div>

### Backend

<div align="center">
  <img src="https://img.shields.io/badge/Java%2017-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Security">
  <img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Data JPA">
  <img src="https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/Stripe-635BFF?style=for-the-badge&logo=stripe&logoColor=white" alt="Stripe">
</div>

### Frontend

<div align="center">
  <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=white" alt="React">
  <img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript">
  <img src="https://img.shields.io/badge/Material%20UI-0081CB?style=for-the-badge&logo=mui&logoColor=white" alt="Material-UI">
  <img src="https://img.shields.io/badge/FullCalendar-00695C?style=for-the-badge" alt="FullCalendar">
</div>

### Infrastructure

<div align="center">
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker">
  <img src="https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white" alt="Nginx">
</div>

---

## 🚀 Démarrage rapide

### Prérequis

- Docker et Docker Compose
- Node.js 18+ (pour le développement frontend)
- Java 17+ (pour le développement backend)

### Installation

1. **Cloner le dépôt**
   ```bash
   git clone https://github.com/votre-organisation/tourflow.git
   cd tourflow
   ```

2. **Configurer les variables d'environnement**
   ```bash
   cp .env.example .env
   # Éditer .env avec vos clés Stripe et configuration email
   ```

3. **Démarrer avec Docker Compose**
   ```bash
   docker-compose up -d
   ```

L'application sera accessible à :
- Frontend : http://localhost:3000
- Backend API : http://localhost:8080
- Documentation Swagger : http://localhost:8080/swagger-ui.html

---

## 📊 Fonctionnalités

### 🧭 Gestion des Tours

- ✅ Création, modification, suppression de tours
- ✅ Photos, description, prix, capacité
- ✅ Gestion des langues et lieux

### 📅 Gestion des Disponibilités

- ✅ Règles récurrentes (jours, heures)
- ✅ Exceptions (dates bloquées)
- ✅ Délai minimum avant réservation
- ✅ Capacité maximale

### 🎫 Booking Direct

- ✅ Processus de réservation en 3 étapes
- ✅ Anti double-booking garanti
- ✅ Réservations expirables (10 min)
- ✅ Paiement sécurisé via Stripe

### 💳 Paiements Stripe

- ✅ Sessions Checkout
- ✅ Webhooks sécurisés
- ✅ Remboursements automatiques
- ✅ Notifications par email

### 📆 Calendriers

- ✅ Vue calendrier pour les guides
- ✅ Export iCal (compatible Airbnb/Viator/GetYourGuide)
- ✅ Import calendriers externes (MVP+)

---

## 🔒 Sécurité

- 🔐 Authentification JWT stateless
- 🛡️ Validation des entrées utilisateur
- 🔒 Chiffrement des mots de passe (bcrypt)
- 🚫 Protection contre les doubles bookings
- 📋 Conformité RGPD

---

## 📚 Documentation

- [Documentation Technique](./DOCUMENTATION_TECHNIQUE.md)
- [API Swagger](http://localhost:8080/swagger-ui.html) (après démarrage)

---

## 🤝 Contribuer

1. Forker le projet
2. Créer une branche (`git checkout -b feature/AmazingFeature`)
3. Committer les changements (`git commit -m 'Add some AmazingFeature'`)
4. Pusher vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

---

## 📄 Licence

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails.

---

## 👥 Équipe

- **Développeurs** : Équipe TourFlow
- **Contact** : contact@tourflow.com
- **Site web** : https://tourflow.com

---

<div align="center">
  <p>Made with ❤️ by TourFlow Team</p>
</div>

