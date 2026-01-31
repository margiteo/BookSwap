import { useEffect, useMemo, useState } from "react";
import TabButton from "../components/TabButton";
import BookCard from "../components/BookCard";

import { getAvailableBooks, getMyBooks, addFromCatalog, deleteMyBook } from "../api/books";
import { getCatalog } from "../api/catalog";
import { createSwap, inbox, outbox, acceptSwap, rejectSwap } from "../api/swaps";
import { getWishlist, addWishlist, removeWishlist } from "../api/wishlist";
import { getHistory } from "../api/history";

const tabs = ["Browse Books", "My Books", "Requests", "Catalog", "Wishlist", "History"];

export default function UserDashboard({ session }) {
  const [active, setActive] = useState("Browse Books");
  const [toast, setToast] = useState("");

  // Browse
  const [available, setAvailable] = useState([]);
  const [loadingBrowse, setLoadingBrowse] = useState(false);
  const [qBrowse, setQBrowse] = useState("");

  // My books
  const [myBooks, setMyBooks] = useState([]);
  const [loadingMy, setLoadingMy] = useState(false);

  // Catalog
  const [catalog, setCatalog] = useState([]);
  const [loadingCatalog, setLoadingCatalog] = useState(false);
  const [qCatalog, setQCatalog] = useState("");

  // Requests
  const [inb, setInb] = useState([]);
  const [outb, setOutb] = useState([]);
  const [loadingReq, setLoadingReq] = useState(false);

  // Wishlist
  const [wishlist, setWishlist] = useState([]);
  const [loadingWish, setLoadingWish] = useState(false);

  // History
  const [history, setHistory] = useState([]);
  const [loadingHist, setLoadingHist] = useState(false);

  function showToast(msg, ms = 2500) {
    setToast(msg);
    setTimeout(() => setToast(""), ms);
  }

  useEffect(() => {
    if (active === "Browse Books") loadBrowse();
    if (active === "My Books") loadMy();
    if (active === "Catalog") loadCatalog();
    if (active === "Requests") loadRequests();
    if (active === "Wishlist") loadWishlist();
    if (active === "History") loadHistory();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [active]);

  async function loadBrowse() {
    setLoadingBrowse(true);
    try {
      const data = await getAvailableBooks(session.id, qBrowse);
      setAvailable(data);
    } finally {
      setLoadingBrowse(false);
    }
  }

  async function loadMy() {
    setLoadingMy(true);
    try {
      const data = await getMyBooks(session.id);
      setMyBooks(data);
    } finally {
      setLoadingMy(false);
    }
  }

  async function loadCatalog() {
    setLoadingCatalog(true);
    try {
      const data = await getCatalog(qCatalog);
      setCatalog(data);
    } finally {
      setLoadingCatalog(false);
    }
  }

  async function loadRequests() {
    setLoadingReq(true);
    try {
      const [a, b] = await Promise.all([inbox(session.id), outbox(session.id)]);
      setInb(a);
      setOutb(b);
    } catch (e) {
      showToast(e?.response?.data?.detail || "Could not load requests", 3500);
      setInb([]);
      setOutb([]);
    } finally {
      setLoadingReq(false);
    }
  }

  async function loadWishlist() {
    setLoadingWish(true);
    try {
      const data = await getWishlist(session.id);
      setWishlist(data);
    } finally {
      setLoadingWish(false);
    }
  }

  async function loadHistory() {
    setLoadingHist(true);
    try {
      const data = await getHistory(session.id);
      setHistory(data);
    } finally {
      setLoadingHist(false);
    }
  }

  const recent = useMemo(() => available.slice(0, 4), [available]);

  return (
    <>
      <div className="mt-6 rounded-3xl bg-white/60 backdrop-blur shadow-sm p-8">
        <h1 className="text-5xl font-extrabold text-bsPink-700">Welcome to Book Swap</h1>

        <p className="mt-3 max-w-2xl text-lg text-bsPink-700/80">
          Swap books with other users. Add books you want to exchange, browse available books, and request swaps.
        </p>

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

        {/* BROWSE */}
        {active === "Browse Books" && (
          <div className="mt-10 grid grid-cols-1 lg:grid-cols-2 gap-10">
            <div>
              <div className="flex items-center justify-between gap-4">
                <h2 className="text-2xl font-bold text-bsPink-700">Recent Books</h2>
                <div className="flex gap-2">
                  <input
                    value={qBrowse}
                    onChange={(e) => setQBrowse(e.target.value)}
                    placeholder="Search title..."
                    className="w-56 rounded-xl border border-bsPink-200 bg-white px-4 py-2 outline-none focus:border-bsPink-500"
                  />
                  <button
                    onClick={loadBrowse}
                    className="rounded-xl bg-bsPink-500 px-4 py-2 font-bold text-white hover:bg-bsPink-600"
                  >
                    Search
                  </button>
                </div>
              </div>

              {loadingBrowse ? (
                <div className="mt-4 text-bsPink-700/70">Loading...</div>
              ) : (
                <div className="mt-4 grid grid-cols-2 gap-4">
                  {recent.map((b) => (
                    <div key={b.id} className="rounded-2xl bg-white/70 border border-bsPink-200 p-4">
                      <div className="flex gap-3">
                        <img
                          src={b.cover_url || "https://via.placeholder.com/90x120?text=Cover"}
                          alt={b.title}
                          className="h-28 w-20 rounded-xl object-cover border border-bsPink-200 bg-white"
                        />
                        <div className="min-w-0">
                          <div className="font-bold truncate">{b.title}</div>
                          <div className="text-sm text-bsPink-700/70 truncate">{b.author || ""}</div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div>
              <h2 className="text-2xl font-bold text-bsPink-700">Request</h2>

              {loadingBrowse ? (
                <div className="mt-4 text-bsPink-700/70">Loading...</div>
              ) : (
                <div className="mt-4 space-y-4">
                  {available.slice(0, 6).map((b) => (
                    <BookCard
                      key={b.id}
                      title={b.title}
                      author={b.author}
                      coverUrl={b.cover_url}
                      subtitle={`Owner: ${b.owner_email ? b.owner_email : `User #${b.owner_id}`}`}
                      primaryLabel="Request Swap"
                      onPrimary={async () => {
                        try {
                          await createSwap(b.id, session.id);
                          showToast("Request sent ✅");
                          await loadRequests();
                        } catch (e) {
                          showToast(e?.response?.data?.detail || "Could not request", 3500);
                        }
                      }}
                    />
                  ))}

                  {available.length === 0 && (
                    <div className="text-bsPink-700/70">No available books right now.</div>
                  )}
                </div>
              )}
            </div>
          </div>
        )}

        {/* MY BOOKS */}
        {active === "My Books" && (
          <div className="mt-10">
            <div className="flex items-center justify-between">
              <h2 className="text-2xl font-bold text-bsPink-700">My Books</h2>
              <button
                onClick={loadMy}
                className="rounded-xl bg-bsPink-500 px-4 py-2 font-bold text-white hover:bg-bsPink-600"
              >
                Refresh
              </button>
            </div>

            {loadingMy ? (
              <div className="mt-4 text-bsPink-700/70">Loading...</div>
            ) : (
              <div className="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
                {myBooks.map((b) => (
                  <BookCard
                    key={b.id}
                    title={b.title}
                    author={b.author}
                    coverUrl={b.cover_url}
                    primaryLabel="Remove"
                    onPrimary={async () => {
                      try {
                        await deleteMyBook(b.id, session.id);
                        showToast("Removed ✅");
                        await loadMy();
                      } catch (e) {
                        showToast(e?.response?.data?.detail || "Could not remove", 3500);
                      }
                    }}
                  />
                ))}
                {myBooks.length === 0 && <div className="text-bsPink-700/70">You have no books yet.</div>}
              </div>
            )}
          </div>
        )}

        {/* REQUESTS */}
        {active === "Requests" && (
          <div className="mt-10 grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* INBOX */}
            <div>
              <div className="flex items-center justify-between">
                <h2 className="text-2xl font-bold text-bsPink-700">Inbox (to accept/reject)</h2>
                <button
                  onClick={loadRequests}
                  className="rounded-xl bg-bsPink-500 px-4 py-2 font-bold text-white hover:bg-bsPink-600"
                >
                  Refresh
                </button>
              </div>

              {loadingReq ? (
                <div className="mt-4 text-bsPink-700/70">Loading...</div>
              ) : (
                <div className="mt-6 space-y-4">
                  {inb.map((s) => {
                    const canDecide = ["PENDING", "REQUESTED"].includes(String(s.status || "").toUpperCase());

                    return (
                      <div key={s.id} className="rounded-2xl bg-white/70 border border-bsPink-200 p-5">
                        <div className="font-extrabold">Swap #{s.id}</div>

                        <div className="text-sm text-bsPink-700/70 mt-1">
                          <span className="font-semibold">{s.book_title || "Unknown title"}</span>
                          {s.book_author ? ` • ${s.book_author}` : ""} • From:{" "}
                          <span className="font-semibold">{s.requester_email || `User #${s.requester_id}`}</span>{" "}
                          • Status: <span className="font-semibold">{s.status}</span>
                        </div>

                        {s.decided_at && (
                          <div className="text-sm text-bsPink-700/70 mt-1">Decided: {String(s.decided_at)}</div>
                        )}

                        {/* ✅ AICI e fixul */}
                        {canDecide ? (
                          <div className="mt-4 flex gap-2">
                            <button
                              onClick={async () => {
                                try {
                                  await acceptSwap(s.id, session.id);
                                  showToast("Accepted ✅");
                                  await loadRequests();
                                } catch (e) {
                                  showToast(e?.response?.data?.detail || "Could not accept", 3500);
                                }
                              }}
                              className="rounded-xl bg-bsPink-500 px-4 py-2 font-bold text-white hover:bg-bsPink-600"
                            >
                              Accept
                            </button>

                            <button
                              onClick={async () => {
                                try {
                                  await rejectSwap(s.id, session.id);
                                  showToast("Rejected ✅");
                                  await loadRequests();
                                } catch (e) {
                                  showToast(e?.response?.data?.detail || "Could not reject", 3500);
                                }
                              }}
                              className="rounded-xl border border-bsPink-200 bg-white px-4 py-2 font-bold text-bsPink-700 hover:bg-bsPink-50"
                            >
                              Reject
                            </button>
                          </div>
                        ) : (
                          <div className="mt-4 inline-flex items-center rounded-xl border border-bsPink-200 bg-white/70 px-4 py-2 font-bold text-bsPink-700">
                            Decision already made
                          </div>
                        )}
                      </div>
                    );
                  })}

                  {inb.length === 0 && <div className="text-bsPink-700/70">No incoming requests.</div>}
                </div>
              )}
            </div>

            {/* OUTBOX */}
            <div>
              <h2 className="text-2xl font-bold text-bsPink-700">Outbox (my requests)</h2>

              {loadingReq ? (
                <div className="mt-4 text-bsPink-700/70">Loading...</div>
              ) : (
                <div className="mt-6 space-y-4">
                  {outb.map((s) => (
                    <div key={s.id} className="rounded-2xl bg-white/70 border border-bsPink-200 p-5">
                      <div className="font-extrabold">Swap #{s.id}</div>

                      <div className="text-sm text-bsPink-700/70 mt-1">
                        <span className="font-semibold">{s.book_title || "Unknown title"}</span>
                        {s.book_author ? ` • ${s.book_author}` : ""} • Owner:{" "}
                        <span className="font-semibold">{s.owner_email || `User #${s.owner_id}`}</span> • Status:{" "}
                        <span className="font-semibold">{s.status}</span>
                      </div>

                      {s.decided_at && (
                        <div className="text-sm text-bsPink-700/70 mt-1">Decided: {String(s.decided_at)}</div>
                      )}
                    </div>
                  ))}

                  {outb.length === 0 && <div className="text-bsPink-700/70">No outgoing requests.</div>}
                </div>
              )}
            </div>
          </div>
        )}

        {/* CATALOG */}
        {active === "Catalog" && (
          <div className="mt-10">
            <div className="flex flex-wrap items-center justify-between gap-4">
              <h2 className="text-2xl font-bold text-bsPink-700">Catalog</h2>

              <div className="flex gap-2">
                <input
                  value={qCatalog}
                  onChange={(e) => setQCatalog(e.target.value)}
                  placeholder="Search title..."
                  className="w-64 rounded-xl border border-bsPink-200 bg-white px-4 py-2 outline-none focus:border-bsPink-500"
                />
                <button
                  onClick={loadCatalog}
                  className="rounded-xl bg-bsPink-500 px-4 py-2 font-bold text-white hover:bg-bsPink-600"
                >
                  Search
                </button>
              </div>
            </div>

            {loadingCatalog ? (
              <div className="mt-4 text-bsPink-700/70">Loading...</div>
            ) : (
              <div className="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
                {catalog.map((c) => (
                  <BookCard
                    key={c.id}
                    title={c.title}
                    author={c.author}
                    coverUrl={c.cover_url}
                    primaryLabel="Add to My Books"
                    onPrimary={async () => {
                      try {
                        await addFromCatalog(session.id, c.id);
                        showToast("Added to your library ✅");
                        await loadMy();
                      } catch (e) {
                        showToast(e?.response?.data?.detail || "Could not add", 3500);
                      }
                    }}
                    secondaryLabel="Add to Wishlist"
                    onSecondary={async () => {
                      try {
                        await addWishlist(session.id, c.id);
                        showToast("Added to wishlist 💗");
                        await loadWishlist();
                      } catch (e) {
                        showToast(e?.response?.data?.detail || "Could not wishlist", 3500);
                      }
                    }}
                  />
                ))}
                {catalog.length === 0 && <div className="text-bsPink-700/70">No catalog results.</div>}
              </div>
            )}
          </div>
        )}

        {/* WISHLIST */}
        {active === "Wishlist" && (
          <div className="mt-10">
            <div className="flex items-center justify-between">
              <h2 className="text-2xl font-bold text-bsPink-700">Wishlist</h2>
              <button
                onClick={loadWishlist}
                className="rounded-xl bg-bsPink-500 px-4 py-2 font-bold text-white hover:bg-bsPink-600"
              >
                Refresh
              </button>
            </div>

            {loadingWish ? (
              <div className="mt-4 text-bsPink-700/70">Loading...</div>
            ) : (
              <div className="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
                {wishlist.map((w) => (
                  <BookCard
                    key={w.catalog_book_id}
                    title={w.title}
                    author={w.author}
                    coverUrl={w.cover_url}
                    primaryLabel={w.is_available ? "Available now ✅" : "Not available"}
                    primaryDisabled
                    secondaryLabel="Remove"
                    onSecondary={async () => {
                      try {
                        await removeWishlist(session.id, w.catalog_book_id);
                        showToast("Removed from wishlist ✅");
                        await loadWishlist();
                      } catch (e) {
                        showToast(e?.response?.data?.detail || "Could not remove", 3500);
                      }
                    }}
                  />
                ))}
                {wishlist.length === 0 && <div className="text-bsPink-700/70">Wishlist empty.</div>}
              </div>
            )}
          </div>
        )}

        {/* HISTORY */}
        {active === "History" && (
          <div className="mt-10">
            <div className="flex items-center justify-between">
              <h2 className="text-2xl font-bold text-bsPink-700">Swap History</h2>
              <button
                onClick={loadHistory}
                className="rounded-xl bg-bsPink-500 px-4 py-2 font-bold text-white hover:bg-bsPink-600"
              >
                Refresh
              </button>
            </div>

            {loadingHist ? (
              <div className="mt-4 text-bsPink-700/70">Loading...</div>
            ) : (
              <div className="mt-6 space-y-4">
                {history.map((h) => (
                  <div key={h.id} className="rounded-2xl bg-white/70 border border-bsPink-200 p-5">
                    <div className="font-extrabold">
                      Swap #{h.id} — {h.status}
                    </div>
                    <div className="text-sm text-bsPink-700/70 mt-1">
                      Book: {h.title} • Owner: {h.owner_email} • Requester: {h.requester_email}
                    </div>
                    <div className="text-sm text-bsPink-700/70 mt-1">
                      Created: {String(h.created_at)} {h.decided_at ? `• Decided: ${String(h.decided_at)}` : ""}
                    </div>
                  </div>
                ))}
                {history.length === 0 && <div className="text-bsPink-700/70">No history yet.</div>}
              </div>
            )}
          </div>
        )}
      </div>
    </>
  );
}
