import { useCallback, useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { Bell } from 'lucide-react'
import { IconButton, cx } from '../ui'
import { api } from '../../lib/api.js'
import { fmtRelative } from '../../lib/format.js'
import NotificationIcon from './NotificationIcon.jsx'

/** Bell with unread badge (polled every 30 s) and a dropdown of the latest notifications. */
export default function NotificationBell() {
  const [unread, setUnread] = useState(0)
  const [open, setOpen] = useState(false)
  const [items, setItems] = useState(null)
  const box = useRef(null)

  const loadCount = useCallback(() => api('/api/notifications/unread-count').then((d) => setUnread(d.count)).catch(() => {}), [])

  useEffect(() => {
    loadCount()
    const id = setInterval(loadCount, 30000)
    return () => clearInterval(id)
  }, [loadCount])

  useEffect(() => {
    if (!open) return undefined
    api('/api/notifications', { params: { size: 6 } }).then((d) => setItems(d.content)).catch(() => setItems([]))
    const onDown = (e) => { if (box.current && !box.current.contains(e.target)) setOpen(false) }
    const onKey = (e) => { if (e.key === 'Escape') setOpen(false) }
    document.addEventListener('mousedown', onDown)
    document.addEventListener('keydown', onKey)
    return () => { document.removeEventListener('mousedown', onDown); document.removeEventListener('keydown', onKey) }
  }, [open])

  const markAll = async () => {
    await api('/api/notifications/read-all', { method: 'PATCH' }).catch(() => {})
    setUnread(0)
    setItems((list) => list?.map((n) => ({ ...n, read: true })))
  }

  return (
    <div className="relative" ref={box}>
      <IconButton label={unread ? `Notifications (${unread} unread)` : 'Notifications'} onClick={() => setOpen((o) => !o)} aria-expanded={open}>
        <Bell className="h-[18px] w-[18px]" />
        {unread > 0 && (
          <span className="absolute right-1 top-1 flex h-4 min-w-4 items-center justify-center rounded-full bg-red-500 px-1 text-[10px] font-bold text-white ring-2 ring-surface">
            {unread > 9 ? '9+' : unread}
          </span>
        )}
      </IconButton>
      {open && (
        <div className="absolute right-0 z-40 mt-2 w-[22rem] max-w-[calc(100vw-2rem)] animate-pop overflow-hidden rounded-xl border border-line bg-surface shadow-pop">
          <div className="flex items-center justify-between border-b border-line px-4 py-3">
            <h2 className="text-sm font-semibold text-ink">Notifications</h2>
            {unread > 0 && <button type="button" onClick={markAll} className="text-xs font-medium text-brand-700 hover:underline dark:text-brand-300">Mark all read</button>}
          </div>
          <ul className="max-h-96 divide-y divide-line overflow-y-auto">
            {items === null && <li className="px-4 py-8 text-center text-sm text-muted">Loading…</li>}
            {items?.length === 0 && <li className="px-4 py-8 text-center text-sm text-muted">You're all caught up.</li>}
            {items?.map((n) => (
              <li key={n.id} className={cx('flex gap-3 px-4 py-3', !n.read && 'bg-brand-50/60 dark:bg-brand-500/5')}>
                <NotificationIcon type={n.type} />
                <div className="min-w-0 text-sm">
                  <p className="text-ink">{n.message}</p>
                  <p className="mt-0.5 text-xs text-muted">{fmtRelative(n.createdAt)}</p>
                </div>
              </li>
            ))}
          </ul>
          <Link to="/notifications" onClick={() => setOpen(false)} className="block border-t border-line px-4 py-2.5 text-center text-sm font-medium text-brand-700 hover:bg-subtle dark:text-brand-300">View all notifications</Link>
        </div>
      )}
    </div>
  )
}
