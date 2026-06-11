import { Link } from 'react-router-dom';
import FlagIcon from './FlagIcon';
import HabitatBadge from './HabitatBadge';

export default function LocationCard({ location }) {
  return (
    <Link to={`/locations/${location.id}`} className="location-card" data-testid={`location-card-${location.id}`}>
      <div className="location-card__top">
        <div className="location-card__flag">
          <FlagIcon countryCode={location.countryCode} label={location.country} />
        </div>
        <div>
          <h3 className="location-card__name">{location.name}</h3>
          <p className="location-card__place">
            {location.region ? `${location.region}, ` : ''}
            {location.country}
          </p>
        </div>
      </div>
      <div className="location-card__meta">
        <HabitatBadge habitatType={location.habitatType} />
        {location.bestSeason && <span className="location-card__season">Best: {location.bestSeason}</span>}
      </div>
    </Link>
  );
}
