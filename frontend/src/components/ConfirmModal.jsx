export default function ConfirmModal({ title, message, confirmLabel = 'Delete', onConfirm, onCancel }) {
  return (
    <div className="modal-overlay" data-testid="confirm-modal-overlay" onClick={onCancel}>
      <div className="modal" data-testid="confirm-modal" onClick={(event) => event.stopPropagation()}>
        <h3>{title}</h3>
        <p className="text-muted">{message}</p>
        <div className="modal__actions">
          <button
            type="button"
            className="btn btn--secondary"
            onClick={onCancel}
            data-testid="confirm-modal-cancel"
          >
            Cancel
          </button>
          <button
            type="button"
            className="btn btn--danger"
            onClick={onConfirm}
            data-testid="confirm-modal-confirm"
          >
            {confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}
