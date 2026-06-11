import { getHabitatTypeOption } from '../data/options';

export default function HabitatBadge({ habitatType }) {
  const option = getHabitatTypeOption(habitatType);
  if (!option) return null;

  return (
    <span className="badge badge--sky" data-testid="habitat-badge">
      <span aria-hidden="true">{option.icon}</span> {option.label}
    </span>
  );
}
