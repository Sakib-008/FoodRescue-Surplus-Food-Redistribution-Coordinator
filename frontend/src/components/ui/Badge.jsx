import { cx } from './cx.js'

const tones = {
  neutral: 'bg-slate-100 text-slate-700 dark:bg-slate-500/20 dark:text-slate-300',
  success: 'bg-emerald-50 text-emerald-700 dark:bg-emerald-500/15 dark:text-emerald-300',
  warning: 'bg-amber-50 text-amber-800 dark:bg-amber-500/15 dark:text-amber-300',
  danger: 'bg-red-50 text-red-700 dark:bg-red-500/15 dark:text-red-300',
  info: 'bg-sky-50 text-sky-700 dark:bg-sky-500/15 dark:text-sky-300',
  violet: 'bg-violet-50 text-violet-700 dark:bg-violet-500/15 dark:text-violet-300',
}
const dots = { neutral: 'bg-slate-400', success: 'bg-emerald-500', warning: 'bg-amber-500', danger: 'bg-red-500', info: 'bg-sky-500', violet: 'bg-violet-500' }

export function Badge({ tone = 'neutral', dot, className, children }) {
  return (
    <span className={cx('inline-flex items-center gap-1.5 rounded-full px-2.5 py-0.5 text-xs font-medium', tones[tone], className)}>
      {dot && <span className={cx('h-1.5 w-1.5 rounded-full', dots[tone])} aria-hidden="true" />}
      {children}
    </span>
  )
}

const STATUS = {
  AVAILABLE: ['success', 'Available'], RESERVED: ['warning', 'Fully reserved'], COMPLETED: ['info', 'Completed'],
  EXPIRED: ['neutral', 'Expired'], CANCELLED: ['neutral', 'Cancelled'],
  PENDING: ['warning', 'Pending'], ACCEPTED: ['success', 'Accepted'], REJECTED: ['danger', 'Rejected'],
  OPEN: ['warning', 'Open'], ASSIGNED: ['violet', 'Assigned'], PICKED_UP: ['info', 'Picked up'], DELIVERED: ['success', 'Delivered'],
}

/** One badge for every status enum in the system (donation, request, pickup). */
export function StatusBadge({ status }) {
  const [tone, label] = STATUS[status] || ['neutral', status]
  return <Badge tone={tone} dot>{label}</Badge>
}
