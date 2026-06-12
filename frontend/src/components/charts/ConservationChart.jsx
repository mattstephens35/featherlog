import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { CONSERVATION_STATUS_OPTIONS } from '../../data/options';

const STATUS_COLOR_VAR = {
  LEAST_CONCERN: '--status-lc-text',
  NEAR_THREATENED: '--status-nt-text',
  VULNERABLE: '--status-vu-text',
  ENDANGERED: '--status-en-text',
  CRITICALLY_ENDANGERED: '--status-cr-text',
};

export default function ConservationChart({ data }) {
  const chartData = data
    .filter((d) => d.speciesCount > 0)
    .map((d) => ({
      name: CONSERVATION_STATUS_OPTIONS.find((o) => o.value === d.status)?.label || d.status,
      value: d.speciesCount,
      status: d.status,
    }));

  return (
    <div className="chart-container" data-testid="conservation-chart">
      <ResponsiveContainer width="100%" height={280}>
        <PieChart>
          <Pie data={chartData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={90} label>
            {chartData.map((entry) => (
              <Cell key={entry.status} fill={`var(${STATUS_COLOR_VAR[entry.status]})`} />
            ))}
          </Pie>
          <Tooltip
            contentStyle={{
              background: 'var(--color-surface)',
              border: '1px solid var(--color-border)',
              borderRadius: 'var(--radius-md)',
              color: 'var(--color-text)',
            }}
          />
          <Legend wrapperStyle={{ color: 'var(--color-text)' }} />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
}
