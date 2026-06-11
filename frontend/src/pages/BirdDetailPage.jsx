import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import ConservationBadge from '../components/ConservationBadge';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorBanner from '../components/ErrorBanner';
import EmptyState from '../components/EmptyState';
import FlagIcon from '../components/FlagIcon';
import SightingListItem from '../components/SightingListItem';
import { fetchBird } from '../api/birds';
import { fetchSightings } from '../api/sightings';
import { formatDate } from '../utils/format';
import { SIZE_CATEGORY_OPTIONS } from '../data/options';

export default function BirdDetailPage() {
  const { id } = useParams();
  const [bird, setBird] = useState(null);
  const [sightings, setSightings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError('');
    Promise.all([fetchBird(id), fetchSightings({ birdId: id, size: 5, sort: 'sightingDate,desc' })])
      .then(([birdData, sightingsData]) => {
        if (cancelled) return;
        setBird(birdData);
        setSightings(sightingsData.content);
      })
      .catch(() => {
        if (!cancelled) setError('Unable to load this bird. It may not exist.');
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
      <div className="page" data-testid="bird-detail-page">
        <div className="container">
          <LoadingSpinner label="Loading bird…" />
        </div>
      </div>
    );
  }

  if (error || !bird) {
    return (
      <div className="page" data-testid="bird-detail-page">
        <div className="container">
          <Link to="/birds" className="back-link" data-testid="back-to-birds">
            ← Back to Birds
          </Link>
          <ErrorBanner message={error || 'Bird not found.'} />
        </div>
      </div>
    );
  }

  const sizeLabel = SIZE_CATEGORY_OPTIONS.find((option) => option.value === bird.sizeCategory)?.label
    || bird.sizeCategory;

  return (
    <div className="page" data-testid="bird-detail-page">
      <div className="container">
        <Link to="/birds" className="back-link" data-testid="back-to-birds">
          ← Back to Birds
        </Link>

        <div className="card detail-header">
          <div
            className="detail-header__icon"
            style={{ backgroundColor: `${bird.colorHex}22` }}
            aria-hidden="true"
          >
            {bird.icon}
          </div>
          <div>
            <h1 className="detail-header__title" data-testid="bird-name">{bird.commonName}</h1>
            <p className="detail-header__subtitle">{bird.scientificName}</p>
            <div className="detail-header__badges">
              <ConservationBadge status={bird.conservationStatus} />
              <span className="badge badge--neutral">{bird.family}</span>
              <span className="badge badge--neutral">{sizeLabel}</span>
              {bird.migratory && <span className="badge badge--accent">Migratory</span>}
            </div>
          </div>
        </div>

        <div className="card detail-info-grid">
          <div className="detail-info-grid__item">
            <span>Average Length</span>
            <strong>{bird.averageLengthCm} cm</strong>
          </div>
          <div className="detail-info-grid__item">
            <span>Average Weight</span>
            <strong>{bird.averageWeightGrams} g</strong>
          </div>
          <div className="detail-info-grid__item">
            <span>Habitat</span>
            <strong>{bird.habitat}</strong>
          </div>
          <div className="detail-info-grid__item">
            <span>Diet</span>
            <strong>{bird.diet}</strong>
          </div>
          <div className="detail-info-grid__item">
            <span>Migratory</span>
            <strong>{bird.migratory ? 'Yes' : 'No'}</strong>
          </div>
        </div>

        <div className="card detail-section">
          <h2 className="section-title">About this Species</h2>
          <p>{bird.description}</p>
        </div>

        <div className="card detail-section">
          <h2 className="section-title">Recent Sightings</h2>
          {sightings.length === 0 ? (
            <EmptyState
              icon="🔭"
              title="No sightings logged yet"
              description="Be the first to log a sighting of this species."
            />
          ) : (
            <ul className="recent-list" data-testid="bird-recent-sightings">
              {sightings.map((sighting) => (
                <SightingListItem
                  key={sighting.id}
                  testId={`recent-sighting-${sighting.id}`}
                  icon={<FlagIcon countryCode={sighting.location.countryCode} label={sighting.location.country} />}
                  title={sighting.location.name}
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
