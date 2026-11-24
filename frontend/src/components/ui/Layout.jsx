import { ChevronLeft, ChevronRight, Check } from 'lucide-react'
import { Button } from './Button.jsx'
import { cx } from './cx.js'
import { Card } from './Card.jsx'

export function PageHeader({ title, description, actions, className }) {
  return (
    <div className={cx('mb-6 flex flex-wrap items-end justify-between gap-4', className)}>
      <div className="min-w-0">
        <h1 className="text-2xl font-bold tracking-tight text-ink sm:text-3xl">{title}</h1>
        {description && <p className="mt-1 max-w-2xl text-sm text-muted sm:text-base">{description}</p>}
      </div>
      {actions && <div className="flex flex-wrap items-center gap-2">{actions}</div>}
    </div>
  )
}

export function Tabs({ tabs, value, onChange, className }) {
  return (
    <div role="tablist" className={cx('mb-5 flex gap-1 overflow-x-auto border-b border-line', className)}>
      {tabs.map((t) => (
        <button key={t.value} role="tab" aria-selected={value === t.value} onClick={() => onChange(t.value)}
          className={cx('-mb-px flex items-center gap-2 whitespace-nowrap border-b-2 px-4 py-2.5 text-sm font-medium transition-colors',
            value === t.value ? 'border-brand-600 text-brand-700 dark:text-brand-300' : 'border-transparent text-muted hover:text-ink')}>
          {t.label}
          {t.count !== undefined && <span className={cx('rounded-full px-2 py-0.5 text-xs', value === t.value ? 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-300' : 'bg-subtle text-muted')}>{t.count}</span>}
        </button>
      ))}
    </div>
  )
}

const tones = {
  brand: 'bg-brand-50 text-brand-600 dark:bg-brand-500/15 dark:text-brand-300',
  amber: 'bg-amber-50 text-amber-600 dark:bg-amber-500/15 dark:text-amber-300',
  sky: 'bg-sky-50 text-sky-600 dark:bg-sky-500/15 dark:text-sky-300',
  red: 'bg-red-50 text-red-600 dark:bg-red-500/15 dark:text-red-300',
  violet: 'bg-violet-50 text-violet-600 dark:bg-violet-500/15 dark:text-violet-300',
}

export function StatCard({ icon: Icon, label, value, hint, tone = 'brand' }) {
  return (
    <Card className="flex items-start gap-4">
      {Icon && <span className={cx('flex h-11 w-11 shrink-0 items-center justify-center rounded-xl', tones[tone])}><Icon className="h-5 w-5" aria-hidden="true" /></span>}
      <div className="min-w-0">
        <p className="text-sm text-muted">{label}</p>
        <p className="mt-0.5 truncate text-2xl font-bold tracking-tight text-ink">{value}</p>
        {hint && <p className="mt-0.5 text-xs text-muted">{hint}</p>}
      </div>
    </Card>
  )
}

export function Pagination({ page, totalPages, onChange }) {
  if (!totalPages || totalPages <= 1) return null
  return (
    <nav aria-label="Pagination" className="mt-6 flex items-center justify-center gap-3 text-sm">
      <Button variant="secondary" size="sm" disabled={page === 0} onClick={() => onChange(page - 1)}><ChevronLeft className="h-4 w-4" />Previous</Button>
      <span className="text-muted">Page <b className="text-ink">{page + 1}</b> of {totalPages}</span>
      <Button variant="secondary" size="sm" disabled={page + 1 >= totalPages} onClick={() => onChange(page + 1)}>Next<ChevronRight className="h-4 w-4" /></Button>
    </nav>
  )
}

export function ProgressBar({ value, max, className, label }) {
  const pct = max > 0 ? Math.max(0, Math.min(100, (value / max) * 100)) : 0
  return (
    <div className={className}>
      <div className="h-2 overflow-hidden rounded-full bg-subtle" role="progressbar" aria-valuemin={0} aria-valuemax={max} aria-valuenow={value} aria-label={label}>
        <div className="h-full rounded-full bg-brand-500 transition-all" style={{ width: `${pct}%` }} />
      </div>
    </div>
  )
}

export function Avatar({ name, size = 'md', className }) {
  const letters = (name || '?').split(/\s+/).filter(Boolean).slice(0, 2).map((s) => s[0].toUpperCase()).join('')
  const sizes = { sm: 'h-8 w-8 text-xs', md: 'h-9 w-9 text-sm', lg: 'h-14 w-14 text-lg' }
  return <span aria-hidden="true" className={cx('inline-flex shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-brand-400 to-brand-700 font-semibold text-white', sizes[size], className)}>{letters}</span>
}

/** Horizontal step tracker, e.g. Requested → Accepted → Collected → Delivered. `current` is the index of the active step. */
export function Stepper({ steps, current, failed }) {
  return (
    <ol className="flex items-center" aria-label="Progress">
      {steps.map((s, i) => {
        const done = i < current || (i === current && current === steps.length - 1 && !failed)
        const active = i === current && !done
        return (
          <li key={s} className={cx('flex items-center', i < steps.length - 1 && 'flex-1')}>
            <span className="flex flex-col items-center gap-1">
              <span className={cx('flex h-6 w-6 items-center justify-center rounded-full text-[11px] font-semibold ring-4 ring-surface',
                done ? 'bg-brand-600 text-white' : active ? (failed ? 'bg-red-500 text-white' : 'bg-brand-100 text-brand-700 ring-brand-50 dark:bg-brand-500/25 dark:text-brand-200') : 'bg-subtle text-muted')}>
                {done ? <Check className="h-3.5 w-3.5" aria-hidden="true" /> : i + 1}
              </span>
              <span className={cx('hidden whitespace-nowrap text-[11px] sm:block', done || active ? 'font-medium text-ink' : 'text-muted')}>{s}</span>
            </span>
            {i < steps.length - 1 && <span className={cx('mx-1 h-0.5 flex-1 rounded', i < current ? 'bg-brand-500' : 'bg-line')} />}
          </li>
        )
      })}
    </ol>
  )
}

export function DataTable({ columns, rows, rowKey = (r) => r.id, empty }) {
  return (
    <div className="overflow-x-auto rounded-xl border border-line bg-surface shadow-card">
      <table className="w-full min-w-[640px] text-left text-sm">
        <thead className="border-b border-line bg-subtle/60 text-xs uppercase tracking-wide text-muted">
          <tr>{columns.map((c) => <th key={c.key} scope="col" className={cx('px-4 py-3 font-medium', c.className)}>{c.header}</th>)}</tr>
        </thead>
        <tbody className="divide-y divide-line">
          {rows.length === 0 && <tr><td colSpan={columns.length} className="px-4 py-10 text-center text-muted">{empty || 'Nothing to show'}</td></tr>}
          {rows.map((r) => (
            <tr key={rowKey(r)} className="transition-colors hover:bg-subtle/50">
              {columns.map((c) => <td key={c.key} className={cx('px-4 py-3 align-middle', c.className)}>{c.render ? c.render(r) : r[c.key]}</td>)}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
