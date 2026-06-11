export default function SightingListItem({ icon, iconBg, title, subtitle, meta, testId }) {
  return (
    <li className="recent-list__item" data-testid={testId}>
      <div className="recent-list__icon" style={iconBg ? { backgroundColor: iconBg } : undefined} aria-hidden="true">
        {icon}
      </div>
      <div className="recent-list__text">
        <strong>{title}</strong>
        <span>{subtitle}</span>
      </div>
      <div className="recent-list__meta">{meta}</div>
    </li>
  );
}
