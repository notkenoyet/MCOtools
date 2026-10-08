CREATE TABLE IF NOT EXISTS deposit (
    deposit_id   VARCHAR(50) PRIMARY KEY,
    partner      VARCHAR(255),
    file_name    VARCHAR(255),
    login        VARCHAR(100),
    type         VARCHAR(50),
    protocol     VARCHAR(50),
    statut       VARCHAR(50),
    code_erreur  VARCHAR(255),
    date_recept  TIMESTAMP,
    date_modif   TIMESTAMP,
    compte       VARCHAR(100),
    source_file  VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS request (
    req_id              BIGINT PRIMARY KEY,
    deposit_id           VARCHAR(50),
    canal                VARCHAR(50),
    affranchissement      VARCHAR(50),
    login                VARCHAR(100),
    etat                 VARCHAR(50),
    nb_plis              INTEGER,
    env2batch            VARCHAR(50),
    date_modif           TIMESTAMP,
    date_creation        TIMESTAMP,
    date_prod_esperee    TIMESTAMP,
    deblocage_envelope   TIMESTAMP,
    code_erreur          VARCHAR(255),
    flow                 VARCHAR(50),
    source_file          VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS batch (
    bat_id           BIGINT PRIMARY KEY,
    reference        VARCHAR(100),
    canal            VARCHAR(50),
    type             VARCHAR(50),
    affranchissement VARCHAR(50),
    etat             VARCHAR(50),
    etab             VARCHAR(50),
    nb_env           INTEGER,
    nb_feuille       INTEGER,
    nb_page          INTEGER,
    date_modif       TIMESTAMP,
    date_creation    TIMESTAMP,
    categorie        VARCHAR(50),
    bat_error_code   VARCHAR(50),
    source_file      VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_request_deposit_id ON request(deposit_id);
CREATE INDEX IF NOT EXISTS idx_request_etat ON request(etat);
CREATE INDEX IF NOT EXISTS idx_batch_etat ON batch(etat);
CREATE INDEX IF NOT EXISTS idx_request_source_file ON request(source_file);
CREATE INDEX IF NOT EXISTS idx_batch_source_file ON batch(source_file);
<<<<<<< HEAD
CREATE INDEX IF NOT EXISTS idx_deposit_source_file ON deposit(source_file);

-- ===== Service auth =====

CREATE TABLE IF NOT EXISTS utilisateur (
    id                 BIGSERIAL PRIMARY KEY,
    nom                VARCHAR(255) NOT NULL,
    email              VARCHAR(255) NOT NULL UNIQUE,
    telephone          VARCHAR(20),
    mot_de_passe_hash  VARCHAR(255) NOT NULL,
    actif              BOOLEAN NOT NULL DEFAULT false,
    token_activation   VARCHAR(255),
    date_creation      TIMESTAMP
);

CREATE TABLE IF NOT EXISTS rapport_genere (
    id                BIGSERIAL PRIMARY KEY,
    utilisateur_id    BIGINT NOT NULL REFERENCES utilisateur(id),
    source_file       VARCHAR(255) NOT NULL,
    date_generation   TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_utilisateur_email ON utilisateur(email);
CREATE INDEX IF NOT EXISTS idx_utilisateur_token_activation ON utilisateur(token_activation);
CREATE INDEX IF NOT EXISTS idx_rapport_genere_utilisateur ON rapport_genere(utilisateur_id);
=======
CREATE INDEX IF NOT EXISTS idx_deposit_source_file ON deposit(source_file);
>>>>>>> e3c48026bbc0dbf775127264bec033d380562877
