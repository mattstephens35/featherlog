export default function StatCard({ icon, value, label, testId }) {
  return (
    <div className="stat-card" data-testid={testId}>
      <div className="stat-card__icon" aria-hidden="true">{icon}</div>
      <div>
        <div className="stat-card__value">{value}</div>
        <div className="stat-card__label">{label}</div>
      </div>
    </div>
  );
}
