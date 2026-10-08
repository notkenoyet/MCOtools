import { BrowserRouter, Routes, Route, Navigate, Link, useNavigate } from "react-router-dom";
import Register from "./pages/Register";
import Login from "./pages/Login";
import Confirmation from "./pages/Confirmation";
import History from "./pages/History";
import MainPage from "./pages/MainPage";
import { isAuthenticated, getUserName, clearSession } from "./api/auth";

function RequireAuth({ children }) {
  if (!isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }
  return children;
}

function Navbar() {
  const navigate = useNavigate();
  if (!isAuthenticated()) return null;

  function handleLogout() {
    clearSession();
    navigate("/login");
  }

  return (
    <div className="navbar">
      <div>
        <Link to="/">Historique</Link>
        <Link to="/principale">Page principale</Link>
      </div>
      <div>
        <span style={{ marginRight: 12, fontSize: 14, color: "#555" }}>{getUserName()}</span>
        <button style={{ width: "auto", margin: 0, padding: "6px 12px" }} onClick={handleLogout}>
          Deconnexion
        </button>
      </div>
    </div>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/confirmation" element={<Confirmation />} />
        <Route
          path="/"
          element={
            <RequireAuth>
              <History />
            </RequireAuth>
          }
        />
        <Route
          path="/principale"
          element={
            <RequireAuth>
              <MainPage />
            </RequireAuth>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}
