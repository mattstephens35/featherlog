import { Link } from 'react-router-dom';
import ConservationBadge from './ConservationBadge';

export default function BirdCard({ bird }) {
  return (
    <Link to={`/birds/${bird.id}`} className="bird-card" data-testid={`bird-card-${bird.id}`}>
      <div className="bird-card__top">
        <div className="bird-card__icon" style={{ backgroundColor: `${bird.colorHex}22` }} aria-hidden="true">
          {bird.icon}
        </div>
        <ConservationBadge status={bird.conservationStatus} />
      </div>
      <div>
        <h3 className="bird-card__name">{bird.commonName}</h3>
        <p className="bird-card__scientific">{bird.scientificName}</p>
      </div>
      <div className="bird-card__meta">
        <span className="bird-card__family">{bird.family}</span>
      </div>
    </Link>
  );
}
