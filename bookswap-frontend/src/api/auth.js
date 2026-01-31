import { api } from "./api";

export async function register(email, password) {
  const { data } = await api.post("/auth/register", { email, password });
  return data; // {id,email,role?} depinde de backend
}

export async function login(email, password) {
  const { data } = await api.post("/auth/login", { email, password });
  return data; // {id,email}
}

export async function me(userId) {
  const { data } = await api.get(`/auth/me/${userId}`);
  return data; // {id,email,is_admin,is_active}
}
