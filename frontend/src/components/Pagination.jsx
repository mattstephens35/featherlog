export default function Pagination({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) return null;

  const isFirst = page <= 0;
  const isLast = page >= totalPages - 1;

  return (
    <div className="pagination" data-testid="pagination">
      <button
        type="button"
        className="btn btn--secondary btn--sm"
        onClick={() => onPageChange(page - 1)}
        disabled={isFirst}
        data-testid="pagination-prev"
      >
        ← Previous
      </button>
      <span className="pagination__info" data-testid="pagination-info">
        Page {page + 1} of {totalPages}
      </span>
      <button
        type="button"
        className="btn btn--secondary btn--sm"
        onClick={() => onPageChange(page + 1)}
        disabled={isLast}
        data-testid="pagination-next"
      >
        Next →
      </button>
    </div>
  );
}
