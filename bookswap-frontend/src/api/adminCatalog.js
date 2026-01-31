import { api } from "./api";

export async function adminDeactivateCatalog(adminId, catalogId) {
  const { data } = await api.post(`/catalog/${catalogId}/deactivate`, null, {
    params: { admin_id: adminId },
  });
  return data;
}

export async function adminActivateCatalog(adminId, catalogId) {
  const { data } = await api.post(`/catalog/${catalogId}/activate`, null, {
    params: { admin_id: adminId },
  });
  return data;
}

export async function adminAddCatalog(adminId, payload) {
  // dacă ai endpoint de create catalog la /catalog (ex: POST /catalog?admin_id=..)
  const { data } = await api.post(`/catalog`, payload, {
    params: { admin_id: adminId },
  });
  return data;
}
