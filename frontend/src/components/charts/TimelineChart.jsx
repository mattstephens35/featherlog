import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

export default function TimelineChart({ data }) {
  const chartData = data.map((m) => ({ month: m.yearMonth, count: m.sightingCount }));

  return (
    <div className="chart-container" data-testid="timeline-chart">
      <ResponsiveContainer width="100%" height={280}>
        <BarChart data={chartData} margin={{ top: 8, right: 8, left: 0, bottom: 8 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
          <XAxis dataKey="month" stroke="var(--color-text-muted)" tick={{ fill: 'var(--color-text-muted)' }} />
          <YAxis allowDecimals={false} stroke="var(--color-text-muted)" tick={{ fill: 'var(--color-text-muted)' }} />
          <Tooltip
            contentStyle={{
              background: 'var(--color-surface)',
              border: '1px solid var(--color-border)',
              borderRadius: 'var(--radius-md)',
              color: 'var(--color-text)',
            }}
          />
          <Bar dataKey="count" name="Sightings" fill="var(--color-primary)" radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
