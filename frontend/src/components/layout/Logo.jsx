import { Link } from 'react-router-dom'
import { cx } from '../ui'

export function LogoMark({ className }) {
  return (
    <svg viewBox="0 0 32 32" className={cx('h-8 w-8', className)} aria-hidden="true">
      <rect width="32" height="32" rx="9" className="fill-brand-600" />
      <path d="M9 21c0-6.4 4.2-10.5 14.5-10.5 0 8.3-4.2 12.5-10.4 12.5-2.1 0-3.4-.8-4.1-2z" fill="white" />
      <path d="M10 23c2.5-4.5 5.5-7 9-8.5" stroke="#059669" strokeWidth="1.6" strokeLinecap="round" fill="none" />
    </svg>
  )
}

export default function Logo({ to = '/', className, light }) {
  return (
    <Link to={to} className={cx('inline-flex items-center gap-2.5 font-bold tracking-tight', light ? 'text-white' : 'text-ink', className)}>
      <LogoMark /><span className="text-lg">FoodRescue</span>
    </Link>
  )
}
