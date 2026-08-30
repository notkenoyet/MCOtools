package com.mcotools.treatment.rules;

/**
 * Une ligne du rapport de traitement : l'entite concernee, son identifiant,
 * l'action recommandee (ou null si le rapport ne fait qu'informer, cf.
 * ActionType.INFO_ONLY), et le motif qui a declenche la regle.
 */
public class ActionItem {

    private final String entityType; // "request" | "batch" | "deposit"
    private final Object id;
    private final ActionType action;
    private final String reason;
    private final String note; // precision optionnelle, ex: hypothese a confirmer

    public ActionItem(String entityType, Object id, ActionType action, String reason) {
        this(entityType, id, action, reason, null);
    }

    public ActionItem(String entityType, Object id, ActionType action, String reason, String note) {
        this.entityType = entityType;
        this.id = id;
        this.action = action;
        this.reason = reason;
        this.note = note;
    }

    public String getEntityType() {
        return entityType;
    }

    public Object getId() {
        return id;
    }

    public ActionType getAction() {
        return action;
    }

    public String getReason() {
        return reason;
    }

    public String getNote() {
        return note;
    }

    @Override
    public String toString() {
        return "ActionItem{entityType=" + entityType + ", id=" + id + ", action=" + action
                + ", reason=" + reason + ", note=" + note + "}";
    }
}
