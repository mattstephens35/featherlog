import apiClient from './client';

export async function fetchLocations(params = {}) {
  const { data } = await apiClient.get('/locations', { params });
  return data;
}

export async function fetchAllLocations() {
  const { data } = await apiClient.get('/locations', {
    params: { size: 100, sort: 'name,asc' },
  });
  return data.content;
}

export async function fetchLocation(id) {
  const { data } = await apiClient.get(`/locations/${id}`);
  return data;
}

export async function fetchLocationCountries() {
  const { data } = await apiClient.get('/locations/countries');
  return data;
}

export async function createLocation(payload) {
  const { data } = await apiClient.post('/locations', payload);
  return data;
}

export async function updateLocation(id, payload) {
  const { data } = await apiClient.put(`/locations/${id}`, payload);
  return data;
}

export async function deleteLocation(id) {
  await apiClient.delete(`/locations/${id}`);
}
