-- Script SQL pour créer la table des pièces jointes
CREATE TABLE IF NOT EXISTS pieces_jointes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    courrier_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    content_type VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (courrier_id) REFERENCES courriers(id) ON DELETE CASCADE
);

-- Index pour améliorer les performances
CREATE INDEX idx_pieces_jointes_courrier_id ON pieces_jointes(courrier_id);