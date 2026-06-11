import { useEffect, useState } from 'react';
import BirdCard from '../components/BirdCard';
import Pagination from '../components/Pagination';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import ErrorBanner from '../components/ErrorBanner';
import { fetchBirds, fetchBirdFamilies } from '../api/birds';
import { CONSERVATION_STATUS_OPTIONS } from '../data/options';

const PAGE_SIZE = 12;

export default function BirdsPage() {
  const [search, setSearch] = useState('');
  const [family, setFamily] = useState('');
  const [conservationStatus, setConservationStatus] = useState('');
  const [page, setPage] = useState(0);

  const [families, setFamilies] = useState([]);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchBirdFamilies()
      .then(setFamilies)
      .catch(() => {});
  }, []);

  useEffect(() => {
    const timeout = setTimeout(() => {
      setLoading(true);
      setError('');
      fetchBirds({
        search: search || undefined,
        family: family || undefined,
        conservationStatus: conservationStatus || undefined,
        page,
        size: PAGE_SIZE,
      })
        .then(setData)
        .catch(() => setError('Unable to load birds. Please try again later.'))
        .finally(() => setLoading(false));
    }, 300);

    return () => clearTimeout(timeout);
  }, [search, family, conservationStatus, page]);

  const handleSearchChange = (event) => {
    setSearch(event.target.value);
    setPage(0);
  };

  const handleFamilyChange = (event) => {
    setFamily(event.target.value);
    setPage(0);
  };

  const handleStatusChange = (event) => {
    setConservationStatus(event.target.value);
    setPage(0);
  };

  const handleClearFilters = () => {
    setSearch('');
    setFamily('');
    setConservationStatus('');
    setPage(0);
  };

  const hasFilters = Boolean(search || family || conservationStatus);

  return (
    <div className="page" data-testid="birds-page">
      <div className="container">
        <div className="page-header">
          <div>
            <h1 className="page-header__title">Bird Field Guide</h1>
            <p className="page-header__subtitle">
              Browse {data ? data.totalElements : '…'} species from around the world.
            </p>
          </div>
        </div>

        <div className="filters-bar">
          <div className="filters-bar__search">
            <input
              type="search"
              className="input"
              placeholder="Search by name, scientific name, or family…"
              value={search}
              onChange={handleSearchChange}
              data-testid="birds-search-input"
              aria-label="Search birds"
            />
          </div>
          <div className="filters-bar__field">
            <select
              className="select"
              value={family}
              onChange={handleFamilyChange}
              data-testid="birds-family-filter"
              aria-label="Filter by family"
            >
              <option value="">All families</option>
              {families.map((familyOption) => (
                <option key={familyOption} value={familyOption}>
                  {familyOption}
                </option>
              ))}
            </select>
          </div>
          <div className="filters-bar__field">
            <select
              className="select"
              value={conservationStatus}
              onChange={handleStatusChange}
              data-testid="birds-status-filter"
              aria-label="Filter by conservation status"
            >
              <option value="">All conservation statuses</option>
              {CONSERVATION_STATUS_OPTIONS.map((option) => (
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
              data-testid="birds-clear-filters"
            >
              Clear filters
            </button>
          )}
        </div>

        {error && <ErrorBanner message={error} />}

        {loading && <LoadingSpinner label="Loading birds…" />}

        {!loading && !error && data && data.content.length === 0 && (
          <EmptyState
            icon="🐦"
            title="No birds match your search"
            description="Try adjusting your filters or search term."
          />
        )}

        {!loading && !error && data && data.content.length > 0 && (
          <>
            <div className="card-grid" data-testid="birds-grid">
              {data.content.map((bird) => (
                <BirdCard key={bird.id} bird={bird} />
              ))}
            </div>
            <Pagination page={data.page} totalPages={data.totalPages} onPageChange={setPage} />
          </>
        )}
      </div>
    </div>
  );
}
