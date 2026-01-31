export default function TabButton({ active, children, onClick }) {
  return (
    <button
      onClick={onClick}
      className={[
        "rounded-xl px-5 py-2 font-semibold transition",
        active
          ? "bg-bsPink-500 text-white shadow-sm"
          : "bg-white/70 border border-bsPink-200 text-bsPink-700 hover:bg-bsPink-50",
      ].join(" ")}
    >
      {children}
    </button>
  );
}
