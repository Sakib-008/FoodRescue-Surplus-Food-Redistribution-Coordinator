import { Link } from 'react-router-dom'
import { SearchX } from 'lucide-react'
import { buttonStyles } from '../ui'

export default function NotFound() {
  return (
    <div className="mx-auto flex min-h-[60vh] max-w-md flex-col items-center justify-center px-4 text-center">
      <span className="mb-4 flex h-14 w-14 items-center justify-center rounded-full bg-subtle text-muted"><SearchX className="h-7 w-7" aria-hidden="true" /></span>
      <h1 className="text-2xl font-bold text-ink">Page not found</h1>
      <p className="mt-2 text-muted">The page you are looking for does not exist or has moved.</p>
      <Link to="/" className={buttonStyles({ className: 'mt-6' })}>Back to home</Link>
    </div>
  )
}
