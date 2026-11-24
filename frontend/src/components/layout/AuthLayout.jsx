import { Navigate, Outlet } from 'react-router-dom'
import { HeartHandshake, ShieldCheck, Truck } from 'lucide-react'
import { useAuth, homeFor } from '../../lib/auth.jsx'
import Logo from './Logo.jsx'
import ThemeToggle from './ThemeToggle.jsx'

const POINTS = [
  [HeartHandshake, 'Post surplus food in under a minute', 'Restaurants, shops, event hosts and households.'],
  [Truck, 'Volunteers close the last mile', 'Claim a pickup, collect, deliver, done.'],
  [ShieldCheck, 'Safety information on every listing', 'Storage conditions and allergens, always visible.'],
]

/** Split-screen layout for sign-in / sign-up. Signed-in users are sent to their home page. */
export default function AuthLayout() {
  const { user } = useAuth()
  if (user) return <Navigate to={homeFor(user.role)} replace />
  return (
    <div className="grid min-h-screen lg:grid-cols-[minmax(0,5fr)_minmax(0,6fr)]">
      <aside className="relative hidden overflow-hidden bg-gradient-to-br from-brand-800 via-brand-700 to-brand-500 p-12 text-white lg:flex lg:flex-col">
        <div className="absolute -right-24 -top-24 h-80 w-80 rounded-full bg-white/10" aria-hidden="true" />
        <div className="absolute -bottom-32 -left-20 h-96 w-96 rounded-full bg-brand-900/30" aria-hidden="true" />
        <Logo light className="relative" />
        <div className="relative my-auto max-w-md">
          <h2 className="text-4xl font-bold leading-tight tracking-tight">Good food deserves a plate, not a bin.</h2>
          <p className="mt-4 text-lg text-brand-100">FoodRescue connects surplus food with charities and neighbours who can use it - safely, quickly and with full traceability.</p>
          <ul className="mt-10 space-y-6">
            {POINTS.map(([Icon, title, text]) => (
              <li key={title} className="flex gap-4">
                <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-white/15"><Icon className="h-5 w-5" aria-hidden="true" /></span>
                <span><span className="block font-semibold">{title}</span><span className="text-sm text-brand-100">{text}</span></span>
              </li>
            ))}
          </ul>
        </div>
      </aside>
      <main className="flex flex-col px-4 py-6 sm:px-10">
        <div className="flex items-center justify-between"><div className="lg:invisible"><Logo /></div><ThemeToggle /></div>
        <div className="mx-auto my-auto w-full max-w-md py-10"><Outlet /></div>
      </main>
    </div>
  )
}
