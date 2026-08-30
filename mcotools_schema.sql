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
CREATE INDEX IF NOT EXISTS idx_deposit_source_file ON deposit(source_file);