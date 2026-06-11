import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorBanner from '../components/ErrorBanner';
import { useToast } from '../components/ToastContext';
import { fetchAllBirds } from '../api/birds';
import { fetchAllLocations } from '../api/locations';
import { fetchSighting, createSighting, updateSighting } from '../api/sightings';
import { WEATHER_CONDITION_OPTIONS } from '../data/options';

const today = () => new Date().toISOString().slice(0, 10);

const emptyForm = {
  birdId: '',
  locationId: '',
  sightingDate: today(),
  sightingTime: '',
  quantity: '1',
  observerName: '',
  weatherCondition: '',
  notes: '',
  favorite: false,
};

export default function SightingFormPage({ mode }) {
  const navigate = useNavigate();
  const { id } = useParams();
  const { showToast } = useToast();
  const isEdit = mode === 'edit';

  const [birds, setBirds] = useState([]);
  const [locations, setLocations] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(isEdit);
  const [submitting, setSubmitting] = useState(false);
  const [loadError, setLoadError] = useState('');

  useEffect(() => {
    Promise.all([fetchAllBirds(), fetchAllLocations()])
      .then(([birdsData, locationsData]) => {
        setBirds(birdsData);
        setLocations(locationsData);
      })
      .catch(() => setLoadError('Unable to load birds and locations.'));
  }, []);

  useEffect(() => {
    if (!isEdit) return undefined;

    let cancelled = false;
    setLoading(true);
    fetchSighting(id)
      .then((sighting) => {
        if (cancelled) return;
        setForm({
          birdId: String(sighting.bird.id),
          locationId: String(sighting.location.id),
          sightingDate: sighting.sightingDate,
          sightingTime: sighting.sightingTime || '',
          quantity: String(sighting.quantity),
          observerName: sighting.observerName,
          weatherCondition: sighting.weatherCondition || '',
          notes: sighting.notes || '',
          favorite: sighting.favorite,
        });
      })
      .catch(() => setLoadError('Unable to load this sighting. It may not exist.'))
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [id, isEdit]);

  const handleChange = (field) => (event) => {
    const value = event.target.type === 'checkbox' ? event.target.checked : event.target.value;
    setForm((current) => ({ ...current, [field]: value }));
  };

  const validate = () => {
    const newErrors = {};
    if (!form.birdId) newErrors.birdId = 'Please select a bird.';
    if (!form.locationId) newErrors.locationId = 'Please select a location.';

    if (!form.sightingDate) {
      newErrors.sightingDate = 'Please select a date.';
    } else if (form.sightingDate > today()) {
      newErrors.sightingDate = 'Date cannot be in the future.';
    }

    const quantityNum = Number(form.quantity);
    if (!form.quantity || !Number.isInteger(quantityNum) || quantityNum < 1 || quantityNum > 10000) {
      newErrors.quantity = 'Quantity must be a whole number between 1 and 10,000.';
    }

    if (!form.observerName.trim()) {
      newErrors.observerName = 'Please enter an observer name.';
    }

    return newErrors;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    setSubmitting(true);
    setErrors({});

    const payload = {
      birdId: Number(form.birdId),
      locationId: Number(form.locationId),
      sightingDate: form.sightingDate,
      sightingTime: form.sightingTime || null,
      quantity: Number(form.quantity),
      observerName: form.observerName.trim(),
      weatherCondition: form.weatherCondition || null,
      notes: form.notes.trim() || null,
      favorite: form.favorite,
    };

    try {
      if (isEdit) {
        await updateSighting(id, payload);
        showToast('Sighting updated successfully.', 'success');
      } else {
        await createSighting(payload);
        showToast('Sighting logged successfully.', 'success');
      }
      navigate('/sightings');
    } catch (err) {
      const responseErrors = err?.response?.data?.fieldErrors;
      if (responseErrors) {
        setErrors(responseErrors);
      } else {
        showToast('Unable to save sighting. Please try again.', 'error');
      }
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="page" data-testid="sighting-form-page">
        <div className="container">
          <LoadingSpinner label="Loading sighting…" />
        </div>
      </div>
    );
  }

  if (loadError) {
    return (
      <div className="page" data-testid="sighting-form-page">
        <div className="container">
          <Link to="/sightings" className="back-link" data-testid="back-to-sightings">
            ← Back to Sightings
          </Link>
          <ErrorBanner message={loadError} />
        </div>
      </div>
    );
  }

  return (
    <div className="page" data-testid="sighting-form-page">
      <div className="container">
        <Link to="/sightings" className="back-link" data-testid="back-to-sightings">
          ← Back to Sightings
        </Link>

        <div className="page-header">
          <div>
            <h1 className="page-header__title">{isEdit ? 'Edit Sighting' : 'Log a New Sighting'}</h1>
            <p className="page-header__subtitle">
              {isEdit ? 'Update the details of this sighting.' : 'Record a bird you spotted in the field.'}
            </p>
          </div>
        </div>

        <form className="card detail-section" onSubmit={handleSubmit} data-testid="sighting-form" noValidate>
          <div className="form-grid">
            <div className="form-field">
              <label className="form-field__label" htmlFor="bird">Bird *</label>
              <select
                id="bird"
                className={`select ${errors.birdId ? 'select--error' : ''}`}
                value={form.birdId}
                onChange={handleChange('birdId')}
                data-testid="field-bird"
              >
                <option value="">Select a bird…</option>
                {birds.map((bird) => (
                  <option key={bird.id} value={bird.id}>
                    {bird.icon} {bird.commonName}
                  </option>
                ))}
              </select>
              {errors.birdId && (
                <span className="form-field__error" data-testid="error-birdId">{errors.birdId}</span>
              )}
            </div>

            <div className="form-field">
              <label className="form-field__label" htmlFor="location">Location *</label>
              <select
                id="location"
                className={`select ${errors.locationId ? 'select--error' : ''}`}
                value={form.locationId}
                onChange={handleChange('locationId')}
                data-testid="field-location"
              >
                <option value="">Select a location…</option>
                {locations.map((location) => (
                  <option key={location.id} value={location.id}>
                    {location.name}
                  </option>
                ))}
              </select>
              {errors.locationId && (
                <span className="form-field__error" data-testid="error-locationId">{errors.locationId}</span>
              )}
            </div>

            <div className="form-field">
              <label className="form-field__label" htmlFor="sightingDate">Date *</label>
              <input
                id="sightingDate"
                type="date"
                className={`input ${errors.sightingDate ? 'input--error' : ''}`}
                value={form.sightingDate}
                onChange={handleChange('sightingDate')}
                max={today()}
                data-testid="field-date"
              />
              {errors.sightingDate && (
                <span className="form-field__error" data-testid="error-sightingDate">{errors.sightingDate}</span>
              )}
            </div>

            <div className="form-field">
              <label className="form-field__label" htmlFor="sightingTime">Time</label>
              <input
                id="sightingTime"
                type="time"
                className="input"
                value={form.sightingTime}
                onChange={handleChange('sightingTime')}
                data-testid="field-time"
              />
              <span className="form-field__hint">Optional</span>
            </div>

            <div className="form-field">
              <label className="form-field__label" htmlFor="quantity">Quantity *</label>
              <input
                id="quantity"
                type="number"
                min="1"
                max="10000"
                step="1"
                className={`input ${errors.quantity ? 'input--error' : ''}`}
                value={form.quantity}
                onChange={handleChange('quantity')}
                data-testid="field-quantity"
              />
              {errors.quantity && (
                <span className="form-field__error" data-testid="error-quantity">{errors.quantity}</span>
              )}
            </div>

            <div className="form-field">
              <label className="form-field__label" htmlFor="observerName">Observer Name *</label>
              <input
                id="observerName"
                type="text"
                maxLength={100}
                className={`input ${errors.observerName ? 'input--error' : ''}`}
                value={form.observerName}
                onChange={handleChange('observerName')}
                data-testid="field-observer"
              />
              {errors.observerName && (
                <span className="form-field__error" data-testid="error-observerName">{errors.observerName}</span>
              )}
            </div>

            <div className="form-field">
              <label className="form-field__label" htmlFor="weatherCondition">Weather</label>
              <select
                id="weatherCondition"
                className="select"
                value={form.weatherCondition}
                onChange={handleChange('weatherCondition')}
                data-testid="field-weather"
              >
                <option value="">Not specified</option>
                {WEATHER_CONDITION_OPTIONS.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.icon} {option.label}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-field">
              <label className="checkbox-field" htmlFor="favorite">
                <input
                  id="favorite"
                  type="checkbox"
                  checked={form.favorite}
                  onChange={handleChange('favorite')}
                  data-testid="field-favorite"
                />
                Mark as a favorite sighting
              </label>
            </div>

            <div className="form-field form-field--full">
              <label className="form-field__label" htmlFor="notes">Notes</label>
              <textarea
                id="notes"
                className="textarea"
                maxLength={2000}
                value={form.notes}
                onChange={handleChange('notes')}
                placeholder="Behavior, plumage details, weather, anything notable…"
                data-testid="field-notes"
              />
            </div>
          </div>

          <div className="form-actions">
            <Link to="/sightings" className="btn btn--secondary" data-testid="cancel-sighting">
              Cancel
            </Link>
            <button type="submit" className="btn btn--primary" disabled={submitting} data-testid="submit-sighting">
              {submitting ? 'Saving…' : isEdit ? 'Update Sighting' : 'Save Sighting'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
