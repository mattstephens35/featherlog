import { useState } from 'react';
import { Link, NavLink } from 'react-router-dom';
import { useTheme } from './ThemeContext';

const NAV_LINKS = [
  { to: '/', label: 'Dashboard', testId: 'nav-dashboard', end: true },
  { to: '/birds', label: 'Birds', testId: 'nav-birds' },
  { to: '/locations', label: 'Locations', testId: 'nav-locations' },
  { to: '/map', label: 'Map', testId: 'nav-map' },
  { to: '/sightings', label: 'Sightings', testId: 'nav-sightings' },
  { to: '/stats', label: 'Stats', testId: 'nav-stats' },
];

export default function Navbar() {
  const [menuOpen, setMenuOpen] = useState(false);
  const { theme, toggleTheme } = useTheme();

  const closeMenu = () => setMenuOpen(false);

  return (
    <header className="navbar">
      <div className="navbar__inner">
        <Link to="/" className="navbar__brand" data-testid="nav-brand" onClick={closeMenu}>
          <span className="navbar__brand-icon" aria-hidden="true">🪶</span>
          FeatherLog
        </Link>

        <nav className={`navbar__links ${menuOpen ? '' : 'navbar__links--closed'}`} data-testid="nav-links">
          {NAV_LINKS.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              end={link.end}
              data-testid={link.testId}
              onClick={closeMenu}
              className={({ isActive }) => `navbar__link${isActive ? ' navbar__link--active' : ''}`}
            >
              {link.label}
            </NavLink>
          ))}
        </nav>

        <div className="navbar__actions">
          <Link
            to="/sightings/new"
            className="btn btn--primary btn--sm"
            data-testid="nav-log-sighting"
            onClick={closeMenu}
          >
            <span aria-hidden="true">+</span>
            <span className="navbar__cta-text">Log Sighting</span>
          </Link>
          <button
            type="button"
            className="btn btn--icon btn--secondary navbar__theme-toggle"
            onClick={toggleTheme}
            aria-label={theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'}
            data-testid="nav-theme-toggle"
          >
            {theme === 'dark' ? '☀️' : '🌙'}
          </button>
          <button
            type="button"
            className="btn btn--icon btn--secondary navbar__toggle"
            onClick={() => setMenuOpen((open) => !open)}
            aria-label="Toggle navigation menu"
            aria-expanded={menuOpen}
            data-testid="nav-toggle"
          >
            {menuOpen ? '✕' : '☰'}
          </button>
        </div>
      </div>
    </header>
  );
}
