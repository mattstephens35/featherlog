import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import HeroIllustration from '../components/HeroIllustration';
import StatCard from '../components/StatCard';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorBanner from '../components/ErrorBanner';
import EmptyState from '../components/EmptyState';
import FlagIcon from '../components/FlagIcon';
import SightingListItem from '../components/SightingListItem';
import { fetchStatsSummary } from '../api/stats';
import { formatDate, formatNumber } from '../utils/format';

export default function Dashboard() {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    fetchStatsSummary()
      .then((data) => {
        if (!cancelled) setSummary(data);
      })
      .catch(() => {
        if (!cancelled) setError('Unable to load dashboard statistics. Please try again later.');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <div className="page" data-testid="dashboard-page">
      <div className="container">
        <section className="hero">
          <div>
            <span className="hero__eyebrow">Field Journal</span>
            <h1 className="hero__title">Welcome back, birder!</h1>
            <p className="hero__subtitle">
              Log your latest sightings, explore a growing field guide of species, and track the
              hotspots where you&apos;ve spotted something special.
            </p>
            <div className="hero__actions">
              <Link to="/sightings/new" className="btn btn--primary" data-testid="hero-log-sighting">
                Log a Sighting
              </Link>
              <Link to="/birds" className="btn btn--secondary" data-testid="hero-explore-birds">
                Explore Birds
              </Link>
            </div>
          </div>
          <HeroIllustration />
        </section>

        {loading && <LoadingSpinner label="Loading your dashboard…" />}
        {!loading && error && <ErrorBanner message={error} />}

        {!loading && !error && summary && (
          <>
            <div className="stat-grid">
              <StatCard
                icon="🔭"
                value={formatNumber(summary.totalSightings)}
                label="Total Sightings"
                testId="stat-total-sightings"
              />
              <StatCard
                icon="🐦"
                value={`${formatNumber(summary.speciesRecorded)} / ${formatNumber(summary.totalSpeciesCatalog)}`}
                label="Species Recorded"
                testId="stat-species-recorded"
              />
              <StatCard
                icon="📍"
                value={`${formatNumber(summary.locationsVisited)} / ${formatNumber(summary.totalLocationsCatalog)}`}
                label="Locations Visited"
                testId="stat-locations-visited"
              />
              <StatCard
                icon="📅"
                value={formatNumber(summary.sightingsThisMonth)}
                label="Sightings This Month"
                testId="stat-sightings-this-month"
              />
            </div>

            <div className="dashboard-grid">
              <div className="card dashboard-panel">
                <h2 className="section-title">Recent Sightings</h2>
                {summary.recentSightings.length === 0 ? (
                  <EmptyState
                    icon="🔭"
                    title="No sightings yet"
                    description="Log your first bird sighting to see it here."
                  />
                ) : (
                  <ul className="recent-list" data-testid="recent-sightings-list">
                    {summary.recentSightings.map((sighting) => (
                      <SightingListItem
                        key={sighting.id}
                        testId={`recent-sighting-${sighting.id}`}
                        icon={sighting.bird.icon}
                        iconBg={`${sighting.bird.colorHex}22`}
                        title={sighting.bird.commonName}
                        subtitle={
                          <>
                            <FlagIcon countryCode={sighting.location.countryCode} label={sighting.location.country} />{' '}
                            {sighting.location.name}
                          </>
                        }
                        meta={formatDate(sighting.sightingDate)}
                      />
                    ))}
                  </ul>
                )}
              </div>

              <div>
                <div className="card dashboard-panel">
                  <h2 className="section-title">Most Spotted Species</h2>
                  {summary.topBirds.length === 0 ? (
                    <EmptyState icon="🐦" title="No data yet" />
                  ) : (
                    <ul className="leaderboard" data-testid="top-birds-list">
                      {summary.topBirds.map((bird, index) => (
                        <li key={bird.birdId} className="leaderboard__item" data-testid={`top-bird-${bird.birdId}`}>
                          <span className="leaderboard__rank">{index + 1}</span>
                          <span
                            className="leaderboard__icon"
                            style={{ backgroundColor: `${bird.colorHex}22` }}
                            aria-hidden="true"
                          >
                            {bird.icon}
                          </span>
                          <span className="leaderboard__name">{bird.commonName}</span>
                          <span className="leaderboard__count">{bird.sightingCount}</span>
                        </li>
                      ))}
                    </ul>
                  )}
                </div>

                <div className="card dashboard-panel">
                  <h2 className="section-title">Top Locations</h2>
                  {summary.topLocations.length === 0 ? (
                    <EmptyState icon="📍" title="No data yet" />
                  ) : (
                    <ul className="leaderboard" data-testid="top-locations-list">
                      {summary.topLocations.map((location, index) => (
                        <li
                          key={location.locationId}
                          className="leaderboard__item"
                          data-testid={`top-location-${location.locationId}`}
                        >
                          <span className="leaderboard__rank">{index + 1}</span>
                          <span className="leaderboard__icon" aria-hidden="true">
                            <FlagIcon countryCode={location.countryCode} label={location.name} />
                          </span>
                          <span className="leaderboard__name">{location.name}</span>
                          <span className="leaderboard__count">{location.sightingCount}</span>
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
