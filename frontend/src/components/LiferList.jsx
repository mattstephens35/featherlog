import { Link } from 'react-router-dom';
import EmptyState from './EmptyState';
import FlagIcon from './FlagIcon';
import { formatDate } from '../utils/format';

export default function LiferList({ lifers }) {
  if (!lifers || lifers.length === 0) {
    return <EmptyState icon="🏅" title="No lifers yet" description="Log a sighting to start your lifer list." />;
  }

  return (
    <ul className="recent-list" data-testid="lifer-list">
      {lifers.map((lifer) => (
        <li key={lifer.bird.id} className="recent-list__item" data-testid={`lifer-${lifer.bird.id}`}>
          <div
            className="recent-list__icon"
            style={{ backgroundColor: `${lifer.bird.colorHex}22` }}
            aria-hidden="true"
          >
            {lifer.bird.icon}
          </div>
          <div className="recent-list__text">
            <Link to={`/birds/${lifer.bird.id}`}>
              <strong>{lifer.bird.commonName}</strong>
            </Link>
            <span>
              <FlagIcon countryCode={lifer.firstSeenLocation.countryCode} label={lifer.firstSeenLocation.country} />{' '}
              {lifer.firstSeenLocation.name}
            </span>
          </div>
          <div className="recent-list__meta">{formatDate(lifer.firstSeenDate)}</div>
        </li>
      ))}
    </ul>
  );
}
