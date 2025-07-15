
-- Extensions PostgreSQL utiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Permissions
GRANT ALL PRIVILEGES ON DATABASE payetonkawa_clients TO payetonkawa_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO payetonkawa_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO payetonkawa_user;

-- Index pour les performances (seront créés par Liquibase)
-- Juste pour s'assurer que l'utilisateur a les permissions

---