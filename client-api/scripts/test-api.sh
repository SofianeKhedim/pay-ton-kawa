# scripts/test-api.sh

#!/bin/bash

set -e

echo "🧪 Tests de l'API Client avec authentification"

API_URL="http://localhost:8081/api/v1"

# Test 1: Health check (public)
echo "🏥 Test health check..."
if curl -f "$API_URL/users/health" &> /dev/null; then
    echo "✅ Health check OK"
else
    echo "❌ Health check failed"
    exit 1
fi

# Test 2: Endpoints publics
echo "📚 Test Swagger (public)..."
if curl -f "$API_URL/swagger-ui.html" &> /dev/null; then
    echo "✅ Swagger accessible"
else
    echo "⚠️ Swagger non accessible (normal si désactivé en prod)"
fi

# Test 3: Actuator (public dans votre config)
echo "📊 Test Actuator..."
if curl -f "$API_URL/actuator/health" &> /dev/null; then
    echo "✅ Actuator accessible"
else
    echo "❌ Actuator non accessible"
fi

# Test 4: Endpoint protégé (doit échouer sans auth)
echo "🔒 Test endpoint protégé (sans auth - doit échouer)..."
response=$(curl -s -w "%{http_code}" -o /dev/null "$API_URL/users/clients")
if [ "$response" -eq 401 ] || [ "$response" -eq 403 ]; then
    echo "✅ Sécurité fonctionnelle (401/403 sans token)"
else
    echo "⚠️ Réponse inattendue: $response"
fi

echo "🎉 Tests de base terminés!"
echo "💡 Pour tester avec authentification, utilisez Postman avec un token JWT"