FROM node:18-alpine

WORKDIR /app

# Copier uniquement les fichiers nécessaires pour installer les dépendances
COPY package*.json ./
RUN npm install

# Copier le reste du code
COPY . .

# Copier le script wait-for.sh et le rendre exécutable
COPY wait-for.sh ./wait-for.sh
RUN chmod +x ./wait-for.sh

# Exposer le port de l'application
EXPOSE 3000

# Attendre que RabbitMQ soit prêt avant de démarrer app.js
CMD ["./wait-for.sh", "rabbitmq", "5672", "node", "src/app.js"]
