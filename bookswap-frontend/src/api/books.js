import { api } from "./api";

export async function getAvailableBooks(userId, q = "", author = "") {
  const { data } = await api.get("/books/available", {
    params: { user_id: userId, q, author },
  });
  return data;
}

export async function getMyBooks(userId) {
  const { data } = await api.get(`/books/mine/${userId}`);
  return data;
}

export async function addFromCatalog(userId, catalogBookId) {
  const { data } = await api.post(
    "/books/from-catalog",
    { catalog_book_id: catalogBookId },
    { params: { owner_id: userId } }
  );
  return data;
}

export async function deleteMyBook(bookId, ownerId) {
  const { data } = await api.delete(`/books/${bookId}`, { params: { owner_id: ownerId } });
  return data;
}
