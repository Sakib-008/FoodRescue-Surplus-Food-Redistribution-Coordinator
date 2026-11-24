import { cx } from './cx.js'

export function Card({ className, padded = true, interactive, ...props }) {
  return (
    <div className={cx('rounded-xl border border-line bg-surface shadow-card', padded && 'p-5',
      interactive && 'transition hover:-translate-y-0.5 hover:shadow-pop', className)} {...props} />
  )
}

export function SectionTitle({ title, description, actions, className }) {
  return (
    <div className={cx('mb-3 flex items-end justify-between gap-3', className)}>
      <div>
        <h2 className="text-base font-semibold text-ink">{title}</h2>
        {description && <p className="text-sm text-muted">{description}</p>}
      </div>
      {actions}
    </div>
  )
}
