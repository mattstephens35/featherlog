import { Link } from 'react-router-dom';

export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer__inner">
        <div className="footer__brand">
          <span aria-hidden="true">🪶</span> FeatherLog
        </div>
        <span className="text-muted">
          Built for birders &mdash; track sightings, explore species, and discover new hotspots.
        </span>
        <div className="footer__links">
          <Link to="/birds" data-testid="footer-link-birds">Birds</Link>
          <Link to="/locations" data-testid="footer-link-locations">Locations</Link>
          <Link to="/sightings" data-testid="footer-link-sightings">Sightings</Link>
        </div>
      </div>
    </footer>
  );
}
