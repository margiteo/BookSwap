import { api } from "./api";

export async function getCatalog(q = "", author = "") {
  const { data } = await api.get("/catalog/", { params: { q, author } });
  return data;
}
