FROM openjdk:17-jdk-slim

WORKDIR /app

RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

COPY target/mspr4-0.0.1-SNAPSHOT.jar app.jar
COPY wait-for.sh wait-for.sh
RUN chmod +x wait-for.sh

ENTRYPOINT ["./wait-for.sh", "mysql-container", "3306", "java", "-jar", "app.jar"]
