import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { HeartHandshake, MapPin, Truck, Utensils } from 'lucide-react'
import { Alert, Button, Field, Input, cx } from '../../components/ui'
import { api } from '../../lib/api.js'
import { homeFor, useAuth } from '../../lib/auth.jsx'
import { getBrowserLocation } from '../../lib/format.js'

const ROLES = [
  { value: 'DONOR', icon: Utensils, title: 'Donor', text: 'I have surplus food to share' },
  { value: 'RECIPIENT', icon: HeartHandshake, title: 'Recipient', text: 'I need food for people' },
  { value: 'VOLUNTEER', icon: Truck, title: 'Volunteer', text: 'I can collect & deliver' },
]

function strength(pw) {
  let s = 0
  if (pw.length >= 8) s++
  if (/[A-Z]/.test(pw) && /[a-z]/.test(pw)) s++
  if (/\d/.test(pw)) s++
  if (/[^A-Za-z0-9]/.test(pw) || pw.length >= 12) s++
  return s
}

export default function RegisterPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ role: 'RECIPIENT', fullName: '', email: '', password: '', phone: '', address: '', lat: '', lng: '' })
  const [error, setError] = useState('')
  const [fields, setFields] = useState({})
  const [busy, setBusy] = useState(false)
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })
  const score = strength(form.password)

  const locate = async () => {
    try {
      const { lat, lng } = await getBrowserLocation()
      setForm((f) => ({ ...f, lat: lat.toFixed(5), lng: lng.toFixed(5) }))
    } catch (err) { setError(err.message) }
  }

  const submit = async (e) => {
    e.preventDefault()
    setBusy(true); setError(''); setFields({})
    try {
      const body = { ...form, lat: form.lat === '' ? null : Number(form.lat), lng: form.lng === '' ? null : Number(form.lng) }
      const data = await api('/api/auth/register', { method: 'POST', body })
      login(data)
      navigate(homeFor(data.user.role), { replace: true })
    } catch (err) {
      setError(err.message); setFields(err.fields || {})
    } finally { setBusy(false) }
  }

  return (
    <div>
      <h1 className="text-3xl font-bold tracking-tight text-ink">Create your account</h1>
      <p className="mt-2 text-muted">It takes less than a minute.</p>
      <form onSubmit={submit} className="mt-8 space-y-5">
        <Alert>{error}</Alert>
        <fieldset>
          <legend className="mb-2 text-sm font-medium text-ink">I am joining as a…</legend>
          <div className="grid grid-cols-3 gap-3">
            {ROLES.map(({ value, icon: Icon, title, text }) => (
              <label key={value} className={cx('relative flex cursor-pointer flex-col items-center rounded-xl border p-3 text-center transition',
                form.role === value ? 'border-brand-500 bg-brand-50 ring-2 ring-brand-500/30 dark:bg-brand-500/10' : 'border-line bg-surface hover:bg-subtle')}>
                <input type="radio" name="role" value={value} checked={form.role === value} onChange={set('role')} className="sr-only" />
                <Icon className={cx('mb-1.5 h-6 w-6', form.role === value ? 'text-brand-600' : 'text-muted')} aria-hidden="true" />
                <span className="text-sm font-semibold text-ink">{title}</span>
                <span className="mt-0.5 hidden text-[11px] leading-tight text-muted sm:block">{text}</span>
              </label>
            ))}
          </div>
        </fieldset>
        <Field label="Full name or organisation" required error={fields.fullName}><Input autoComplete="name" value={form.fullName} onChange={set('fullName')} /></Field>
        <Field label="Email address" required error={fields.email}><Input type="email" autoComplete="email" value={form.email} onChange={set('email')} /></Field>
        <div>
          <Field label="Password" required error={fields.password} hint="At least 8 characters"><Input type="password" autoComplete="new-password" value={form.password} onChange={set('password')} /></Field>
          <div className="mt-2 flex gap-1" aria-hidden="true">
            {[0, 1, 2, 3].map((i) => <span key={i} className={cx('h-1 flex-1 rounded-full', i < score ? ['bg-red-400', 'bg-amber-400', 'bg-lime-500', 'bg-emerald-500'][score - 1] : 'bg-subtle')} />)}
          </div>
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <Field label="Phone"><Input type="tel" autoComplete="tel" value={form.phone} onChange={set('phone')} /></Field>
          <Field label="Address"><Input autoComplete="street-address" value={form.address} onChange={set('address')} /></Field>
        </div>
        <div className="flex items-end gap-3">
          <Field label="Latitude" error={fields.lat} className="flex-1"><Input type="number" step="any" value={form.lat} onChange={set('lat')} /></Field>
          <Field label="Longitude" error={fields.lng} className="flex-1"><Input type="number" step="any" value={form.lng} onChange={set('lng')} /></Field>
          <Button variant="secondary" onClick={locate} aria-label="Use my location"><MapPin className="h-4 w-4" /></Button>
        </div>
        <Button type="submit" size="lg" loading={busy} className="w-full">Create account</Button>
      </form>
      <p className="mt-8 text-center text-sm text-muted">Already registered? <Link to="/login" className="font-medium text-brand-700 hover:underline dark:text-brand-300">Sign in</Link></p>
    </div>
  )
}
