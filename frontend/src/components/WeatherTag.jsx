import { getWeatherConditionOption } from '../data/options';

export default function WeatherTag({ condition }) {
  const option = getWeatherConditionOption(condition);

  if (!option) {
    return (
      <span className="text-muted" data-testid="weather-tag">
        &mdash;
      </span>
    );
  }

  return (
    <span className="flex-row" data-testid="weather-tag">
      <span aria-hidden="true">{option.icon}</span>
      <span>{option.label}</span>
    </span>
  );
}
