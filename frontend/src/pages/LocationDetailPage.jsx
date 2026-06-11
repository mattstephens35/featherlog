import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import HabitatBadge from '../components/HabitatBadge';
import FlagIcon from '../components/FlagIcon';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorBanner from '../components/ErrorBanner';
import EmptyState from '../components/EmptyState';
import SightingListItem from '../components/SightingListItem';
import { fetchLocation } from '../api/locations';
import { fetchSightings } from '../api/sightings';
import { formatDate } from '../utils/format';

export default function LocationDetailPage() {
  const { id } = useParams();
  const [location, setLocation] = useState(null);
  const [sightings, setSightings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError('');
    Promise.all([fetchLocation(id), fetchSightings({ locationId: id, size: 5, sort: 'sightingDate,desc' })])
      .then(([locationData, sightingsData]) => {
        if (cancelled) return;
        setLocation(locationData);
        setSightings(sightingsData.content);
      })
      .catch(() => {
        if (!cancelled) setError('Unable to load this location. It may not exist.');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [id]);

  if (loading) {
    return (
      <div className="page" data-testid="location-detail-page">
        <div className="container">
          <LoadingSpinner label="Loading location…" />
        </div>
      </div>
    );
  }

  if (error || !location) {
    return (
      <div className="page" data-testid="location-detail-page">
        <div className="container">
          <Link to="/locations" className="back-link" data-testid="back-to-locations">
            ← Back to Locations
          </Link>
          <ErrorBanner message={error || 'Location not found.'} />
        </div>
      </div>
    );
  }

  return (
    <div className="page" data-testid="location-detail-page">
      <div className="container">
        <Link to="/locations" className="back-link" data-testid="back-to-locations">
          ← Back to Locations
        </Link>

        <div className="card detail-header">
          <div className="detail-header__flag">
            <FlagIcon countryCode={location.countryCode} label={location.country} />
          </div>
          <div>
            <h1 className="detail-header__title" data-testid="location-name">{location.name}</h1>
            <p className="detail-header__subtitle">
              {location.region ? `${location.region}, ` : ''}
              {location.country}
            </p>
            <div className="detail-header__badges">
              <HabitatBadge habitatType={location.habitatType} />
            </div>
          </div>
        </div>

        <div className="card detail-info-grid">
          <div className="detail-info-grid__item">
            <span>Latitude</span>
            <strong>{location.latitude}</strong>
          </div>
          <div className="detail-info-grid__item">
            <span>Longitude</span>
            <strong>{location.longitude}</strong>
          </div>
          <div className="detail-info-grid__item">
            <span>Best Season</span>
            <strong>{location.bestSeason || '—'}</strong>
          </div>
        </div>

        <div className="card detail-section">
          <h2 className="section-title">About this Location</h2>
          <p>{location.description}</p>
        </div>

        <div className="card detail-section">
          <h2 className="section-title">Recent Sightings Here</h2>
          {sightings.length === 0 ? (
            <EmptyState
              icon="🔭"
              title="No sightings logged yet"
              description="Be the first to log a sighting at this location."
            />
          ) : (
            <ul className="recent-list" data-testid="location-recent-sightings">
              {sightings.map((sighting) => (
                <SightingListItem
                  key={sighting.id}
                  testId={`recent-sighting-${sighting.id}`}
                  icon={sighting.bird.icon}
                  iconBg={`${sighting.bird.colorHex}22`}
                  title={sighting.bird.commonName}
                  subtitle={`${sighting.observerName} · Qty ${sighting.quantity}`}
                  meta={formatDate(sighting.sightingDate)}
                />
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}
