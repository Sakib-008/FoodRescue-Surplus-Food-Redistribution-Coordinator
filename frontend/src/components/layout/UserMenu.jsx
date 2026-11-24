import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { ChevronDown, LogOut, UserCog } from 'lucide-react'
import { Avatar } from '../ui'
import { useAuth } from '../../lib/auth.jsx'

const ROLE_LABEL = { ADMIN: 'Administrator', DONOR: 'Donor', RECIPIENT: 'Recipient', VOLUNTEER: 'Volunteer' }

export default function UserMenu() {
  const { user, logout } = useAuth()
  const [open, setOpen] = useState(false)
  const box = useRef(null)
  useEffect(() => {
    if (!open) return undefined
    const onDown = (e) => { if (box.current && !box.current.contains(e.target)) setOpen(false) }
    const onKey = (e) => { if (e.key === 'Escape') setOpen(false) }
    document.addEventListener('mousedown', onDown); document.addEventListener('keydown', onKey)
    return () => { document.removeEventListener('mousedown', onDown); document.removeEventListener('keydown', onKey) }
  }, [open])
  if (!user) return null
  return (
    <div className="relative" ref={box}>
      <button type="button" onClick={() => setOpen((o) => !o)} aria-expanded={open} aria-haspopup="menu"
        className="flex items-center gap-2 rounded-lg p-1 pr-2 transition-colors hover:bg-subtle focus-visible:outline-2 focus-visible:outline-brand-600">
        <Avatar name={user.fullName} size="sm" />
        <span className="hidden text-left leading-tight sm:block">
          <span className="block max-w-[10rem] truncate text-sm font-medium text-ink">{user.fullName}</span>
          <span className="block text-xs text-muted">{ROLE_LABEL[user.role]}</span>
        </span>
        <ChevronDown className="h-4 w-4 text-muted" aria-hidden="true" />
      </button>
      {open && (
        <div role="menu" className="absolute right-0 z-40 mt-2 w-56 animate-pop overflow-hidden rounded-xl border border-line bg-surface py-1 shadow-pop">
          <div className="border-b border-line px-4 py-2.5"><p className="truncate text-sm font-medium text-ink">{user.fullName}</p><p className="truncate text-xs text-muted">{user.email}</p></div>
          <Link role="menuitem" to="/profile" onClick={() => setOpen(false)} className="flex items-center gap-2 px-4 py-2 text-sm text-ink hover:bg-subtle"><UserCog className="h-4 w-4 text-muted" />Profile &amp; settings</Link>
          <button role="menuitem" type="button" onClick={logout} className="flex w-full items-center gap-2 px-4 py-2 text-sm text-red-600 hover:bg-subtle dark:text-red-400"><LogOut className="h-4 w-4" />Log out</button>
        </div>
      )}
    </div>
  )
}
