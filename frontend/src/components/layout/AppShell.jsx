import { useEffect, useState } from 'react'
import { NavLink, Outlet, useLocation } from 'react-router-dom'
import { Menu, X } from 'lucide-react'
import { IconButton, cx } from '../ui'
import { useAuth } from '../../lib/auth.jsx'
import Logo from './Logo.jsx'
import NotificationBell from './NotificationBell.jsx'
import ThemeToggle from './ThemeToggle.jsx'
import UserMenu from './UserMenu.jsx'

function NavItems({ items, onNavigate }) {
  return (
    <nav aria-label="Main" className="space-y-1">
      {items.map(({ path, label, icon: Icon }) => (
        <NavLink key={path} to={path} onClick={onNavigate}
          className={({ isActive }) => cx('group flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors',
            isActive ? 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-200' : 'text-muted hover:bg-subtle hover:text-ink')}>
          {Icon && <Icon className="h-[18px] w-[18px] shrink-0" aria-hidden="true" />}
          {label}
        </NavLink>
      ))}
    </nav>
  )
}

/** Authenticated layout: fixed sidebar on desktop, slide-in drawer on mobile, sticky top bar. */
export default function AppShell({ navItems }) {
  const { user } = useAuth()
  const [open, setOpen] = useState(false)
  const { pathname } = useLocation()
  useEffect(() => { setOpen(false) }, [pathname])

  const items = navItems.filter((n) => !n.roles || n.roles.includes(user.role))

  return (
    <div className="min-h-screen">
      <a href="#main" className="sr-only focus:not-sr-only focus:absolute focus:left-4 focus:top-4 focus:z-50 focus:rounded-lg focus:bg-brand-600 focus:px-4 focus:py-2 focus:text-white">Skip to content</a>

      {/* desktop sidebar */}
      <aside className="fixed inset-y-0 left-0 z-30 hidden w-64 flex-col border-r border-line bg-surface lg:flex">
        <div className="flex h-16 items-center px-5"><Logo to="/" /></div>
        <div className="flex-1 overflow-y-auto px-3 py-4">
          <p className="mb-2 px-3 text-[11px] font-semibold uppercase tracking-wider text-muted">Menu</p>
          <NavItems items={items} />
        </div>
        <p className="border-t border-line px-5 py-4 text-xs text-muted">Reducing food waste, one meal at a time.</p>
      </aside>

      {/* mobile drawer */}
      {open && (
        <div className="fixed inset-0 z-40 lg:hidden">
          <div className="absolute inset-0 animate-fade-in bg-slate-900/50" onClick={() => setOpen(false)} aria-hidden="true" />
          <aside className="relative flex h-full w-72 max-w-[85vw] animate-slide-left flex-col bg-surface shadow-pop">
            <div className="flex h-16 items-center justify-between px-5"><Logo /><IconButton label="Close menu" onClick={() => setOpen(false)}><X className="h-5 w-5" /></IconButton></div>
            <div className="flex-1 overflow-y-auto px-3 py-2"><NavItems items={items} onNavigate={() => setOpen(false)} /></div>
          </aside>
        </div>
      )}

      <div className="lg:pl-64">
        <header className="sticky top-0 z-20 flex h-16 items-center gap-2 border-b border-line bg-surface/80 px-4 backdrop-blur sm:px-6">
          <IconButton label="Open menu" className="lg:hidden" onClick={() => setOpen(true)}><Menu className="h-5 w-5" /></IconButton>
          <div className="lg:hidden"><Logo /></div>
          <div className="ml-auto flex items-center gap-1 sm:gap-2">
            <ThemeToggle />
            <NotificationBell />
            <span className="mx-1 hidden h-6 w-px bg-line sm:block" aria-hidden="true" />
            <UserMenu />
          </div>
        </header>
        <main id="main" className="mx-auto w-full max-w-7xl px-4 py-8 sm:px-6 lg:px-8"><Outlet /></main>
      </div>
    </div>
  )
}
