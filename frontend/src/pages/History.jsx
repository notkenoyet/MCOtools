import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getHistorique } from "../api/auth";

export default function History() {
  const [historique, setHistorique] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getHistorique()
      .then(setHistorique)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="page page-wide">
      <h1>Historique des rapports</h1>
      <div className="link-row" style={{ textAlign: "left", marginBottom: 8 }}>
        <Link to="/principale">+ Aller a la page principale (upload / generation)</Link>
      </div>

      {loading && <p>Chargement...</p>}
      {error && <div className="error">{error}</div>}

      {!loading && !error && historique.length === 0 && <p>Aucun rapport genere pour le moment.</p>}

      {!loading && historique.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>Fichier source</th>
              <th>Date de generation</th>
            </tr>
          </thead>
          <tbody>
            {historique.map((item) => (
              <tr key={item.id}>
                <td>{item.sourceFile}</td>
                <td>{new Date(item.dateGeneration).toLocaleString("fr-FR")}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
