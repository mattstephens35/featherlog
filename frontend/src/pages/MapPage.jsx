import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import L from 'leaflet';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorBanner from '../components/ErrorBanner';
import HabitatBadge from '../components/HabitatBadge';
import FlagIcon from '../components/FlagIcon';
import { fetchAllLocations } from '../api/locations';

function createDivIcon(emoji) {
  return L.divIcon({
    html: `<span class="map-marker__icon"><span class="map-marker__icon-inner">${emoji}</span></span>`,
    className: 'map-marker',
    iconSize: [36, 36],
    iconAnchor: [18, 36],
    popupAnchor: [0, -36],
  });
}

const DEFAULT_CENTER = [20, 0];
const DEFAULT_ZOOM = 2;

export default function MapPage() {
  const [locations, setLocations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    fetchAllLocations()
      .then((data) => {
        if (!cancelled) setLocations(data);
      })
      .catch(() => {
        if (!cancelled) setError('Unable to load locations for the map.');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <div className="page" data-testid="map-page">
      <div className="container">
        <div className="page-header">
          <div>
            <h1 className="page-header__title">Birding Locations Map</h1>
            <p className="page-header__subtitle">Explore every location in your field journal.</p>
          </div>
        </div>

        {loading && <LoadingSpinner label="Loading map…" />}
        {!loading && error && <ErrorBanner message={error} />}

        {!loading && !error && (
          <div className="map-container card" data-testid="map-container">
            <MapContainer center={DEFAULT_CENTER} zoom={DEFAULT_ZOOM} scrollWheelZoom style={{ height: '100%', width: '100%' }}>
              <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
              />
              {locations.map((location) => (
                <Marker
                  key={location.id}
                  position={[Number(location.latitude), Number(location.longitude)]}
                  icon={createDivIcon(location.icon)}
                >
                  <Popup>
                    <div data-testid={`map-popup-${location.id}`}>
                      <strong>{location.name}</strong>
                      <div>
                        <FlagIcon countryCode={location.countryCode} label={location.country} /> {location.country}
                      </div>
                      <HabitatBadge habitatType={location.habitatType} />
                      <div>
                        <Link to={`/locations/${location.id}`}>View details →</Link>
                      </div>
                    </div>
                  </Popup>
                </Marker>
              ))}
            </MapContainer>
          </div>
        )}
      </div>
    </div>
  );
}
