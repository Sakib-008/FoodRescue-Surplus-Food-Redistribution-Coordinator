import { Link, Outlet } from 'react-router-dom'
import { buttonStyles } from '../ui'
import { useAuth, homeFor } from '../../lib/auth.jsx'
import Logo from './Logo.jsx'
import ThemeToggle from './ThemeToggle.jsx'

/** Marketing layout for the landing page. */
export default function PublicLayout() {
  const { user } = useAuth()
  return (
    <div className="flex min-h-screen flex-col">
      <header className="sticky top-0 z-30 border-b border-line/70 bg-canvas/80 backdrop-blur">
        <div className="mx-auto flex h-16 max-w-6xl items-center gap-6 px-4 sm:px-6">
          <Logo />
          <nav className="ml-4 hidden items-center gap-6 text-sm font-medium text-muted md:flex" aria-label="Sections">
            <a href="#how" className="hover:text-ink">How it works</a>
            <a href="#roles" className="hover:text-ink">Who it's for</a>
            <a href="#trust" className="hover:text-ink">Safety &amp; trust</a>
          </nav>
          <div className="ml-auto flex items-center gap-2">
            <ThemeToggle />
            {user
              ? <Link to={homeFor(user.role)} className={buttonStyles({})}>Open dashboard</Link>
              : <>
                <Link to="/login" className={buttonStyles({ variant: 'ghost', className: 'hidden sm:inline-flex' })}>Sign in</Link>
                <Link to="/register" className={buttonStyles({})}>Get started</Link>
              </>}
          </div>
        </div>
      </header>
      <main className="flex-1"><Outlet /></main>
      <footer className="border-t border-line bg-surface">
        <div className="mx-auto flex max-w-6xl flex-col items-center justify-between gap-4 px-4 py-8 text-sm text-muted sm:flex-row sm:px-6">
          <Logo />
          <p>Built as a Software Engineering &amp; Project Management lab project.</p>
        </div>
      </footer>
    </div>
  )
}
