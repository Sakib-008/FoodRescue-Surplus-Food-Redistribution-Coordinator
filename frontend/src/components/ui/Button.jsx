import { Loader2 } from 'lucide-react'
import { cx } from './cx.js'

const base = 'inline-flex items-center justify-center gap-2 rounded-lg font-medium whitespace-nowrap transition-colors focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-600 disabled:cursor-not-allowed disabled:opacity-50'
const variants = {
  primary: 'bg-brand-600 text-white shadow-sm hover:bg-brand-700 active:bg-brand-800',
  secondary: 'border border-line bg-surface text-ink shadow-sm hover:bg-subtle',
  soft: 'bg-brand-50 text-brand-700 hover:bg-brand-100 dark:bg-brand-500/15 dark:text-brand-300 dark:hover:bg-brand-500/25',
  ghost: 'text-muted hover:bg-subtle hover:text-ink',
  danger: 'bg-red-600 text-white shadow-sm hover:bg-red-700',
  dangerSoft: 'bg-red-50 text-red-700 hover:bg-red-100 dark:bg-red-500/15 dark:text-red-300 dark:hover:bg-red-500/25',
}
const sizes = { sm: 'px-2.5 py-1.5 text-xs', md: 'px-3.5 py-2 text-sm', lg: 'px-5 py-2.5 text-base' }

/** Class names, so <Link> / <a> can look exactly like a button. */
export const buttonStyles = ({ variant = 'primary', size = 'md', className } = {}) => cx(base, variants[variant], sizes[size], className)

export function Button({ variant, size, loading, disabled, className, children, type = 'button', ...props }) {
  return (
    <button type={type} className={buttonStyles({ variant, size, className })} disabled={disabled || loading} aria-busy={loading || undefined} {...props}>
      {loading && <Loader2 className="h-4 w-4 animate-spin" aria-hidden="true" />}
      {children}
    </button>
  )
}

export function IconButton({ label, className, children, ...props }) {
  return (
    <button type="button" aria-label={label} title={label}
      className={cx('inline-flex h-9 w-9 items-center justify-center rounded-lg text-muted transition-colors hover:bg-subtle hover:text-ink focus-visible:outline-2 focus-visible:outline-brand-600', className)} {...props}>
      {children}
    </button>
  )
}
