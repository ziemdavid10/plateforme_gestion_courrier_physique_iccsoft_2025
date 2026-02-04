-- Script SQL pour créer la table d'historique des statuts de courrier
-- Cette table permet de tracer tous les changements d'état des courriers

CREATE TABLE IF NOT EXISTS courrier_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    courrier_id BIGINT NOT NULL,
    ancien_statut VARCHAR(50),
    nouveau_statut VARCHAR(50) NOT NULL,
    utilisateur VARCHAR(100) NOT NULL,
    date_changement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    commentaire TEXT,
    FOREIGN KEY (courrier_id) REFERENCES courriers(id) ON DELETE CASCADE,
    INDEX idx_courrier_id (courrier_id),
    INDEX idx_date_changement (date_changement)
);

-- Ajout d'une colonne email dans la table employes si elle n'existe pas
ALTER TABLE employes ADD COLUMN IF NOT EXISTS email VARCHAR(255);

-- Mise à jour des emails par défaut pour les utilisateurs existants
UPDATE employes SET email = CONCAT(username, '@iccsoft.com') WHERE email IS NULL OR email = '';