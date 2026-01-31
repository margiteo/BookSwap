export default function PinkShell({ children }) {
  return (
    <div className="min-h-screen w-full bg-gradient-to-br from-bsPink-100 via-bsPink-50 to-white">
      <div className="mx-auto max-w-6xl px-6 py-10">{children}</div>
    </div>
  );
}
