# MCOtools

Outil d'analyse des rapports MCO : parsing des fichiers de rapport (format fixed-width), stockage en base PostgreSQL, détection des demandes / lots / dépôts en erreur et génération de rapports de traitement (PDF).

Monorepo composé de 4 microservices Spring Boot et d'un frontend React.

## Architecture

| Module      | Port | Rôle |
|-------------|------|------|
| `mco`       | 8082 | Entités JPA + API REST CRUD (`/api/requests`, `/api/batches`, `/api/deposits`) |
| `parser`    | 8081 | Parse les fichiers de rapport et envoie les lignes à `mco` (`/api/parser/upload`, `/api/parser/parse-text`) |
| `treatment` | 8083 | Analyse des codes d'erreur (`/treatementRequest/{id}`, `/treatementBatch/{id}`, `/treatementDeposit/{id}`) et rapport (`/rapportTraitement`, `/rapportTraitement/pdf`) |
| `auth`      | 8084 | Inscription, confirmation par email, login JWT (`/api/auth/*`) et historique (`/api/historique`) |
| `frontend`  | 3000 | Interface React (Vite) : login, inscription, page principale, historique |

```
frontend (3000) ──► auth (8084) ──► PostgreSQL
        │
        └────────► parser (8081) ──► mco (8082) ──► PostgreSQL
                   treatment (8083) ──► mco (8082)
```

## Structure

```
MCOtools/
├── mco/                  # service données (CRUD)
├── parser/               # service de parsing
├── treatment/            # service de traitement / rapports
├── auth/                 # authentification + historique
├── frontend/             # React + Vite
├── data/                 # exemples de fichiers de rapport (.txt)
├── generate_reports.py   # génère un PDF par fichier de data/
└── mcotools_schema.sql   # schéma PostgreSQL
```

## Prérequis

- Java 17
- Maven 3.9+
- PostgreSQL
- Node.js 18+
- Python 3 + `requests` (pour `generate_reports.py`)

## Installation

### 1. Base de données

```bash
createdb -U postgres mcotools
psql -U postgres -d mcotools -f mcotools_schema.sql
```

### 2. Configuration

Dans `mco/` et `auth/`, éditer `src/main/resources/application.properties` :

- `spring.datasource.username` / `spring.datasource.password` : identifiants PostgreSQL
- `auth` uniquement : `spring.mail.username` / `spring.mail.password` (mot de passe d'application Gmail), `app.jwt-secret` (valeur aléatoire de 32+ caractères)

> ⚠️ Ne jamais committer de vrais mots de passe ou secrets. Utiliser des variables d'environnement ou un fichier local non versionné.

### 3. Lancer les services

Chaque service dans un terminal séparé :

```bash
cd mco       && mvn spring-boot:run
cd parser    && mvn spring-boot:run
cd treatment && mvn spring-boot:run
cd auth      && mvn spring-boot:run
```

### 4. Lancer le frontend

```bash
cd frontend
npm install
npm run dev     # http://localhost:3000
```

## Utilisation

- Via l'interface : créer un compte, confirmer l'email, se connecter, charger un fichier de rapport.
- Via l'API :
  ```bash
  curl -F "file=@data/MCOtools_20260624.txt" http://localhost:8081/api/parser/upload
  curl http://localhost:8083/rapportTraitement
  ```
- Génération en lot (un PDF par fichier de `data/`, écrit dans `reports/`) :
  ```bash
  python3 generate_reports.py
  ```

## Logs

Les services écrivent dans `logs/` (SLF4J + Logback, rotation quotidienne, fichiers `*-error.log` séparés). Ce dossier n'est pas versionné.

## Auteur

Mouad Benfilali — [@notkenoyet](https://github.com/notkenoyet)
