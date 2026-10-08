import { useState } from "react";
import { Link } from "react-router-dom";
import { register } from "../api/auth";

export default function Register() {
  const [nom, setNom] = useState("");
  const [email, setEmail] = useState("");
  const [motDePasse, setMotDePasse] = useState("");
  const [telephone, setTelephone] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);
    try {
      const result = await register({ nom, email, motDePasse, telephone });
      setSuccess(result.message || "Inscription reussie, verifiez votre email.");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page">
      <h1>Creer un compte</h1>
      <form onSubmit={handleSubmit}>
        <label>Nom</label>
        <input type="text" value={nom} onChange={(e) => setNom(e.target.value)} required />

        <label>Email</label>
        <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />

        <label>Mot de passe</label>
        <input type="password" value={motDePasse} onChange={(e) => setMotDePasse(e.target.value)} required minLength={6} />

        <label>Telephone (optionnel)</label>
        <input type="tel" value={telephone} onChange={(e) => setTelephone(e.target.value)} />

        <button type="submit" disabled={loading}>
          {loading ? "Inscription..." : "S'inscrire"}
        </button>

        {error && <div className="error">{error}</div>}
        {success && <div className="success">{success}</div>}
      </form>
      <div className="link-row">
        Deja un compte ? <Link to="/login">Se connecter</Link>
      </div>
    </div>
  );
}
