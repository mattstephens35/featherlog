import apiClient from './client';

export async function fetchStatsSummary() {
  const { data } = await apiClient.get('/stats/summary');
  return data;
}
