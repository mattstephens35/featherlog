import apiClient from './client';

export async function fetchBirds(params = {}) {
  const { data } = await apiClient.get('/birds', { params });
  return data;
}

export async function fetchAllBirds() {
  const { data } = await apiClient.get('/birds', {
    params: { size: 100, sort: 'commonName,asc' },
  });
  return data.content;
}

export async function fetchBird(id) {
  const { data } = await apiClient.get(`/birds/${id}`);
  return data;
}

export async function fetchBirdFamilies() {
  const { data } = await apiClient.get('/birds/families');
  return data;
}

export async function createBird(payload) {
  const { data } = await apiClient.post('/birds', payload);
  return data;
}

export async function updateBird(id, payload) {
  const { data } = await apiClient.put(`/birds/${id}`, payload);
  return data;
}

export async function deleteBird(id) {
  await apiClient.delete(`/birds/${id}`);
}
