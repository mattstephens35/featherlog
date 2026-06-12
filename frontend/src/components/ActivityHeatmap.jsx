import { useMemo } from 'react';
import { formatDate } from '../utils/format';

function levelForCount(count) {
  if (count === 0) return 0;
  if (count === 1) return 1;
  if (count <= 3) return 2;
  return 3;
}

export default function ActivityHeatmap({ days, longestStreak, longestStreakStart, longestStreakEnd }) {
  const { cells, weekCount } = useMemo(() => {
    if (days.length === 0) return { cells: [], weekCount: 0 };

    const countByDate = new Map(days.map((d) => [d.date, d.sightingCount]));
    const allDates = days.map((d) => new Date(`${d.date}T00:00:00`));
    const minDate = new Date(Math.min(...allDates));
    const maxDate = new Date(Math.max(...allDates));

    const start = new Date(minDate);
    start.setDate(start.getDate() - start.getDay());

    const result = [];
    let cursor = new Date(start);
    let weekIndex = 0;

    while (cursor <= maxDate) {
      const iso = cursor.toISOString().slice(0, 10);
      const count = countByDate.get(iso) || 0;
      result.push({ date: iso, count, level: levelForCount(count), dayOfWeek: cursor.getDay(), weekIndex });
      if (cursor.getDay() === 6) weekIndex++;
      cursor.setDate(cursor.getDate() + 1);
    }

    return { cells: result, weekCount: weekIndex + 1 };
  }, [days]);

  if (days.length === 0) {
    return <p className="text-muted">No activity recorded yet.</p>;
  }

  return (
    <div data-testid="activity-heatmap">
      {longestStreak > 0 && (
        <p className="activity-heatmap__streak" data-testid="longest-streak">
          🔥 Longest streak: <strong>{longestStreak} day{longestStreak === 1 ? '' : 's'}</strong>
          {longestStreakStart && longestStreakEnd && (
            <> ({formatDate(longestStreakStart)} – {formatDate(longestStreakEnd)})</>
          )}
        </p>
      )}
      <div className="activity-heatmap__grid" style={{ gridTemplateColumns: `repeat(${weekCount}, 1fr)` }}>
        {cells.map((cell) => (
          <div
            key={cell.date}
            className={`activity-heatmap__cell activity-heatmap__cell--level-${cell.level}`}
            style={{ gridColumn: cell.weekIndex + 1, gridRow: cell.dayOfWeek + 1 }}
            title={`${cell.date}: ${cell.count} sighting${cell.count === 1 ? '' : 's'}`}
            data-testid={`activity-cell-${cell.date}`}
          />
        ))}
      </div>
    </div>
  );
}
