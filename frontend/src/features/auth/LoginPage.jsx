import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Eye, EyeOff, LogIn } from 'lucide-react'
import { Alert, Button, Field, IconButton, Input } from '../../components/ui'
import { homeFor, loginRequest, useAuth } from '../../lib/auth.jsx'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '' })
  const [show, setShow] = useState(false)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const submit = async (e) => {
    e.preventDefault()
    setBusy(true); setError('')
    try {
      const data = await loginRequest(form.email, form.password)
      login(data)
      navigate(homeFor(data.user.role), { replace: true })
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <div>
      <h1 className="text-3xl font-bold tracking-tight text-ink">Welcome back</h1>
      <p className="mt-2 text-muted">Sign in to manage donations, requests and pickups.</p>
      <form onSubmit={submit} className="mt-8 space-y-5">
        <Alert>{error}</Alert>
        <Field label="Email address" required>
          <Input type="email" autoComplete="email" placeholder="you@example.com" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        </Field>
        <Field label="Password" required>
          <div className="relative">
            <Input type={show ? 'text' : 'password'} autoComplete="current-password" className="pr-11" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
            <IconButton label={show ? 'Hide password' : 'Show password'} onClick={() => setShow((s) => !s)} className="absolute right-0.5 top-0.5">
              {show ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
            </IconButton>
          </div>
        </Field>
        <Button type="submit" size="lg" loading={busy} className="w-full"><LogIn className="h-4 w-4" />Sign in</Button>
      </form>
      <p className="mt-8 text-center text-sm text-muted">New to FoodRescue? <Link to="/register" className="font-medium text-brand-700 hover:underline dark:text-brand-300">Create an account</Link></p>
    </div>
  )
}
