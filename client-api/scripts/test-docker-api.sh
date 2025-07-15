#!/bin/bash

set -e

echo "🧪 Tests de l'API Client - Docker"

API_URL="http://localhost:8081/api/v1"

# Test 1: Health check
echo "🏥 Test health check..."
response=$(curl -s -w "%{http_code}" -o /dev/null "$API_URL/actuator/health")
if [ "$response" -eq 200 ]; then
    echo "✅ Health check OK"
else
    echo "❌ Health check failed (code: $response)"
    exit 1
fi

# Test 2: Info endpoint
echo "📊 Test info endpoint..."
if curl -f "$API_URL/actuator/info" &> /dev/null; then
    echo "✅ Info endpoint OK"
else
    echo "⚠️ Info endpoint non accessible"
fi


# Test 4: Swagger UI
echo "📚 Test Swagger UI..."
if curl -f "$API_URL/swagger-ui.html" &> /dev/null; then
    echo "✅ Swagger UI accessible"
else
    echo "⚠️ Swagger UI non accessible (normal si désactivé en prod)"
fi

# Test 5: Base de données via API
echo "🗄️ Test connectivité base de données..."
# Note: Ce test nécessiterait un endpoint spécifique ou une authentification

echo ""
echo "🎉 Tests Docker terminés!"
echo ""
echo "🌐 URLs de test:"
echo "   - Health: $API_URL/actuator/health"
echo "   - Info: $API_URL/actuator/info"
echo "   - Swagger: $API_URL/swagger-ui.html"

