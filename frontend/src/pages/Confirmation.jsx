import { useEffect, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { confirmAccount } from "../api/auth";

export default function Confirmation() {
  const [searchParams] = useSearchParams();
  const [status, setStatus] = useState("loading"); // loading | success | error
  const [message, setMessage] = useState("");

  useEffect(() => {
    const token = searchParams.get("token");
    if (!token) {
      setStatus("error");
      setMessage("Lien invalide : aucun token trouve.");
      return;
    }
    confirmAccount(token)
      .then((res) => {
        setStatus("success");
        setMessage(res.message || "Votre compte est active.");
      })
      .catch((err) => {
        setStatus("error");
        setMessage(err.message);
      });
  }, [searchParams]);

  return (
    <div className="page">
      <h1>Activation du compte</h1>
      {status === "loading" && <p>Verification en cours...</p>}
      {status === "success" && <div className="success">{message}</div>}
      {status === "error" && <div className="error">{message}</div>}
      {status !== "loading" && (
        <div className="link-row">
          <Link to="/login">Aller a la page de connexion</Link>
        </div>
      )}
    </div>
  );
}
