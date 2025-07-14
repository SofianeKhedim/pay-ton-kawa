#!/bin/sh
host="$1"; port="$2"; shift 2
echo "⏳ Attente de $host:$port..."
until nc -z "$host" "$port"; do sleep 2; done
echo "✅ $host:$port prêt, on démarre !"
exec "$@"
