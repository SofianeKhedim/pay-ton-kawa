.PHONY: help docker-build docker-start docker-stop docker-restart docker-logs docker-backup docker-test docker-clean

help: ## Affiche l'aide
	@echo "Commandes Docker disponibles:"
	@echo "  docker-deploy   - Déploie l'application Docker"
	@echo "  docker-start    - Démarre les services"
	@echo "  docker-stop     - Arrête les services"
	@echo "  docker-restart  - Redémarre les services"
	@echo "  docker-logs     - Affiche les logs"
	@echo "  docker-backup   - Sauvegarde la base de données"
	@echo "  docker-test     - Teste l'API"
	@echo "  docker-clean    - Nettoie les ressources"

docker-deploy: ## Déploiement complet Docker
	@./scripts/deploy-docker.sh

docker-start: ## Démarre les services Docker
	@echo "🚀 Démarrage des services Docker..."
	@docker-compose up -d

docker-stop: ## Arrête les services Docker
	@echo "🛑 Arrêt des services Docker..."
	@docker-compose down

docker-restart: docker-stop docker-start ## Redémarre les services

docker-logs: ## Affiche les logs de l'API
	@docker-compose logs -f client-api

docker-logs-all: ## Affiche tous les logs
	@docker-compose logs -f

docker-backup: ## Sauvegarde PostgreSQL
	@./scripts/backup-docker.sh

docker-test: ## Teste l'API Docker
	@./scripts/test-docker-api.sh

docker-clean: ## Nettoie les ressources Docker
	@echo "🧹 Nettoyage des ressources Docker..."
	@docker system prune -f
	@docker volume prune -f

docker-status: ## Statut des services
	@echo "📊 Statut des services Docker:"
	@docker-compose ps