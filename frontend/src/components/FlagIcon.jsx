export default function FlagIcon({ countryCode, label }) {
  if (!countryCode) return null;

  return (
    <span
      className={`fi fi-${countryCode.toLowerCase()}`}
      role="img"
      aria-label={label || countryCode.toUpperCase()}
      data-testid="flag-icon"
    />
  );
}
