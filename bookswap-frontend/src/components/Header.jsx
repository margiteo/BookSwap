export default function Header({ session, onLogout }) {
  return (
    <div className="rounded-3xl bg-white/60 backdrop-blur shadow-sm border border-bsPink-100 px-8 py-5 flex items-center justify-between">
      {/* Left: logo + title */}
      <div className="flex items-center gap-4">
        <img
          src="/hello-kitty.png"   // pune poza în public/hello-kitty.png
          alt="Hello Kitty"
          className="h-10 w-10 rounded-xl object-contain"
          onError={(e) => {
            e.currentTarget.style.display = "none"; // dacă lipsește imaginea, nu mai arată “broken img”
          }}
        />
        <div className="text-3xl font-extrabold text-bsPink-700">Book Swap</div>
      </div>

      {/* Right: user + logout */}
      <div className="flex items-center gap-4">
        <div className="flex items-center gap-3 rounded-2xl border border-bsPink-200 bg-white/70 px-4 py-2">
          <div className="h-9 w-9 rounded-full bg-bsPink-100 border border-bsPink-200 flex items-center justify-center font-extrabold text-bsPink-700">
            {(session?.email?.[0] || "U").toUpperCase()}
          </div>
          <div className="leading-tight">
            <div className="font-bold text-bsPink-700">{session?.email || "User"}</div>
            <div className="text-xs text-bsPink-700/60">User</div>
          </div>
        </div>

        <button
          onClick={onLogout}
          className="rounded-2xl border border-bsPink-200 bg-white px-6 py-3 font-bold text-bsPink-700 hover:bg-bsPink-50"
        >
          Log Out
        </button>
      </div>
    </div>
  );
}
