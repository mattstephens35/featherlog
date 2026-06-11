import { getConservationStatusOption } from '../data/options';

export default function ConservationBadge({ status }) {
  const option = getConservationStatusOption(status);
  if (!option) return null;

  return (
    <span className={`badge ${option.badgeClass}`} data-testid="conservation-badge">
      {option.label}
    </span>
  );
}
