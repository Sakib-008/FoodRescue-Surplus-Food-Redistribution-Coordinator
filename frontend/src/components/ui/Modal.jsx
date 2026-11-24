import { useEffect, useRef } from 'react'
import { createPortal } from 'react-dom'
import { X } from 'lucide-react'
import { IconButton } from './Button.jsx'
import { cx } from './cx.js'

const FOCUSABLE = 'a[href],button:not([disabled]),input:not([disabled]),select:not([disabled]),textarea:not([disabled]),[tabindex]:not([tabindex="-1"])'

function useOverlay(open, onClose, panelRef) {
  useEffect(() => {
    if (!open) return undefined
    const previous = document.activeElement
    const onKey = (e) => {
      if (e.key === 'Escape') onClose()
      if (e.key === 'Tab' && panelRef.current) {            // keep focus inside the dialog
        const els = [...panelRef.current.querySelectorAll(FOCUSABLE)]
        if (!els.length) return
        const first = els[0], last = els[els.length - 1]
        if (e.shiftKey && document.activeElement === first) { e.preventDefault(); last.focus() }
        else if (!e.shiftKey && document.activeElement === last) { e.preventDefault(); first.focus() }
      }
    }
    document.addEventListener('keydown', onKey)
    document.body.style.overflow = 'hidden'
    panelRef.current?.querySelector(FOCUSABLE)?.focus()
    return () => {
      document.removeEventListener('keydown', onKey)
      document.body.style.overflow = ''
      previous?.focus?.()
    }
  }, [open, onClose, panelRef])
}

function Overlay({ open, onClose, title, description, footer, children, panelClass, align, labelId }) {
  const ref = useRef(null)
  useOverlay(open, onClose, ref)
  if (!open) return null
  return createPortal(
    <div className={cx('fixed inset-0 z-50 flex', align === 'right' ? 'justify-end' : 'items-center justify-center p-4')}>
      <div className="absolute inset-0 animate-fade-in bg-slate-900/50 backdrop-blur-[2px]" onClick={onClose} aria-hidden="true" />
      <div ref={ref} role="dialog" aria-modal="true" aria-labelledby={labelId} className={cx('relative flex max-h-full flex-col bg-surface shadow-pop', panelClass)}>
        <div className="flex items-start justify-between gap-4 border-b border-line px-6 py-4">
          <div>
            <h2 id={labelId} className="text-lg font-semibold text-ink">{title}</h2>
            {description && <p className="mt-0.5 text-sm text-muted">{description}</p>}
          </div>
          <IconButton label="Close" onClick={onClose}><X className="h-5 w-5" /></IconButton>
        </div>
        <div className="flex-1 overflow-y-auto px-6 py-5">{children}</div>
        {footer && <div className="flex justify-end gap-2 border-t border-line bg-subtle/40 px-6 py-3">{footer}</div>}
      </div>
    </div>,
    document.body,
  )
}

export function Modal({ size = 'md', ...props }) {
  const w = { sm: 'max-w-sm', md: 'max-w-lg', lg: 'max-w-2xl' }[size]
  return <Overlay labelId="modal-title" panelClass={cx('w-full animate-pop rounded-2xl', w)} {...props} />
}

/** Slide-over panel from the right edge (used for the donation form). */
export function Drawer({ width = 'max-w-2xl', ...props }) {
  return <Overlay align="right" labelId="drawer-title" panelClass={cx('h-full w-full animate-slide-in', width)} {...props} />
}
