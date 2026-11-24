import { Ban, Bell, CheckCircle2, Clock, Inbox, PackageCheck, Truck, XCircle } from 'lucide-react'
import { cx } from '../ui'

const META = {
  REQUEST_SUBMITTED: [Inbox, 'bg-sky-50 text-sky-600 dark:bg-sky-500/15 dark:text-sky-300'],
  REQUEST_ACCEPTED: [CheckCircle2, 'bg-emerald-50 text-emerald-600 dark:bg-emerald-500/15 dark:text-emerald-300'],
  REQUEST_REJECTED: [XCircle, 'bg-red-50 text-red-600 dark:bg-red-500/15 dark:text-red-300'],
  REQUEST_CANCELLED: [Ban, 'bg-slate-100 text-slate-600 dark:bg-slate-500/20 dark:text-slate-300'],
  PICKUP_ASSIGNED: [Truck, 'bg-violet-50 text-violet-600 dark:bg-violet-500/15 dark:text-violet-300'],
  PICKUP_DELIVERED: [PackageCheck, 'bg-emerald-50 text-emerald-600 dark:bg-emerald-500/15 dark:text-emerald-300'],
  DONATION_EXPIRING: [Clock, 'bg-amber-50 text-amber-600 dark:bg-amber-500/15 dark:text-amber-300'],
}

/** Round icon for a notification type - shared by the bell dropdown and the notifications page. */
export default function NotificationIcon({ type, className }) {
  const [Icon, tone] = META[type] || [Bell, 'bg-subtle text-muted']
  return (
    <span className={cx('flex h-9 w-9 shrink-0 items-center justify-center rounded-full', tone, className)}>
      <Icon className="h-[18px] w-[18px]" aria-hidden="true" />
    </span>
  )
}
