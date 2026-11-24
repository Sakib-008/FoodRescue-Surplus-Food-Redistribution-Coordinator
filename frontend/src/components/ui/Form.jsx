import { createContext, useContext, useId } from 'react'
import { Search } from 'lucide-react'
import { cx } from './cx.js'

const control = 'w-full rounded-lg border border-line bg-surface px-3 py-2 text-sm text-ink placeholder:text-muted/70 shadow-sm outline-none transition focus:border-brand-500 focus:ring-4 focus:ring-brand-500/15 disabled:opacity-60 aria-[invalid=true]:border-red-500'

const FieldContext = createContext(null)

/** Label + control + hint/error. Controls inside (Input, Select, Textarea) pick up the id and aria attributes via context,
 *  so the label stays correctly associated even when the control is wrapped in another element (e.g. a password reveal button). */
export function Field({ label, hint, error, required, className, children }) {
  const id = useId()
  const describedBy = error ? `${id}-err` : hint ? `${id}-hint` : undefined
  return (
    <FieldContext.Provider value={{ id, describedBy, invalid: !!error, required }}>
      <div className={cx('text-sm', className)}>
        <label htmlFor={id} className="mb-1.5 block font-medium text-ink">
          {label}{required && <span className="ml-0.5 text-red-500" aria-hidden="true">*</span>}
        </label>
        {children}
        {error ? <p id={`${id}-err`} className="mt-1.5 text-xs text-red-600 dark:text-red-400" role="alert">{error}</p>
          : hint ? <p id={`${id}-hint`} className="mt-1.5 text-xs text-muted">{hint}</p> : null}
      </div>
    </FieldContext.Provider>
  )
}

function useFieldProps() {
  const f = useContext(FieldContext)
  return f ? { id: f.id, 'aria-describedby': f.describedBy, 'aria-invalid': f.invalid || undefined, required: f.required || undefined } : {}
}

export const Input = ({ className, ...props }) => <input className={cx(control, className)} {...useFieldProps()} {...props} />
export const Select = ({ className, children, ...props }) => <select className={cx(control, 'pr-8', className)} {...useFieldProps()} {...props}>{children}</select>
export const Textarea = ({ className, ...props }) => <textarea rows={3} className={cx(control, 'resize-y', className)} {...useFieldProps()} {...props} />

export function SearchInput({ className, ...props }) {
  return (
    <div className={cx('relative', className)}>
      <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted" aria-hidden="true" />
      <input type="search" className={cx(control, 'pl-9')} {...props} />
    </div>
  )
}


export function Switch({ checked, onChange, label, disabled }) {
  return (
    <button type="button" role="switch" aria-checked={checked} aria-label={label} disabled={disabled} onClick={() => onChange(!checked)}
      className={cx('relative inline-flex h-6 w-11 shrink-0 items-center rounded-full transition-colors focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-600 disabled:opacity-50',
        checked ? 'bg-brand-600' : 'bg-slate-300 dark:bg-slate-600')}>
      <span className={cx('inline-block h-5 w-5 transform rounded-full bg-white shadow transition-transform', checked ? 'translate-x-5' : 'translate-x-0.5')} />
    </button>
  )
}
