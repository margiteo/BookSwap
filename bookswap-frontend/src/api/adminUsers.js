import { api } from "./api";

export async function listUsers(adminId) {
  const { data } = await api.get("/admin/users", {
    params: { admin_id: adminId },
  });
  return data;
}

export async function deactivateUser(adminId, userId) {
  const { data } = await api.post(`/admin/users/${userId}/deactivate`, null, {
    params: { admin_id: adminId },
  });
  return data;
}

export async function activateUser(adminId, userId) {
  const { data } = await api.post(`/admin/users/${userId}/activate`, null, {
    params: { admin_id: adminId },
  });
  return data;
}
