import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Pagination from '../components/Pagination';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import ErrorBanner from '../components/ErrorBanner';
import ConfirmModal from '../components/ConfirmModal';
import FlagIcon from '../components/FlagIcon';
import WeatherTag from '../components/WeatherTag';
import { useToast } from '../components/ToastContext';
import { fetchSightings, deleteSighting, toggleSightingFavorite } from '../api/sightings';
import { fetchAllBirds } from '../api/birds';
import { fetchAllLocations } from '../api/locations';
import { formatDate, formatTime } from '../utils/format';

const PAGE_SIZE = 10;

export default function SightingsPage() {
  const { showToast } = useToast();

  const [search, setSearch] = useState('');
  const [birdId, setBirdId] = useState('');
  const [locationId, setLocationId] = useState('');
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');
  const [favoritesOnly, setFavoritesOnly] = useState(false);
  const [page, setPage] = useState(0);

  const [birds, setBirds] = useState([]);
  const [locations, setLocations] = useState([]);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [deleteTarget, setDeleteTarget] = useState(null);

  useEffect(() => {
    fetchAllBirds().then(setBirds).catch(() => {});
    fetchAllLocations().then(setLocations).catch(() => {});
  }, []);

  useEffect(() => {
    const timeout = setTimeout(() => {
      setLoading(true);
      setError('');
      fetchSightings({
        search: search || undefined,
        birdId: birdId || undefined,
        locationId: locationId || undefined,
        fromDate: fromDate || undefined,
        toDate: toDate || undefined,
        favorite: favoritesOnly ? true : undefined,
        page,
        size: PAGE_SIZE,
      })
        .then(setData)
        .catch(() => setError('Unable to load sightings. Please try again later.'))
        .finally(() => setLoading(false));
    }, 300);

    return () => clearTimeout(timeout);
  }, [search, birdId, locationId, fromDate, toDate, favoritesOnly, page]);

  const handleFilterChange = (setter) => (event) => {
    setter(event.target.value);
    setPage(0);
  };

  const handleFavoritesToggle = (event) => {
    setFavoritesOnly(event.target.checked);
    setPage(0);
  };

  const handleClearFilters = () => {
    setSearch('');
    setBirdId('');
    setLocationId('');
    setFromDate('');
    setToDate('');
    setFavoritesOnly(false);
    setPage(0);
  };

  const handleToggleFavorite = async (sighting) => {
    try {
      const updated = await toggleSightingFavorite(sighting.id);
      setData((current) => ({
        ...current,
        content: current.content.map((item) => (item.id === updated.id ? updated : item)),
      }));
    } catch {
      showToast('Unable to update favorite status.', 'error');
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    const target = deleteTarget;
    try {
      await deleteSighting(target.id);
      showToast('Sighting deleted.', 'success');
      setDeleteTarget(null);
      if (data.content.length === 1 && page > 0) {
        setPage((current) => current - 1);
      } else {
        setData((current) => ({
          ...current,
          content: current.content.filter((item) => item.id !== target.id),
          totalElements: current.totalElements - 1,
        }));
      }
    } catch {
      showToast('Unable to delete sighting.', 'error');
      setDeleteTarget(null);
    }
  };

  const hasFilters = Boolean(search || birdId || locationId || fromDate || toDate || favoritesOnly);

  return (
    <div className="page" data-testid="sightings-page">
      <div className="container">
        <div className="page-header">
          <div>
            <h1 className="page-header__title">Sighting Journal</h1>
            <p className="page-header__subtitle">
              {data ? data.totalElements : '…'} sightings logged so far.
            </p>
          </div>
          <div className="page-header__actions">
            <Link to="/sightings/new" className="btn btn--primary" data-testid="add-sighting-button">
              + Log Sighting
            </Link>
          </div>
        </div>

        <div className="filters-bar">
          <div className="filters-bar__search">
            <input
              type="search"
              className="input"
              placeholder="Search notes, observer, bird, or location…"
              value={search}
              onChange={handleFilterChange(setSearch)}
              data-testid="sightings-search-input"
              aria-label="Search sightings"
            />
          </div>
          <div className="filters-bar__field">
            <select
              className="select"
              value={birdId}
              onChange={handleFilterChange(setBirdId)}
              data-testid="sightings-bird-filter"
              aria-label="Filter by bird"
            >
              <option value="">All birds</option>
              {birds.map((bird) => (
                <option key={bird.id} value={bird.id}>
                  {bird.commonName}
                </option>
              ))}
            </select>
          </div>
          <div className="filters-bar__field">
            <select
              className="select"
              value={locationId}
              onChange={handleFilterChange(setLocationId)}
              data-testid="sightings-location-filter"
              aria-label="Filter by location"
            >
              <option value="">All locations</option>
              {locations.map((location) => (
                <option key={location.id} value={location.id}>
                  {location.name}
                </option>
              ))}
            </select>
          </div>
          <div className="filters-bar__field">
            <input
              type="date"
              className="input"
              value={fromDate}
              onChange={handleFilterChange(setFromDate)}
              data-testid="sightings-from-date"
              aria-label="From date"
            />
          </div>
          <div className="filters-bar__field">
            <input
              type="date"
              className="input"
              value={toDate}
              onChange={handleFilterChange(setToDate)}
              data-testid="sightings-to-date"
              aria-label="To date"
            />
          </div>
          <label className="filters-bar__checkbox">
            <input
              type="checkbox"
              checked={favoritesOnly}
              onChange={handleFavoritesToggle}
              data-testid="sightings-favorites-filter"
            />
            Favorites only
          </label>
          {hasFilters && (
            <button
              type="button"
              className="btn btn--ghost filters-bar__clear"
              onClick={handleClearFilters}
              data-testid="sightings-clear-filters"
            >
              Clear filters
            </button>
          )}
        </div>

        {error && <ErrorBanner message={error} />}
        {loading && <LoadingSpinner label="Loading sightings…" />}

        {!loading && !error && data && data.content.length === 0 && (
          <EmptyState
            icon="🔭"
            title="No sightings found"
            description="Try adjusting your filters, or log a new sighting to get started."
          />
        )}

        {!loading && !error && data && data.content.length > 0 && (
          <>
            <div className="sightings-list" data-testid="sightings-list">
              {data.content.map((sighting) => (
                <div className="sighting-row" key={sighting.id} data-testid={`sighting-row-${sighting.id}`}>
                  <div className="sighting-row__date">
                    <strong>{formatDate(sighting.sightingDate)}</strong>
                    {sighting.sightingTime && <span>{formatTime(sighting.sightingTime)}</span>}
                  </div>

                  <Link to={`/birds/${sighting.bird.id}`} className="sighting-row__entity">
                    <div
                      className="sighting-row__entity-icon"
                      style={{ backgroundColor: `${sighting.bird.colorHex}22` }}
                      aria-hidden="true"
                    >
                      {sighting.bird.icon}
                    </div>
                    <div className="sighting-row__entity-text">
                      <strong>{sighting.bird.commonName}</strong>
                      <span>{sighting.bird.scientificName}</span>
                    </div>
                  </Link>

                  <Link to={`/locations/${sighting.location.id}`} className="sighting-row__entity">
                    <div className="sighting-row__entity-flag">
                      <FlagIcon countryCode={sighting.location.countryCode} label={sighting.location.country} />
                    </div>
                    <div className="sighting-row__entity-text">
                      <strong>{sighting.location.name}</strong>
                      <span>{sighting.location.country}</span>
                    </div>
                  </Link>

                  <div className="sighting-row__quantity" data-testid={`sighting-quantity-${sighting.id}`}>
                    {sighting.quantity}
                  </div>

                  <div className="sighting-row__weather">
                    <WeatherTag condition={sighting.weatherCondition} />
                  </div>

                  <div className="sighting-row__notes" title={sighting.notes || ''}>
                    {sighting.notes || <span className="text-muted">No notes</span>}
                  </div>

                  <div className="sighting-row__actions">
                    <button
                      type="button"
                      className={`favorite-toggle ${sighting.favorite ? 'favorite-toggle--active' : ''}`}
                      onClick={() => handleToggleFavorite(sighting)}
                      aria-label={sighting.favorite ? 'Remove from favorites' : 'Mark as favorite'}
                      data-testid={`favorite-toggle-${sighting.id}`}
                    >
                      {sighting.favorite ? '★' : '☆'}
                    </button>
                    <Link
                      to={`/sightings/${sighting.id}/edit`}
                      className="btn btn--secondary btn--sm"
                      data-testid={`edit-sighting-${sighting.id}`}
                    >
                      Edit
                    </Link>
                    <button
                      type="button"
                      className="btn btn--danger btn--sm"
                      onClick={() => setDeleteTarget(sighting)}
                      data-testid={`delete-sighting-${sighting.id}`}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              ))}
            </div>
            <Pagination page={data.page} totalPages={data.totalPages} onPageChange={setPage} />
          </>
        )}

        {deleteTarget && (
          <ConfirmModal
            title="Delete sighting?"
            message={`This will permanently remove the ${deleteTarget.bird.commonName} sighting at ${deleteTarget.location.name} on ${formatDate(deleteTarget.sightingDate)}.`}
            confirmLabel="Delete"
            onConfirm={handleDelete}
            onCancel={() => setDeleteTarget(null)}
          />
        )}
      </div>
    </div>
  );
}
