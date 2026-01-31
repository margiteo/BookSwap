import { api } from "./api";

export async function adminAddCatalog(adminId, payload) {
  const { data } = await api.post(`/admin/catalog`, payload, {
    params: { admin_id: adminId },
  });
  return data;
}
