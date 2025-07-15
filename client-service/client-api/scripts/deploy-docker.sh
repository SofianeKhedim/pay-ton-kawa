#!/bin/bash

set -e

echo "🐳 Déploiement Docker - Client API PayeTonKawa"

# Variables
ENV_FILE=".env"
COMPOSE_FILE="docker-compose.yml"

# Vérifications préalables
echo "🔍 Vérifications préalables..."

if ! command -v docker &> /dev/null; then
    echo "❌ Docker n'est pas installé"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose n'est pas installé"
    exit 1
fi

# Vérification du fichier .env
if [ ! -f "$ENV_FILE" ]; then
    echo "⚠️ Fichier .env manquant, création..."
    ./scripts/generate-env.sh
fi

# Vérification des variables critiques
source "$ENV_FILE"
if [ -z "$DB_PASSWORD" ] || [ -z "$JWT_SECRET" ]; then
    echo "❌ Variables critiques manquantes dans .env"
    echo "💡 Lancez: ./scripts/generate-env.sh"
    exit 1
fi

echo "✅ Vérifications OK"

# Création des répertoires nécessaires
echo "📁 Création des répertoires..."
mkdir -p monitoring scripts logs

# Arrêt des services existants
echo "🛑 Arrêt des services existants..."
docker-compose down 2>/dev/null || true

# Nettoyage des images obsolètes
echo "🧹 Nettoyage des images obsolètes..."
docker system prune -f

# Construction et démarrage
echo "🔨 Construction des images..."
docker-compose build --no-cache

echo "🚀 Démarrage des services..."
docker-compose up -d

# Attente du démarrage
echo "⏳ Attente du démarrage complet..."
echo "   - PostgreSQL..."
sleep 30

echo "   - Application..."
sleep 60

# Vérification des services
echo "🔍 Vérification des services..."
docker-compose ps

# Tests de santé
echo "🏥 Tests de santé..."

# Test PostgreSQL
if docker exec client-api-postgres pg_isready -U payetonkawa_user -d payetonkawa_clients &> /dev/null; then
    echo "✅ PostgreSQL opérationnel"
else
    echo "❌ PostgreSQL non accessible"
    docker-compose logs postgres
    exit 1
fi

# Test API
max_attempts=20
attempt=1
echo "🧪 Test API (max $max_attempts tentatives)..."

while [ $attempt -le $max_attempts ]; do
    if curl -f http://localhost:8081/api/v1/users/health &> /dev/null; then
        echo "✅ API opérationnelle!"
        break
    else
        echo "⏳ Tentative $attempt/$max_attempts..."
        sleep 10
        ((attempt++))
    fi
done

if [ $attempt -gt $max_attempts ]; then
    echo "❌ L'API n'a pas démarré"
    echo "📋 Logs de l'API:"
    docker-compose logs client-api
    exit 1
fi


echo ""
echo "🎉 Déploiement Docker terminé avec succès!"
echo ""
echo "🌐 Services disponibles:"
echo "   - API: http://localhost:8081/api/v1"
echo "   - Health: http://localhost:8081/api/v1/actuator/health"
echo "   - Swagger: http://localhost:8081/swagger-ui.html"
echo "   - PostgreSQL: localhost:5432"
echo ""
echo "📊 Commandes utiles:"
echo "   - Logs API: docker-compose logs -f client-api"
echo "   - Logs DB: docker-compose logs -f postgres"
echo "   - Statut: docker-compose ps"
echo "   - Arrêt: docker-compose down"
