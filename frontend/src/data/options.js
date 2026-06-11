export const CONSERVATION_STATUS_OPTIONS = [
  { value: 'LEAST_CONCERN', label: 'Least Concern', badgeClass: 'badge--lc' },
  { value: 'NEAR_THREATENED', label: 'Near Threatened', badgeClass: 'badge--nt' },
  { value: 'VULNERABLE', label: 'Vulnerable', badgeClass: 'badge--vu' },
  { value: 'ENDANGERED', label: 'Endangered', badgeClass: 'badge--en' },
  { value: 'CRITICALLY_ENDANGERED', label: 'Critically Endangered', badgeClass: 'badge--cr' },
];

export const SIZE_CATEGORY_OPTIONS = [
  { value: 'SMALL', label: 'Small' },
  { value: 'MEDIUM', label: 'Medium' },
  { value: 'LARGE', label: 'Large' },
];

export const HABITAT_TYPE_OPTIONS = [
  { value: 'WETLAND', label: 'Wetland', icon: '💧' },
  { value: 'FOREST', label: 'Forest', icon: '🌲' },
  { value: 'GRASSLAND', label: 'Grassland', icon: '🌾' },
  { value: 'COASTAL', label: 'Coastal', icon: '🌊' },
  { value: 'MOUNTAIN', label: 'Mountain', icon: '⛰️' },
  { value: 'URBAN_PARK', label: 'Urban Park', icon: '🏙️' },
  { value: 'DESERT', label: 'Desert', icon: '🏜️' },
  { value: 'TUNDRA', label: 'Tundra', icon: '❄️' },
];

export const WEATHER_CONDITION_OPTIONS = [
  { value: 'SUNNY', label: 'Sunny', icon: '☀️' },
  { value: 'PARTLY_CLOUDY', label: 'Partly Cloudy', icon: '⛅' },
  { value: 'CLOUDY', label: 'Cloudy', icon: '☁️' },
  { value: 'OVERCAST', label: 'Overcast', icon: '🌥️' },
  { value: 'RAINY', label: 'Rainy', icon: '🌧️' },
  { value: 'FOGGY', label: 'Foggy', icon: '🌫️' },
  { value: 'SNOWY', label: 'Snowy', icon: '❄️' },
  { value: 'WINDY', label: 'Windy', icon: '💨' },
];

export function getConservationStatusOption(value) {
  return CONSERVATION_STATUS_OPTIONS.find((option) => option.value === value);
}

export function getHabitatTypeOption(value) {
  return HABITAT_TYPE_OPTIONS.find((option) => option.value === value);
}

export function getWeatherConditionOption(value) {
  return WEATHER_CONDITION_OPTIONS.find((option) => option.value === value);
}
