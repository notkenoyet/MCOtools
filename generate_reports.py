#!/usr/bin/env python3
"""
Genere un rapport PDF par fichier .txt du dossier data/.

Pour chaque fichier data/<nom>.txt :
  1. l'envoie a parser (POST /api/parser/upload) qui le parse et pousse
     les lignes vers mco, taguees avec source_file=<nom>.txt
  2. demande a treatment le rapport PDF filtre sur ce source_file
     (GET /rapportTraitement/pdf?sourceFile=<nom>.txt)
  3. sauvegarde le resultat dans reports/<nom>.pdf

Pre-requis : mco (8082), parser (8081) et treatment (8083) doivent deja
tourner (mvn spring-boot:run dans chaque module), et la base doit avoir
la colonne source_file (voir mcotools_schema.sql).

Usage :
    python3 generate_reports.py
"""

import sys
from pathlib import Path

import requests

SCRIPT_DIR = Path(__file__).resolve().parent
DATA_DIR = SCRIPT_DIR / "data"
REPORTS_DIR = SCRIPT_DIR / "reports"

PARSER_BASE_URL = "http://localhost:8081"
TREATMENT_BASE_URL = "http://localhost:8083"


def upload_to_parser(txt_path: Path) -> None:
    with open(txt_path, "rb") as f:
        files = {"file": (txt_path.name, f, "text/plain")}
        resp = requests.post(f"{PARSER_BASE_URL}/api/parser/upload", files=files, timeout=60)
    resp.raise_for_status()
    summary = resp.json()
    print(
        f"  parser  -> {summary.get('requestsPushed', 0)} request(s), "
        f"{summary.get('batchesPushed', 0)} batch(es), "
        f"{summary.get('depositsPushed', 0)} deposit(s) pousse(s)"
        + (f" | erreurs: {summary.get('errors')}" if summary.get("errors") else "")
    )


def download_pdf(source_file: str, out_path: Path) -> None:
    resp = requests.get(
        f"{TREATMENT_BASE_URL}/rapportTraitement/pdf",
        params={"sourceFile": source_file},
        timeout=60,
    )
    resp.raise_for_status()
    out_path.write_bytes(resp.content)
    print(f"  treatment -> {out_path.name} ({len(resp.content)} octets)")


def main() -> int:
    if not DATA_DIR.is_dir():
        print(f"Dossier introuvable: {DATA_DIR}", file=sys.stderr)
        return 1

    txt_files = sorted(DATA_DIR.glob("*.txt"))
    if not txt_files:
        print(f"Aucun fichier .txt trouve dans {DATA_DIR}")
        return 0

    REPORTS_DIR.mkdir(exist_ok=True)

    print(f"{len(txt_files)} fichier(s) a traiter\n")
    failures = []

    for txt_path in txt_files:
        print(f"[{txt_path.name}]")
        try:
            upload_to_parser(txt_path)
            pdf_name = txt_path.stem + ".pdf"
            download_pdf(txt_path.name, REPORTS_DIR / pdf_name)
        except requests.RequestException as e:
            print(f"  ERREUR: {e}", file=sys.stderr)
            failures.append(txt_path.name)
        print()

    print(f"Termine: {len(txt_files) - len(failures)}/{len(txt_files)} fichier(s) traite(s) avec succes")
    if failures:
        print(f"Echecs: {failures}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
