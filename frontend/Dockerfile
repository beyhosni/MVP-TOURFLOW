# Étape 1: Installation des dépendances
FROM node:18-alpine AS deps

WORKDIR /app

# Copier les fichiers de package
COPY package*.json ./

# Installer les dépendances
RUN npm install

# Étape 2: Build de l'application
FROM node:18-alpine AS builder

WORKDIR /app

# Copier les dépendances installées
COPY --from=deps /app/node_modules ./node_modules

# Copier le code source
COPY . .

# Build de l'application
RUN npm run build

# Étape 3: Image de production avec Nginx
FROM nginx:alpine

# Copier le build de l'étape précédente
COPY --from=builder /app/build /usr/share/nginx/html

# Copier la configuration Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exposer le port 80
EXPOSE 80

# Lancer Nginx
CMD ["nginx", "-g", "daemon off;"]
