# Documentation Technique - TourFlow

## Architecture Générale

TourFlow est une plateforme B2B SaaS de booking direct pour guides touristiques indépendants, basée sur une architecture client-serveur avec les technologies suivantes :

- **Backend** : Java 17, Spring Boot, Spring Security (JWT), Spring Data JPA, PostgreSQL, Stripe
- **Frontend** : React, TypeScript, Material-UI, FullCalendar

## Architecture Backend

### Structure des Packages

```
com.tourflow
├── config          # Configuration de l'application (Security, CORS)
├── controller      # Contrôleurs REST (API)
├── model           # Entités JPA
├── repository      # Interfaces Spring Data JPA
└── service         # Logique métier
```

### Modèle de Données

#### Entités Principales

1. **User** : Représente un utilisateur (guide ou admin)
   - id (UUID)
   - email, password, firstName, lastName
   - role (GUIDE, ADMIN)
   - createdAt, lastLogin, active

2. **Tour** : Représente un tour proposé par un guide
   - id (UUID)
   - title, description, durationMinutes, location
   - maxCapacity, price, language
   - photoUrls (JSON)
   - createdAt, updatedAt, active
   - guide (ManyToOne vers User)

3. **AvailabilityRule** : Règles de disponibilité récurrentes
   - id (UUID)
   - daysOfWeek (EnumSet<DayOfWeek>)
   - startTimes (List<LocalTime>)
   - minBookingHours, maxCapacity
   - active, createdAt
   - tour (ManyToOne vers Tour)

4. **AvailabilityException** : Exceptions aux règles de disponibilité
   - id (UUID)
   - startDate, endDate, reason
   - createdAt
   - tour (ManyToOne vers Tour)

5. **Booking** : Réservation d'un tour
   - id (UUID)
   - startDate, endDate, participants, totalPrice
   - customerName, customerEmail, customerPhone
   - status (PENDING, CONFIRMED, CANCELLED, EXPIRED)
   - createdAt, expiresAt, confirmedAt, cancelledAt
   - cancellationReason, specialRequests
   - tour (ManyToOne vers Tour)
   - payment (OneToOne vers Payment)

6. **Payment** : Paiement associé à une réservation
   - id (UUID)
   - amount, currency
   - stripeSessionId, paymentIntentId
   - status (PENDING, COMPLETED, FAILED, REFUNDED)
   - createdAt, completedAt, failedAt
   - failureReason
   - booking (OneToOne vers Booking)

7. **ExternalCalendar** : Calendrier externe (iCal)
   - id (UUID)
   - name, icsUrl
   - createdAt, lastSyncAt, active
   - guide (ManyToOne vers User)

### API REST

#### Authentification (/api/auth)
- POST /register : Inscription d'un nouvel utilisateur
- POST /login : Connexion d'un utilisateur

#### Tours (/api/tours)
- GET / : Lister tous les tours actifs
- GET /{id} : Détails d'un tour
- GET /my-tours : Tours de l'utilisateur connecté
- GET /available : Tours disponibles pour une période
- POST / : Créer un tour (authentifié)
- PUT /{id} : Modifier un tour (authentifié)
- DELETE /{id} : Supprimer un tour (authentifié)

#### Disponibilités (/api/availability)
- Règles de disponibilité :
  - POST /rules : Créer une règle
  - PUT /rules/{id} : Modifier une règle
  - DELETE /rules/{id} : Supprimer une règle
  - GET /rules/tour/{tourId} : Règles d'un tour
- Exceptions de disponibilité :
  - POST /exceptions : Créer une exception
  - PUT /exceptions/{id} : Modifier une exception
  - DELETE /exceptions/{id} : Supprimer une exception
  - GET /exceptions/tour/{tourId} : Exceptions d'un tour
- Créneaux disponibles :
  - GET /slots/{tourId} : Créneaux disponibles

#### Réservations (/api/bookings)
- POST / : Créer une réservation
- GET /{id} : Détails d'une réservation
- POST /{id}/confirm : Confirmer une réservation
- POST /{id}/cancel : Annuler une réservation
- GET /tour/{tourId} : Réservations d'un tour
- GET /customer/{email} : Réservations d'un client

#### Paiements (/api/payments)
- POST /create-checkout-session : Créer une session Stripe
- GET /stripe-publishable-key : Clé publique Stripe
- POST /webhook/stripe : Webhook Stripe
- POST /{paymentId}/refund : Rembourser un paiement

#### Calendriers (/api/calendar)
- GET /ics/{guideId} : Exporter calendrier iCal
- POST /external : Ajouter calendrier externe
- PUT /external/{id} : Modifier calendrier externe
- DELETE /external/{id} : Supprimer calendrier externe
- GET /external : Calendriers externes

### Sécurité

- **Authentification JWT** : Tokens stateless avec expiration configurable
- **Autorisation** : Rôles (GUIDE, ADMIN)
- **Validation** : Validation des entrées utilisateur
- **CORS** : Configuration pour autoriser le frontend

### Anti Double-Booking

L'application garantit l'absence de double-booking grâce à :

1. **Vérification transactionnelle** : Les créneaux sont vérifiés en base de données avant réservation
2. **Vérification de capacité** : Le nombre de participants est validé
3. **Expiration automatique** : Les réservations en attente expirent après 10 minutes
4. **Verrouillage pessimiste** : Utilisation de transactions pour éviter les conflits

## Architecture Frontend

### Structure des Composants

```
src/
├── components/     # Composants réutilisables
├── contexts/       # Contextes React (Auth, etc.)
├── pages/          # Pages de l'application
├── services/       # Services API
└── utils/          # Utilitaires
```

### Pages Principales

1. **Home** : Page d'accueil présentant la plateforme
2. **Tours** : Liste des tours disponibles avec recherche
3. **TourDetails** : Détails d'un tour et bouton de réservation
4. **Booking** : Processus de réservation en 3 étapes (date, infos, paiement)
5. **Login/Register** : Authentification et inscription
6. **Dashboard** : Tableau de bord pour les guides (tours, réservations, stats)
7. **Calendar** : Gestion des disponibilités et calendriers externes
8. **Profile** : Gestion du profil utilisateur

### État Global

L'état global est géré avec :

- **Contexte d'authentification** : État de connexion et informations utilisateur
- **Contexte de réservation** : État du processus de réservation
- **Gestion locale** : État spécifique à chaque composant

### Intégration Stripe

L'intégration Stripe se fait via :

- **Stripe Elements** : Saisie sécurisée des informations de paiement
- **Stripe Checkout** : Redirection vers la page de paiement Stripe
- **Webhooks** : Notification des changements de statut de paiement

## Déploiement

### Docker

L'application est containerisée avec Docker :

- **Backend** : Image basée sur OpenJDK 17 avec Maven
- **Frontend** : Image multi-étapes (build + Nginx)
- **Base de données** : PostgreSQL 15

### Docker Compose

Le déploiement complet se fait via docker-compose :

1. **Base de données PostgreSQL** : Persistance des données
2. **Backend Spring Boot** : API REST
3. **Frontend Nginx** : Application React servie

## Fonctionnalités Clés

### Anti Double-Booking

Le système garantit l'absence de double-booking grâce à :

1. **Vérification en base de données** : Avant création de réservation
2. **Transactions atomiques** : Garantie de cohérence
3. **Expiration automatique** : Nettoyage des réservations expirées

### Synchronisation iCal

Les guides peuvent synchroniser leur calendrier avec :

1. **Export iCal** : URL publique pour les réservations confirmées
2. **Import iCal** : Lecture des calendriers externes (MVP+)
3. **Mapping statut** : Réservations confirmées → busy

### Flux de Réservation

1. **Sélection** : Client choisit tour, date, participants
2. **Création PENDING** : Réservation créée avec expiration 10min
3. **Paiement Stripe** : Session Checkout créée
4. **Webhook** : Paiement confirmé → réservation CONFIRMED
5. **Emails** : Notifications à chaque étape

## Évolutions Possibles

### Phase 2

1. **Synchronisation iCal active** : Import et parsing des calendriers externes
2. **Reviews/Notations** : Système d'évaluation des tours
3. **Notifications push** : Alertes temps réel pour les guides
4. **Gestion des documents** : Contrats, attestations, etc.

### Phase 3

1. **Multi-devises** : Support de plusieurs devises
2. **Multi-langues** : Interface multilingue
3. **Marketplace** : Place de marché entre guides et clients
4. **Mobile app** : Applications natives iOS/Android

## Sécurité

### RGPD

L'application respecte le RGPD via :

1. **Collecte minimale** : Seules les données nécessaires sont collectées
2. **Consentement explicite** : Acceptation claire des conditions
3. **Droit de suppression** : Possibilité de supprimer son compte
4. **Anonymisation** : Données anonymisées après suppression

### Sécurité Technique

1. **HTTPS** : Toutes les communications sont chiffrées
2. **JWT sécurisés** : Tokens avec expiration et rotation
3. **Validation stricte** : Toutes les entrées sont validées
4. **Mots de passe robustes** : Hashage bcrypt avec sel

## Monitoring

### Logs

L'application génère des logs structurés pour :

1. **Traçabilité** : Suivi des actions utilisateur
2. **Débogage** : Informations détaillées en cas d'erreur
3. **Analyse** : Métriques d'utilisation et performance
4. **Sécurité** : Tentatives d'accès non autorisées

### Métriques

Les métriques clés suivies sont :

1. **Taux de conversion** : Réservations / visites
2. **Temps de réponse** : Performance des APIs
3. **Taux d'erreur** : Fréquence des erreurs
4. **Adoption** : Nombre de guides et réservations
