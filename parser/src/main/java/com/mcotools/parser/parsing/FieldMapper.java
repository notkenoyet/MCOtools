package com.mcotools.parser.parsing;

import java.util.HashMap;
import java.util.Map;

/**
 * Normalizes the raw column headers found in the report dumps (which vary
 * slightly between snapshots, e.g. "Code_Erreur" vs "Code Erreur") into a
 * stable canonical field key used across the parser.
 */
public final class FieldMapper {

    private static final Map<String, String> NORMALIZED_TO_FIELD = new HashMap<>();

    static {
        // Request table
        NORMALIZED_TO_FIELD.put("canal", "canal");
        NORMALIZED_TO_FIELD.put("reqid", "reqId");
        NORMALIZED_TO_FIELD.put("depositid", "depositId");
        NORMALIZED_TO_FIELD.put("affranch", "affranchissement");
        NORMALIZED_TO_FIELD.put("login", "login");
        NORMALIZED_TO_FIELD.put("etat", "etat");
        NORMALIZED_TO_FIELD.put("nbplis", "nbPlis");
        NORMALIZED_TO_FIELD.put("env2batch", "env2Batch");
        NORMALIZED_TO_FIELD.put("datemodif", "dateModif");
        NORMALIZED_TO_FIELD.put("datecreation", "dateCreation");
        NORMALIZED_TO_FIELD.put("dateprodesperee", "dateProdEsperee");
        NORMALIZED_TO_FIELD.put("deblocageenvelope", "deblocageEnvelope");
        NORMALIZED_TO_FIELD.put("codeerreur", "codeErreur");

        // Batch table
        NORMALIZED_TO_FIELD.put("batid", "batId");
        NORMALIZED_TO_FIELD.put("reference", "reference");
        NORMALIZED_TO_FIELD.put("type", "type");
        NORMALIZED_TO_FIELD.put("etab", "etab");
        NORMALIZED_TO_FIELD.put("nbenv", "nbEnv");
        NORMALIZED_TO_FIELD.put("nbfeuille", "nbFeuille");
        NORMALIZED_TO_FIELD.put("nbpage", "nbPage");
        NORMALIZED_TO_FIELD.put("categorie", "categorie");
        NORMALIZED_TO_FIELD.put("baterrorcode", "batErrorCode");

        // Deposit table
        NORMALIZED_TO_FIELD.put("partner", "partner");
        NORMALIZED_TO_FIELD.put("filename", "fileName");
        NORMALIZED_TO_FIELD.put("protocol", "protocol");
        NORMALIZED_TO_FIELD.put("statut", "statut");
        NORMALIZED_TO_FIELD.put("daterecept", "dateRecept");
        NORMALIZED_TO_FIELD.put("compte", "compte");
    }

    private FieldMapper() {
    }

    /**
     * Normalizes a raw header token (e.g. "Code_Erreur", "Code Erreur",
     * "Affranch.") to a lookup key (e.g. "codeerreur", "affranch").
     */
    public static String normalize(String rawHeader) {
        if (rawHeader == null) {
            return "";
        }
        return rawHeader.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    /**
     * Returns the canonical field name for a raw header, or null if unknown
     * (unrecognized columns are simply ignored by the parser).
     */
    public static String canonicalField(String rawHeader) {
        return NORMALIZED_TO_FIELD.get(normalize(rawHeader));
    }
}
