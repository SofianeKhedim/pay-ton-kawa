#!/bin/bash

# Script wait-for.sh corrigé pour Docker
set -e

host="$1"
port="$2"
shift 2
cmd="$@"

echo "⏳ Attente de $host:$port..."

until nc -z "$host" "$port"; do
  echo "En attente de $host:$port..."
  sleep 1
done

echo "✅ $host:$port prêt, on démarre !"

# Exécution de la commande
exec $cmd