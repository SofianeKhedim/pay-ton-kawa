#!/bin/bash

echo "🔐 Génération du fichier .env pour Docker"

# Génération des mots de passe sécurisés
DB_PASSWORD=$(openssl rand -base64 32 | tr -d "=+/" | cut -c1-32)
JWT_SECRET="payetonkawa-jwt-$(openssl rand -hex 32)"

# Création du fichier .env
cat > .env << EOF
# =============================================================================
# Configuration Docker - Client API PayeTonKawa
# Généré le $(date)
# =============================================================================

# Base de données PostgreSQL
DB_NAME=payetonkawa_clients
DB_USERNAME=payetonkawa_user
DB_PASSWORD=$DB_PASSWORD

# JWT Configuration
JWT_SECRET=$JWT_SECRET

# Application
SERVER_PORT=8081

# Environnement
ENVIRONMENT=production
EOF

# Permissions restrictives
chmod 600 .env

echo "✅ Fichier .env créé avec succès"
echo "🔐 DB Password: $DB_PASSWORD"
echo "🔑 JWT Secret: ${JWT_SECRET:0:20}..."
echo "🔒 Permissions restrictives appliquées"
echo ""
echo "⚠️ IMPORTANT: Sauvegardez ces informations en lieu sûr!"