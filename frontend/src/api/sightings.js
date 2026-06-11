import apiClient from './client';

export async function fetchSightings(params = {}) {
  const { data } = await apiClient.get('/sightings', { params });
  return data;
}

export async function fetchSighting(id) {
  const { data } = await apiClient.get(`/sightings/${id}`);
  return data;
}

export async function createSighting(payload) {
  const { data } = await apiClient.post('/sightings', payload);
  return data;
}

export async function updateSighting(id, payload) {
  const { data } = await apiClient.put(`/sightings/${id}`, payload);
  return data;
}

export async function toggleSightingFavorite(id) {
  const { data } = await apiClient.patch(`/sightings/${id}/favorite`);
  return data;
}

export async function deleteSighting(id) {
  await apiClient.delete(`/sightings/${id}`);
}
