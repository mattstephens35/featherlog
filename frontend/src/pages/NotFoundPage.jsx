import { Link } from 'react-router-dom';

export default function NotFoundPage() {
  return (
    <div className="container">
      <div className="not-found" data-testid="not-found-page">
        <div className="not-found__icon" aria-hidden="true">🦤</div>
        <h1>Page not found</h1>
        <p className="text-muted">
          Looks like this page has flown the coop &mdash; the dodo went extinct, and so did this URL.
        </p>
        <Link to="/" className="btn btn--primary" data-testid="not-found-home-link">
          Back to Dashboard
        </Link>
      </div>
    </div>
  );
}
