# Étape 1 : Build avec Maven + Java 17
FROM maven:3.9.4-eclipse-temurin-17 AS builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Runtime avec app.jar
FROM openjdk:17-jdk-slim

WORKDIR /app

RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

COPY wait-for.sh wait-for.sh
RUN chmod +x wait-for.sh

COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["./wait-for.sh", "mysql-container", "3306", "java", "-jar", "app.jar"]
