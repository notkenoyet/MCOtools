package com.mcotools.treatment.rules;

/**
 * Actions possibles a l'issue du rapport de traitement, une par regle
 * metier communiquee par l'encadrant.
 */
public enum ActionType {

    /** converting_error, code_erreur commencant par "26" -> MBean manualconversion. */
    MANUAL_CONVERSION,

    /**
     * converting_error (autre code), DIGITAL_CHECK_ERROR, DIGITOOLS_ERROR,
     * ou PARSING_ERROR non-FLW -> relancer la request.
     */
    RETRY_REQUEST,

    /** PARSING_ERROR + FLW, ou deposit "Deposit rejected" -> replayDeposit. */
    REPLAY_DEPOSIT,

    /** TO_BATCH_ERROR -> router sur le batching V1. */
    BATCHING_V1,

    /** batch ETAB=DIGIPOSTE + etat=SENT -> passer l'etat a RECEIVED. */
    SET_RECEIVED,

    /**
     * TO_BATCH : aucune decision automatique n'est prise (cf. reunion avec
     * l'encadrant du 22/08 : le rapport doit seulement afficher l'etat trouve,
     * pas trancher entre BATCHED/WAIT).
     */
    INFO_ONLY
}
