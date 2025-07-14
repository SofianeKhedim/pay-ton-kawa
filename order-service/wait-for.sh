#!/bin/sh

# Attendre que le service (host:port) soit prêt avant d'exécuter une commande

host="$1"
port="$2"
shift 2
cmd="$@"

echo "⏳ Attente de $host:$port..."

while ! nc -z "$host" "$port"; do
  echo "🔄 $host:$port pas encore prêt - attente..."
  sleep 2
done

echo "✅ $host:$port est prêt - lancement de la commande : $cmd"
exec $cmd
