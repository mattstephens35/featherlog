export default function LoadingSpinner({ label = 'Loading…' }) {
  return (
    <div className="loading-state" data-testid="loading-state">
      <div className="spinner" aria-hidden="true" />
      <span>{label}</span>
    </div>
  );
}
