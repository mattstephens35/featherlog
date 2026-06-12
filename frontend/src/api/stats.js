import apiClient from './client';

export async function fetchStatsSummary() {
  const { data } = await apiClient.get('/stats/summary');
  return data;
}

export async function fetchStatsTimeline() {
  const { data } = await apiClient.get('/stats/timeline');
  return data;
}

export async function fetchConservationBreakdown() {
  const { data } = await apiClient.get('/stats/conservation');
  return data;
}

export async function fetchLifers() {
  const { data } = await apiClient.get('/stats/lifers');
  return data;
}

export async function fetchActivity() {
  const { data } = await apiClient.get('/stats/activity');
  return data;
}
