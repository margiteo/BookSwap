import { useState } from "react";
import kitty from "../assets/hello-kitty.png";
import { register as apiRegister } from "../api/auth";

export default function Login({ onLogin }) {
  const [mode, setMode] = useState("login"); // "login" | "register"
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");

  async function handleRegister() {
    setErr("");
    setOk("");

    try {
      await apiRegister(email, password);
      setOk("Account created ✅ Now you can log in.");
      setMode("login");
      // opțional: păstrează email-ul completat
    } catch (e) {
      setErr(e?.response?.data?.detail || "Register failed");
    }
  }

  async function handleLoginClick() {
    setErr("");
    setOk("");

    try {
      await onLogin(email, password);
    } catch (e) {
      setErr(e?.response?.data?.detail || "Login failed");
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-bsPink-100 via-bsPink-50 to-white flex items-center justify-center px-6">
      <div className="w-full max-w-lg rounded-3xl bg-white/70 backdrop-blur shadow-lg p-10">
        <div className="flex flex-col items-center gap-3 mb-6">
          <img src={kitty} alt="Hello Kitty" className="h-16 w-16 rounded-2xl" />
          <h1 className="text-4xl font-extrabold text-bsPink-700">Book Swap</h1>
        </div>

        {/* Tabs */}
        <div className="mx-auto mb-6 flex w-full max-w-sm rounded-2xl bg-white/70 border border-bsPink-200 p-1">
          <button
            onClick={() => { setMode("login"); setErr(""); setOk(""); }}
            className={[
              "w-1/2 rounded-xl py-2 text-sm font-bold",
              mode === "login" ? "bg-bsPink-500 text-white" : "text-bsPink-700 hover:bg-bsPink-50",
            ].join(" ")}
          >
            Log In
          </button>
          <button
            onClick={() => { setMode("register"); setErr(""); setOk(""); }}
            className={[
              "w-1/2 rounded-xl py-2 text-sm font-bold",
              mode === "register" ? "bg-bsPink-500 text-white" : "text-bsPink-700 hover:bg-bsPink-50",
            ].join(" ")}
          >
            Register
          </button>
        </div>

        <h2 className="text-3xl font-bold text-bsPink-700 text-center mb-6">
          {mode === "login" ? "Log In" : "Create Account"}
        </h2>

        <div className="space-y-4">
          <div>
            <label className="text-sm font-semibold">Email Address</label>
            <input
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="mt-1 w-full rounded-xl border border-bsPink-200 bg-white px-4 py-3 outline-none focus:border-bsPink-500"
              placeholder="ana@test.com"
            />
          </div>

          <div>
            <label className="text-sm font-semibold">Password</label>
            <input
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              type="password"
              className="mt-1 w-full rounded-xl border border-bsPink-200 bg-white px-4 py-3 outline-none focus:border-bsPink-500"
              placeholder="1234"
            />
          </div>

          {err && <div className="text-sm text-red-600">{err}</div>}
          {ok && <div className="text-sm text-green-700">{ok}</div>}

          {mode === "login" ? (
            <>
              <button
                onClick={handleLoginClick}
                className="w-full rounded-xl bg-bsPink-500 py-3 font-bold text-white hover:bg-bsPink-600"
              >
                Log in
              </button>
              <div className="text-right text-sm text-bsPink-700/70">Forget password?</div>
            </>
          ) : (
            <button
              onClick={handleRegister}
              className="w-full rounded-xl bg-bsPink-500 py-3 font-bold text-white hover:bg-bsPink-600"
            >
              Create account
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
