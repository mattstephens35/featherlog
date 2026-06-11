import { useEffect, useState } from 'react';
import LocationCard from '../components/LocationCard';
import Pagination from '../components/Pagination';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import ErrorBanner from '../components/ErrorBanner';
import { fetchLocations, fetchLocationCountries } from '../api/locations';
import { HABITAT_TYPE_OPTIONS } from '../data/options';

const PAGE_SIZE = 12;

export default function LocationsPage() {
  const [search, setSearch] = useState('');
  const [country, setCountry] = useState('');
  const [habitatType, setHabitatType] = useState('');
  const [page, setPage] = useState(0);

  const [countries, setCountries] = useState([]);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchLocationCountries()
      .then(setCountries)
      .catch(() => {});
  }, []);

  useEffect(() => {
    const timeout = setTimeout(() => {
      setLoading(true);
      setError('');
      fetchLocations({
        search: search || undefined,
        country: country || undefined,
        habitatType: habitatType || undefined,
        page,
        size: PAGE_SIZE,
      })
        .then(setData)
        .catch(() => setError('Unable to load locations. Please try again later.'))
        .finally(() => setLoading(false));
    }, 300);

    return () => clearTimeout(timeout);
  }, [search, country, habitatType, page]);

  const handleSearchChange = (event) => {
    setSearch(event.target.value);
    setPage(0);
  };

  const handleCountryChange = (event) => {
    setCountry(event.target.value);
    setPage(0);
  };

  const handleHabitatChange = (event) => {
    setHabitatType(event.target.value);
    setPage(0);
  };

  const handleClearFilters = () => {
    setSearch('');
    setCountry('');
    setHabitatType('');
    setPage(0);
  };

  const hasFilters = Boolean(search || country || habitatType);

  return (
    <div className="page" data-testid="locations-page">
      <div className="container">
        <div className="page-header">
          <div>
            <h1 className="page-header__title">Birding Locations</h1>
            <p className="page-header__subtitle">
              Discover {data ? data.totalElements : '…'} hotspots around the world.
            </p>
          </div>
        </div>

        <div className="filters-bar">
          <div className="filters-bar__search">
            <input
              type="search"
              className="input"
              placeholder="Search by name, country, or region…"
              value={search}
              onChange={handleSearchChange}
              data-testid="locations-search-input"
              aria-label="Search locations"
            />
          </div>
          <div className="filters-bar__field">
            <select
              className="select"
              value={country}
              onChange={handleCountryChange}
              data-testid="locations-country-filter"
              aria-label="Filter by country"
            >
              <option value="">All countries</option>
              {countries.map((option) => (
                <option key={option.countryCode} value={option.country}>
                  {option.country}
                </option>
              ))}
            </select>
          </div>
          <div className="filters-bar__field">
            <select
              className="select"
              value={habitatType}
              onChange={handleHabitatChange}
              data-testid="locations-habitat-filter"
              aria-label="Filter by habitat type"
            >
              <option value="">All habitats</option>
              {HABITAT_TYPE_OPTIONS.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </div>
          {hasFilters && (
            <button
              type="button"
              className="btn btn--ghost filters-bar__clear"
              onClick={handleClearFilters}
              data-testid="locations-clear-filters"
            >
              Clear filters
            </button>
          )}
        </div>

        {error && <ErrorBanner message={error} />}
        {loading && <LoadingSpinner label="Loading locations…" />}

        {!loading && !error && data && data.content.length === 0 && (
          <EmptyState
            icon="📍"
            title="No locations match your search"
            description="Try adjusting your filters or search term."
          />
        )}

        {!loading && !error && data && data.content.length > 0 && (
          <>
            <div className="card-grid" data-testid="locations-grid">
              {data.content.map((location) => (
                <LocationCard key={location.id} location={location} />
              ))}
            </div>
            <Pagination page={data.page} totalPages={data.totalPages} onPageChange={setPage} />
          </>
        )}
      </div>
    </div>
  );
}
