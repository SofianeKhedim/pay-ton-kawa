#!/bin/bash

set -e

echo "💾 Sauvegarde Docker - PostgreSQL"

# Variables
BACKUP_DIR="./backups"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/docker_postgres_${TIMESTAMP}.sql"
CONTAINER_NAME="client-api-postgres"

# Création du répertoire
mkdir -p "$BACKUP_DIR"

# Vérification du conteneur
if ! docker ps | grep -q "$CONTAINER_NAME"; then
    echo "❌ Conteneur PostgreSQL non trouvé ou arrêté"
    echo "💡 Lancez: docker-compose up -d postgres"
    exit 1
fi

# Sauvegarde
echo "📦 Création de la sauvegarde..."
docker exec "$CONTAINER_NAME" pg_dump -U payetonkawa_user -d payetonkawa_clients > "$BACKUP_FILE"

# Vérification
if [ -s "$BACKUP_FILE" ]; then
    # Compression
    echo "🗜️ Compression..."
    gzip "$BACKUP_FILE"
    
    # Nettoyage (garde les 10 dernières)
    echo "🧹 Nettoyage des anciennes sauvegardes..."
    find "$BACKUP_DIR" -name "docker_postgres_*.sql.gz" -type f -exec ls -1t {} + | tail -n +11 | xargs -r rm
    
    echo "✅ Sauvegarde créée: ${BACKUP_FILE}.gz"
    echo "📊 Taille: $(du -h "${BACKUP_FILE}.gz" | cut -f1)"
else
    echo "❌ Erreur lors de la sauvegarde"
    rm -f "$BACKUP_FILE"
    exit 1
fi