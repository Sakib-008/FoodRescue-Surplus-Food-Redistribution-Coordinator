import { AlertCircle, CheckCircle2, Loader2 } from 'lucide-react'
import { cx } from './cx.js'

export function Spinner({ className }) {
  return <Loader2 className={cx('h-5 w-5 animate-spin text-brand-600', className)} aria-label="Loading" />
}

export function Skeleton({ className }) {
  return <div className={cx('animate-pulse rounded-lg bg-subtle', className)} aria-hidden="true" />
}

export function CardSkeletons({ count = 6 }) {
  return (
    <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3" role="status" aria-label="Loading">
      {Array.from({ length: count }, (_, i) => (
        <div key={i} className="overflow-hidden rounded-xl border border-line bg-surface">
          <Skeleton className="h-40 rounded-none" />
          <div className="space-y-3 p-5"><Skeleton className="h-4 w-2/3" /><Skeleton className="h-3 w-1/2" /><Skeleton className="h-3 w-full" /></div>
        </div>
      ))}
    </div>
  )
}

export function ListSkeleton({ rows = 4 }) {
  return (
    <div className="space-y-3" role="status" aria-label="Loading">
      {Array.from({ length: rows }, (_, i) => <Skeleton key={i} className="h-20 w-full" />)}
    </div>
  )
}

export function EmptyState({ icon: Icon, title, description, action }) {
  return (
    <div className="flex flex-col items-center rounded-xl border border-dashed border-line bg-surface/60 px-6 py-14 text-center">
      {Icon && <span className="mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-brand-50 text-brand-600 dark:bg-brand-500/15 dark:text-brand-300"><Icon className="h-6 w-6" aria-hidden="true" /></span>}
      <h3 className="text-base font-semibold text-ink">{title}</h3>
      {description && <p className="mt-1 max-w-sm text-sm text-muted">{description}</p>}
      {action && <div className="mt-5">{action}</div>}
    </div>
  )
}

export function Alert({ kind = 'error', title, children, className }) {
  if (!children && !title) return null
  const ok = kind === 'success'
  const Icon = ok ? CheckCircle2 : AlertCircle
  return (
    <div role={ok ? 'status' : 'alert'} className={cx('flex gap-3 rounded-lg border p-3 text-sm',
      ok ? 'border-emerald-200 bg-emerald-50 text-emerald-800 dark:border-emerald-500/30 dark:bg-emerald-500/10 dark:text-emerald-200'
        : 'border-red-200 bg-red-50 text-red-800 dark:border-red-500/30 dark:bg-red-500/10 dark:text-red-200', className)}>
      <Icon className="mt-0.5 h-4 w-4 shrink-0" aria-hidden="true" />
      <div>{title && <p className="font-medium">{title}</p>}{children}</div>
    </div>
  )
}
