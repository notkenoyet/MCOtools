import { useState } from "react";
import { Link } from "react-router-dom";
import { uploadFile, genererRapportPdf, telechargerPdf } from "../api/rapports";
import { enregistrerRapport } from "../api/auth";

export default function MainPage() {
  const [file, setFile] = useState(null);
  const [uploadStatus, setUploadStatus] = useState("");
  const [error, setError] = useState("");
  const [uploading, setUploading] = useState(false);
  const [generating, setGenerating] = useState(false);

  async function handleUpload() {
    if (!file) {
      setError("Choisissez un fichier .txt avant d'uploader.");
      return;
    }
    setError("");
    setUploading(true);
    try {
      const summary = await uploadFile(file);
      setUploadStatus(
        `Fichier traite : ${summary.requestsPushed || 0} request(s), ` +
          `${summary.batchesPushed || 0} batch(es), ${summary.depositsPushed || 0} deposit(s).`
      );
    } catch (err) {
      setError(err.message);
    } finally {
      setUploading(false);
    }
  }

  async function handleGenerate() {
    if (!file) {
      setError("Uploadez d'abord un fichier .txt.");
      return;
    }
    setError("");
    setGenerating(true);
    try {
      const blob = await genererRapportPdf(file.name);
      const pdfName = file.name.replace(/\.txt$/, "") + ".pdf";
      telechargerPdf(blob, pdfName);
      await enregistrerRapport(file.name);
    } catch (err) {
      setError(err.message);
    } finally {
      setGenerating(false);
    }
  }

  return (
    <div className="page">
      <h1>Traitement d'un rapport</h1>
      <div className="link-row" style={{ textAlign: "left", marginBottom: 8 }}>
        <Link to="/">&larr; Retour a l'historique</Link>
      </div>

      <label>Fichier .txt</label>
      <input type="file" accept=".txt" onChange={(e) => setFile(e.target.files[0] || null)} />

      <button onClick={handleUpload} disabled={uploading || !file}>
        {uploading ? "Upload en cours..." : "Uploader le fichier"}
      </button>

      <button onClick={handleGenerate} disabled={generating || !file} className="secondary">
        {generating ? "Generation en cours..." : "Generer le rapport"}
      </button>

      {uploadStatus && <div className="success">{uploadStatus}</div>}
      {error && <div className="error">{error}</div>}
    </div>
  );
}
