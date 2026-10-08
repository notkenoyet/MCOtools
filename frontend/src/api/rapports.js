import { PARSER_BASE_URL, TREATMENT_BASE_URL } from "./config";

export async function uploadFile(file) {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${PARSER_BASE_URL}/api/parser/upload`, {
    method: "POST",
    body: formData,
  });
  if (!response.ok) {
    throw new Error("Echec de l'upload du fichier");
  }
  return response.json();
}

/** Retourne un Blob PDF pour le fichier source donne. */
export async function genererRapportPdf(sourceFile) {
  const response = await fetch(
    `${TREATMENT_BASE_URL}/rapportTraitement/pdf?sourceFile=${encodeURIComponent(sourceFile)}`
  );
  if (!response.ok) {
    throw new Error("Echec de la generation du rapport");
  }
  return response.blob();
}

export function telechargerPdf(blob, nomFichier) {
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = nomFichier;
  document.body.appendChild(a);
  a.click();
  a.remove();
  window.URL.revokeObjectURL(url);
}
