import { api } from "./api";

// create swap request
export async function createSwap(bookId, requesterId) {
  const res = await api.post("/swaps/", {
    book_id: bookId,
    requester_id: requesterId,
  });
  return res.data;
}

// INBOX: /swaps/inbox-view/{owner_id}
export async function inbox(ownerId) {
  const res = await api.get(`/swaps/inbox-view/${ownerId}`);
  return res.data;
}

// OUTBOX: /swaps/outbox/{requester_id}
export async function outbox(requesterId) {
  const res = await api.get(`/swaps/outbox/${requesterId}`);
  return res.data;
}

// accept/reject (query param owner_id)
export async function acceptSwap(swapId, ownerId) {
  const res = await api.post(`/swaps/${swapId}/accept`, null, {
    params: { owner_id: ownerId },
  });
  return res.data;
}

export async function rejectSwap(swapId, ownerId) {
  const res = await api.post(`/swaps/${swapId}/reject`, null, {
    params: { owner_id: ownerId },
  });
  return res.data;
}
