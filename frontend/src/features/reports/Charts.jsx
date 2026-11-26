import { cx } from '../../components/ui'

/** Dependency-free SVG donut. data: [{ label, value, color }] (color = any CSS colour). */
export function DonutChart({ data, size = 148, thickness = 20, centerLabel, centerValue }) {
  const total = data.reduce((s, d) => s + d.value, 0)
  const r = (size - thickness) / 2
  const c = 2 * Math.PI * r
  let offset = 0
  return (
    <div className="flex flex-wrap items-center gap-6">
      <div className="relative" style={{ width: size, height: size }}>
        <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`} role="img" aria-label={centerLabel || 'Chart'}>
          <circle cx={size / 2} cy={size / 2} r={r} fill="none" stroke="var(--c-subtle)" strokeWidth={thickness} />
          {total > 0 && data.filter((d) => d.value > 0).map((d) => {
            const len = (d.value / total) * c
            const el = (
              <circle key={d.label} cx={size / 2} cy={size / 2} r={r} fill="none" stroke={d.color} strokeWidth={thickness}
                strokeDasharray={`${len} ${c - len}`} strokeDashoffset={-offset} transform={`rotate(-90 ${size / 2} ${size / 2})`} />
            )
            offset += len
            return el
          })}
        </svg>
        <div className="absolute inset-0 flex flex-col items-center justify-center">
          <span className="text-2xl font-bold text-ink">{centerValue ?? total}</span>
          {centerLabel && <span className="text-xs text-muted">{centerLabel}</span>}
        </div>
      </div>
      <ul className="space-y-1.5 text-sm">
        {data.map((d) => (
          <li key={d.label} className="flex items-center gap-2">
            <span className="h-2.5 w-2.5 rounded-full" style={{ background: d.color }} aria-hidden="true" />
            <span className="text-muted">{d.label}</span><span className="ml-auto pl-4 font-medium text-ink">{d.value}</span>
          </li>
        ))}
      </ul>
    </div>
  )
}

/** Horizontal bars. data: [{ label, value }] */
export function BarList({ data, color = 'bg-brand-500', className }) {
  const max = Math.max(1, ...data.map((d) => d.value))
  return (
    <ul className={cx('space-y-3', className)}>
      {data.map((d) => (
        <li key={d.label}>
          <div className="mb-1 flex justify-between text-sm"><span className="text-muted">{d.label}</span><span className="font-medium text-ink">{d.value}</span></div>
          <div className="h-2 overflow-hidden rounded-full bg-subtle"><div className={cx('h-full rounded-full', color)} style={{ width: `${(d.value / max) * 100}%` }} /></div>
        </li>
      ))}
    </ul>
  )
}
