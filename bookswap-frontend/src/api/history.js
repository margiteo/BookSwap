import { api } from "./api";

export async function getHistory(userId) {
  const { data } = await api.get(`/history/user/${userId}`);
  return data;
}
