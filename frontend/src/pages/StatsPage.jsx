import { useEffect, useState } from 'react';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorBanner from '../components/ErrorBanner';
import EmptyState from '../components/EmptyState';
import TimelineChart from '../components/charts/TimelineChart';
import ConservationChart from '../components/charts/ConservationChart';
import LiferList from '../components/LiferList';
import ActivityHeatmap from '../components/ActivityHeatmap';
import { fetchStatsTimeline, fetchConservationBreakdown, fetchLifers, fetchActivity } from '../api/stats';

export default function StatsPage() {
  const [timeline, setTimeline] = useState(null);
  const [conservation, setConservation] = useState(null);
  const [lifers, setLifers] = useState(null);
  const [activity, setActivity] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    Promise.all([fetchStatsTimeline(), fetchConservationBreakdown(), fetchLifers(), fetchActivity()])
      .then(([t, c, l, a]) => {
        if (cancelled) return;
        setTimeline(t);
        setConservation(c);
        setLifers(l);
        setActivity(a);
      })
      .catch(() => {
        if (!cancelled) setError('Unable to load statistics. Please try again later.');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <div className="page" data-testid="stats-page">
      <div className="container">
        <div className="page-header">
          <div>
            <h1 className="page-header__title">Statistics</h1>
            <p className="page-header__subtitle">Your birding activity at a glance.</p>
          </div>
        </div>

        {loading && <LoadingSpinner label="Loading statistics…" />}
        {!loading && error && <ErrorBanner message={error} />}

        {!loading && !error && (
          <div className="stats-grid">
            <div className="card stats-panel">
              <h2 className="section-title">Sightings Over Time</h2>
              {timeline.months.length === 0 ? (
                <EmptyState icon="📈" title="No sightings yet" />
              ) : (
                <TimelineChart data={timeline.months} />
              )}
            </div>

            <div className="card stats-panel">
              <h2 className="section-title">Conservation Status Breakdown</h2>
              <ConservationChart data={conservation.statuses} />
            </div>

            <div className="card stats-panel stats-panel--wide">
              <h2 className="section-title">Activity Calendar</h2>
              <ActivityHeatmap
                days={activity.days}
                longestStreak={activity.longestStreak}
                longestStreakStart={activity.longestStreakStart}
                longestStreakEnd={activity.longestStreakEnd}
              />
            </div>

            <div className="card stats-panel stats-panel--wide">
              <h2 className="section-title">Lifer Log</h2>
              <LiferList lifers={lifers.lifers} />
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
