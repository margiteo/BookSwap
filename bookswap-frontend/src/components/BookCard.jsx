export default function BookCard({
  title,
  author,
  coverUrl,
  subtitle,          // ✅ ADĂUGAT
  right,
  onPrimary,
  primaryLabel = "Action",
  primaryDisabled,
  secondaryLabel,
  onSecondary,
}) {
  return (
    <div className="rounded-2xl bg-white/70 border border-bsPink-200 p-5">
      <div className="flex gap-4">
        <img
          src={coverUrl || "https://via.placeholder.com/90x120?text=Cover"}
          alt={title}
          className="h-28 w-20 rounded-xl object-cover border border-bsPink-200 bg-white"
        />

        <div className="flex-1 min-w-0">
          <div className="text-lg font-extrabold truncate">{title}</div>
          <div className="text-sm text-bsPink-700/70 truncate">{author || ""}</div>

          {/* ✅ SUBTITLE */}
          {subtitle && <div className="mt-1 text-sm text-bsPink-700/70 truncate">{subtitle}</div>}

          <div className="mt-4 flex flex-wrap gap-2">
            {onPrimary && (
              <button
                disabled={primaryDisabled}
                onClick={onPrimary}
                className={[
                  "rounded-xl px-4 py-2 font-semibold border",
                  primaryDisabled
                    ? "bg-gray-100 text-gray-400 border-gray-200 cursor-not-allowed"
                    : "bg-white text-bsPink-700 border-bsPink-200 hover:bg-bsPink-50",
                ].join(" ")}
              >
                {primaryLabel}
              </button>
            )}

            {onSecondary && (
              <button
                onClick={onSecondary}
                className="rounded-xl px-4 py-2 font-semibold border border-bsPink-200 bg-white text-bsPink-700 hover:bg-bsPink-50"
              >
                {secondaryLabel}
              </button>
            )}
          </div>
        </div>

        {right && <div className="shrink-0">{right}</div>}
      </div>
    </div>
  );
}
