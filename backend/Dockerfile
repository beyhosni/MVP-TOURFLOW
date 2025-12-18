# Étape 1: Build de l'application avec Maven
FROM maven:3.8-openjdk-17 AS build

WORKDIR /app

# Copier le fichier pom.xml
COPY pom.xml .

# Télécharger les dépendances
RUN mvn dependency:go-offline

# Copier le code source
COPY src ./src

# Builder l'application
RUN mvn clean package -DskipTests

# Étape 2: Création de l'image de production
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copier le JAR de l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port 8080
EXPOSE 8080

# Lancer l'application
CMD ["java", "-jar", "app.jar"]
