# Build stage
FROM gradle:8.14.2-jdk17 AS build

WORKDIR /app

# Copier les fichiers de build
COPY build.gradle settings.gradle ./
COPY gradle gradle

# Télécharger les dépendances (mise en cache)
RUN gradle dependencies --no-daemon

# Copier le code source et builder
COPY src src
RUN gradle clean bootJar --no-daemon -x test

# Runtime stage - SANS -alpine !
FROM eclipse-temurin:17-jre

# Installer curl pour health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Créer utilisateur non-root
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Créer répertoires
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

WORKDIR /app

# Copier le JAR
COPY --from=build /app/build/libs/*.jar app.jar
RUN chown appuser:appuser app.jar

# Basculer vers utilisateur non-root
USER appuser

# Variables d'environnement
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC"

# Port
EXPOSE 8081

# Health check
# HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
#   CMD curl -f http://localhost:8081/api/v1/actuator/health || exit 1

# Démarrage
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]