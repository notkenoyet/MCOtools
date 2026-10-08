import { AUTH_BASE_URL } from "./config";

function getToken() {
  return localStorage.getItem("mcotools_token");
}

export function setSession(token, nom, email) {
  localStorage.setItem("mcotools_token", token);
  localStorage.setItem("mcotools_nom", nom);
  localStorage.setItem("mcotools_email", email);
}

export function clearSession() {
  localStorage.removeItem("mcotools_token");
  localStorage.removeItem("mcotools_nom");
  localStorage.removeItem("mcotools_email");
}

export function isAuthenticated() {
  return !!getToken();
}

export function getUserName() {
  return localStorage.getItem("mcotools_nom") || "";
}

async function parseJsonSafe(response) {
  try {
    return await response.json();
  } catch {
    return null;
  }
}

export async function register({ nom, email, motDePasse, telephone }) {
  const response = await fetch(`${AUTH_BASE_URL}/api/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ nom, email, motDePasse, telephone: telephone || null }),
  });
  const body = await parseJsonSafe(response);
  if (!response.ok) {
    throw new Error(body?.message || "Erreur lors de l'inscription");
  }
  return body;
}

export async function login({ email, motDePasse }) {
  const response = await fetch(`${AUTH_BASE_URL}/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, motDePasse }),
  });
  const body = await parseJsonSafe(response);
  if (!response.ok) {
    throw new Error(body?.message || "Erreur lors de la connexion");
  }
  setSession(body.token, body.nom, body.email);
  return body;
}

export async function confirmAccount(token) {
  const response = await fetch(`${AUTH_BASE_URL}/api/auth/confirm?token=${encodeURIComponent(token)}`);
  const body = await parseJsonSafe(response);
  if (!response.ok) {
    throw new Error(body?.message || "Lien d'activation invalide");
  }
  return body;
}

export async function getHistorique() {
  const response = await fetch(`${AUTH_BASE_URL}/api/historique`, {
    headers: { Authorization: `Bearer ${getToken()}` },
  });
  if (response.status === 401) {
    clearSession();
    throw new Error("Session expiree, reconnectez-vous.");
  }
  return response.json();
}

export async function enregistrerRapport(sourceFile) {
  const response = await fetch(`${AUTH_BASE_URL}/api/historique`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${getToken()}`,
    },
    body: JSON.stringify({ sourceFile }),
  });
  return response.json();
}
