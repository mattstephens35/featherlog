export default function ErrorBanner({ message }) {
  if (!message) return null;

  return (
    <div className="error-banner" data-testid="error-banner" role="alert">
      {message}
    </div>
  );
}
