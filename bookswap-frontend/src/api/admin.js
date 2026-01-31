import { api } from "./api";

// stats
export async function topRequested(adminId, limit = 10) {
  const { data } = await api.get("/admin/stats/top-requested", { params: { admin_id: adminId, limit } });
  return data;
}

// catalog admin: dacă ai endpoint-uri de add/deactivate/delete, le legăm după ce îmi confirmi path-urile
