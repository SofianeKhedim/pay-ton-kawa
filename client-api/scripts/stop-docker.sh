#!/bin/bash

echo "🛑 Arrêt des services Docker"

# Arrêt gracieux
echo "⏳ Arrêt gracieux des services..."
docker-compose stop

# Suppression des conteneurs
echo "🗑️ Suppression des conteneurs..."
docker-compose down

# Vérification
if [ "$(docker ps -q -f name=client-api)" ]; then
    echo "⚠️ Certains conteneurs sont encore actifs"
    docker ps -f name=client-api
else
    echo "✅ Tous les conteneurs sont arrêtés"
fi

# Option de nettoyage complet
read -p "🧹 Supprimer également les volumes de données ? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🗑️ Suppression des volumes..."
    docker-compose down -v
    echo "✅ Volumes supprimés"
else
    echo "💾 Volumes conservés"
fi

echo "✅ Arrêt terminé"