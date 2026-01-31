import { api } from "./api";

export async function getWishlist(userId) {
  const res = await api.get(`/wishlist/${userId}`);
  return res.data;
}

export async function addWishlist(userId, catalogBookId) {
  const res = await api.post(`/wishlist/${userId}/${catalogBookId}`);
  return res.data;
}

export async function removeWishlist(userId, catalogBookId) {
  const res = await api.delete(`/wishlist/${userId}/${catalogBookId}`);
  return res.data;
}
