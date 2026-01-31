import { useEffect, useState } from "react";
import TabButton from "../components/TabButton";
import BookCard from "../components/BookCard";

import { topRequested } from "../api/admin";
import { getCatalog } from "../api/catalog";
import { adminActivateCatalog, adminDeactivateCatalog } from "../api/adminCatalog";
import { listUsers, deactivateUser, activateUser } from "../api/adminUsers";
import { adminAddCatalog } from "../api/adminCatalogAdd"; // <- IMPORTANT: vezi nota de mai jos

const tabs = ["Catalog", "Users", "Stats"];

export default function AdminDashboard({ session }) {
  const [active, setActive] = useState("Stats");
  const [toast, setToast] = useState("");

  // Stats
  const [stats, setStats] = useState([]);

  // Catalog
  const [catalog, setCatalog] = useState([]);
  const [q, setQ] = useState("");
  const [title, setTitle] = useState("");
  const [author, setAuthor] = useState("");
  const [coverUrl, setCoverUrl] = useState("");

  // Users
  const [users, setUsers] = useState([]);

  function showToast(msg, ms = 2500) {
    setToast(msg);
    setTimeout(() => setToast(""), ms);
  }

  useEffect(() => {
    if (active === "Stats") loadStats();
    if (active === "Catalog") loadCatalog();
    if (active === "Users") loadUsers();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [active]);

  async function loadStats() {
    try {
      const data = await topRequested(session.id, 10);
      setStats(data);
    } catch (e) {
      showToast(e?.response?.data?.detail || "Could not load stats", 3500);
    }
  }

  async function loadCatalog() {
    try {
      const data = await getCatalog(q);
      setCatalog(data);
    } catch (e) {
      showToast(e?.response?.data?.detail || "Could not load catalog", 3500);
    }
  }

  async function loadUsers() {
    try {
      const data = await listUsers(session.id);
      setUsers(data);
    } catch (e) {
      showToast(e?.response?.data?.detail || "Could not load users", 3500);
    }
  }

  return (
    <div className="rounded-3xl bg-white/60 backdrop-blur shadow-sm p-8">
      <h1 className="text-4xl font-extrabold text-bsPink-700">Admin Panel</h1>
      <p className="mt-2 text-bsPink-700/80">Manage catalog, users, and view stats (all pink ✨).</p>

      <div className="mt-6 flex flex-wrap gap-3">
        {tabs.map((t) => (
          <TabButton key={t} active={active === t} onClick={() => setActive(t)}>
            {t}
          </TabButton>
        ))}
      </div>

      {toast && (
        <div className="mt-4 rounded-xl border border-bsPink-200 bg-bsPink-50 px-4 py-3 text-bsPink-700">
          {toast}
        </div>
      )}

      {/* STATS */}
      {active === "Stats" && (
        <div className="mt-8">
          <h2 className="text-2xl font-bold text-bsPink-700">Top Requested Titles</h2>

          <div className="mt-4 grid grid-cols-1 md:grid-cols-2 gap-4">
            {stats.map((s) => (
              <div key={s.catalog_book_id ?? s.id} className="rounded-2xl bg-white/70 border border-bsPink-200 p-5">
                <div className="font-extrabold text-bsPink-700">{s.title}</div>
                <div className="text-sm text-bsPink-700/70">{s.author}</div>
                <div className="mt-2 text-xs text-bsPink-700/60">Requests: {s.count}</div>
              </div>
            ))}
          </div>

          {stats.length === 0 && <div className="mt-4 text-bsPink-700/70">No stats yet.</div>}
        </div>
      )}

      {/* CATALOG */}
      {active === "Catalog" && (
        <div className="mt-8">
          <h2 className="text-2xl font-bold text-bsPink-700">Catalog Management</h2>

          <div className="mt-4 grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Add form */}
            <div className="rounded-2xl bg-white/70 border border-bsPink-200 p-6">
              <div className="text-lg font-extrabold">Add new title</div>

              <div className="mt-4 space-y-3">
                <input
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="Title"
                  className="w-full rounded-xl border border-bsPink-200 bg-white px-4 py-2 outline-none focus:border-bsPink-500"
                />
                <input
                  value={author}
                  onChange={(e) => setAuthor(e.target.value)}
                  placeholder="Author"
                  className="w-full rounded-xl border border-bsPink-200 bg-white px-4 py-2 outline-none focus:border-bsPink-500"
                />
                <input
                  value={coverUrl}
                  onChange={(e) => setCoverUrl(e.target.value)}
                  placeholder="Cover URL (optional)"
                  className="w-full rounded-xl border border-bsPink-200 bg-white px-4 py-2 outline-none focus:border-bsPink-500"
                />

                <button
                  onClick={async () => {
                    try {
                      await adminAddCatalog(session.id, { title, author, cover_url: coverUrl || null });
                      showToast("Catalog title added ✅");
                      setTitle("");
                      setAuthor("");
                      setCoverUrl("");
                      await loadCatalog();
                    } catch (e) {
                      showToast(e?.response?.data?.detail || "Could not add title", 3500);
                    }
                  }}
                  className="w-full rounded-xl bg-bsPink-500 py-2 font-bold text-white hover:bg-bsPink-600"
                >
                  Add to catalog
                </button>
              </div>
            </div>

            {/* Search + list */}
            <div className="rounded-2xl bg-white/70 border border-bsPink-200 p-6">
              <div className="flex items-center justify-between gap-3">
                <div className="text-lg font-extrabold">Catalog list</div>
                <div className="flex gap-2">
                  <input
                    value={q}
                    onChange={(e) => setQ(e.target.value)}
                    placeholder="Search title..."
                    className="w-56 rounded-xl border border-bsPink-200 bg-white px-4 py-2 outline-none focus:border-bsPink-500"
                  />
                  <button
                    onClick={loadCatalog}
                    className="rounded-xl bg-bsPink-500 px-4 py-2 font-bold text-white hover:bg-bsPink-600"
                  >
                    Search
                  </button>
                </div>
              </div>

              <div className="mt-4 space-y-4 max-h-[520px] overflow-auto pr-1">
                {catalog.map((c) => (
                  <BookCard
                    key={c.id}
                    title={c.title}
                    author={c.author}
                    coverUrl={c.cover_url}
                    // Toggle Activate/Deactivate
                    primaryLabel={c.is_active ? "Deactivate" : "Activate"}
                    onPrimary={async () => {
                      try {
                        if (c.is_active) {
                          await adminDeactivateCatalog(session.id, c.id);
                          showToast("Deactivated ✅");
                        } else {
                          await adminActivateCatalog(session.id, c.id);
                          showToast("Activated ✅");
                        }
                        await loadCatalog();
                      } catch (e) {
                        showToast(e?.response?.data?.detail || "Action failed", 3500);
                      }
                    }}
                  />
                ))}
                {catalog.length === 0 && <div className="text-bsPink-700/70">No catalog results.</div>}
              </div>

              <div className="mt-4 text-xs text-bsPink-700/60">
                Admin can only Activate / Deactivate (safe). No Delete.
              </div>
            </div>
          </div>
        </div>
      )}

      {/* USERS */}
      {active === "Users" && (
        <div className="mt-8">
          <div className="flex items-center justify-between">
            <h2 className="text-2xl font-bold text-bsPink-700">User Management</h2>
            <button
              onClick={loadUsers}
              className="rounded-xl bg-bsPink-500 px-4 py-2 font-bold text-white hover:bg-bsPink-600"
            >
              Refresh
            </button>
          </div>

          <div className="mt-6 space-y-3">
            {users.map((u) => (
              <div
                key={u.id}
                className="rounded-2xl bg-white/70 border border-bsPink-200 p-5 flex items-center justify-between"
              >
                <div>
                  <div className="font-extrabold">{u.email}</div>
                  <div className="text-sm text-bsPink-700/70">
                    id: {u.id} • role: {u.role} • active: {String(u.is_active)}
                  </div>
                </div>

                <div className="flex gap-2">
                  <button
                    onClick={async () => {
                      try {
                        await deactivateUser(session.id, u.id);
                        showToast("User deactivated ✅");
                        await loadUsers();
                      } catch (e) {
                        showToast(e?.response?.data?.detail || "Could not deactivate", 3500);
                      }
                    }}
                    className="rounded-xl border border-bsPink-200 bg-white px-4 py-2 font-bold text-bsPink-700 hover:bg-bsPink-50"
                  >
                    Deactivate
                  </button>

                  <button
                    onClick={async () => {
                      try {
                        await activateUser(session.id, u.id);
                        showToast("User activated ✅");
                        await loadUsers();
                      } catch (e) {
                        showToast(e?.response?.data?.detail || "Could not activate", 3500);
                      }
                    }}
                    className="rounded-xl bg-bsPink-500 px-4 py-2 font-bold text-white hover:bg-bsPink-600"
                  >
                    Activate
                  </button>
                </div>
              </div>
            ))}

            {users.length === 0 && <div className="text-bsPink-700/70">No users.</div>}
          </div>
        </div>
      )}
    </div>
  );
}
