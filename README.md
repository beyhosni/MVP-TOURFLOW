# TourFlow - Plateforme B2B SaaS de booking direct pour guides touristiques

## Description
TourFlow est une plateforme B2B SaaS qui permet aux guides touristiques indépendants de gérer leurs réservations directement, sans passer par des plateformes intermédiaires.

## Fonctionnalités principales
- Création et gestion de tours
- Gestion des disponibilités
- Réservations directes
- Paiement sécurisé via Stripe
- Export de calendrier au format iCal
- Synchronisation avec calendriers externes

## Stack technique
### Backend
- Java 17
- Spring Boot
- Spring Web
- Spring Security (JWT)
- Spring Data JPA
- PostgreSQL
- Stripe (paiements)
- Docker

### Frontend
- React
- TypeScript
- Material-UI (MUI) ou Tailwind CSS
- FullCalendar

## Architecture
Le projet est structuré en deux modules principaux :
- `backend/` : API REST Spring Boot
- `frontend/` : Application React TypeScript

## Installation et démarrage
### Backend
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm start
```

## Documentation API
Une fois le backend démarré, l'API est accessible via Swagger UI : http://localhost:8080/swagger-ui.html

## License
[Type de licence]

