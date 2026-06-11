export default function EmptyState({ icon = '🔍', title, description }) {
  return (
    <div className="empty-state" data-testid="empty-state">
      <div className="empty-state__icon" aria-hidden="true">{icon}</div>
      <p className="empty-state__title">{title}</p>
      {description && <p className="text-muted mt-0">{description}</p>}
    </div>
  );
}
