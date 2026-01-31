/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      colors: {
        bsPink: {
          50: "#FFF1F6",
          100: "#FFE4EC",
          200: "#FFC9D7",
          500: "#D86A7A",
          600: "#C15867",
          700: "#9E3E4C",
        },
      },
    },
  },
  plugins: [],
};
