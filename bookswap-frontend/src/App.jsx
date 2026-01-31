import { useEffect, useState } from "react";
import Login from "./pages/Login";
import PinkShell from "./components/PinkShell";
import Header from "./components/Header";
import { login, me } from "./api/auth";

import UserDashboard from "./pages/UserDashboard";
import AdminDashboard from "./pages/AdminDashboard";

export default function App() {
  const [session, setSession] = useState(() => {
    const raw = localStorage.getItem("bookswap_session");
    return raw ? JSON.parse(raw) : null; // {id,email,is_admin}
  });

  useEffect(() => {
    if (session) localStorage.setItem("bookswap_session", JSON.stringify(session));
    else localStorage.removeItem("bookswap_session");
  }, [session]);

  async function handleLogin(email, password) {
    const u = await login(email, password); // {id,email}
    const meta = await me(u.id); // {is_admin,is_active,...}

    if (!meta.is_active) {
      throw { response: { data: { detail: "Account deactivated" } } };
    }

    setSession({ id: u.id, email: u.email, is_admin: !!meta.is_admin });
  }

  function handleLogout() {
    setSession(null);
  }

  if (!session) return <Login onLogin={handleLogin} />;

  return (
    <PinkShell>
      <Header session={session} onLogout={handleLogout} />
      <div className="mt-8">
        {session.is_admin ? (
          <AdminDashboard session={session} />
        ) : (
          <UserDashboard session={session} />
        )}
      </div>
    </PinkShell>
  );
}
