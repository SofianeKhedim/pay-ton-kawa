# ---------- Étape 1 : build Maven ----------
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src src
RUN mvn -B clean package -DskipTests

# ---------- Étape 2 : image d'exécution ----------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# outil nc pour le wait-for (apt pour Ubuntu/Jammy, pas apk)
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

# --- script de veille ---
COPY wait-for.sh ./wait-for.sh
RUN chmod +x ./wait-for.sh

# --- jar Spring Boot ---
COPY --from=builder /build/target/*SNAPSHOT.jar /app/app.jar

EXPOSE 8089
ENTRYPOINT ["./wait-for.sh", "mysql-container", "3306", "--", "java", "-jar", "/app/app.jar"]