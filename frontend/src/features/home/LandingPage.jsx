import { Link } from 'react-router-dom'
import { ArrowRight, BellRing, ClipboardCheck, Clock, HeartHandshake, MapPin, PackageCheck, PackagePlus, ScrollText, ShieldCheck, Sparkles, Truck, UserCheck, Utensils } from 'lucide-react'
import { Badge, Card, buttonStyles } from '../../components/ui'

const STEPS = [
  [PackagePlus, 'Post', 'Donors list surplus food with quantity, expiry, pickup window, storage and allergen details.'],
  [Sparkles, 'Match', 'Recipients search and filter, or get ranked matches by category, distance and urgency.'],
  [Truck, 'Collect', 'A volunteer accepts the pickup and collects the food within the pickup window.'],
  [PackageCheck, 'Deliver', 'Delivery is confirmed, everyone is notified, and the donation is counted as rescued.'],
]

const ROLES = [
  { icon: Utensils, title: 'Donors', text: 'Restaurants, shops, event hosts and households with food to spare.', points: ['Publish a donation in a minute', 'Approve or decline requests', 'Automatic expiry, no stale listings'] },
  { icon: HeartHandshake, title: 'Recipients', text: 'Charities, shelters and individuals who can put food to good use.', points: ['Browse by category and distance', 'Request exactly the quantity you need', 'Track every request to delivery'] },
  { icon: Truck, title: 'Volunteers', text: 'People with a bit of time who close the last mile.', points: ['See open collection tasks', 'Accept, collect, deliver', 'Keep a pickup history'] },
]

const TRUST = [
  [ShieldCheck, 'Safety first', 'Storage conditions and allergen details are shown on every listing.'],
  [Clock, 'Expiry enforced', 'Expired food can never be requested; listings expire automatically.'],
  [ClipboardCheck, 'No over-allocation', 'Quantities are locked in the database, so stock can never be promised twice.'],
  [ScrollText, 'Secure accounts', 'Passwords are hashed with BCrypt and every action is checked against your role.'],
]

function PreviewCard() {
  return (
    <div className="relative mx-auto w-full max-w-sm" aria-hidden="true">
      <div className="absolute -inset-6 rounded-[2rem] bg-gradient-to-tr from-brand-200/60 via-transparent to-amber-200/50 blur-2xl dark:from-brand-500/20 dark:to-amber-500/10" />
      <Card padded={false} className="relative overflow-hidden shadow-pop">
        <div className="flex h-36 items-center justify-center bg-gradient-to-br from-brand-100 to-brand-200 dark:from-brand-500/20 dark:to-brand-700/30"><Utensils className="h-14 w-14 text-brand-600/70" /></div>
        <div className="space-y-3 p-5">
          <div className="flex items-start justify-between"><div><p className="font-semibold text-ink">Vegetable biryani</p><p className="text-sm text-muted">Cooked meals</p></div><Badge tone="success" dot>Available</Badge></div>
          <p className="text-sm"><b className="text-ink">30 portions</b> <span className="text-muted">available</span></p>
          <div className="flex flex-wrap gap-2 text-xs"><Badge tone="warning"><Clock className="h-3 w-3" />Expires in 4 h</Badge><Badge><MapPin className="h-3 w-3" />1.2 km away</Badge></div>
          <div className="rounded-lg bg-amber-50 p-2.5 text-xs text-amber-900 dark:bg-amber-500/10 dark:text-amber-200">Allergens: contains nuts (cashew)</div>
          <div className="rounded-lg bg-brand-600 py-2 text-center text-sm font-medium text-white">Request this food</div>
        </div>
      </Card>
      <div className="absolute -bottom-5 -left-4 flex items-center gap-3 rounded-xl border border-line bg-surface px-4 py-3 shadow-pop">
        <span className="flex h-9 w-9 items-center justify-center rounded-full bg-emerald-50 text-emerald-600 dark:bg-emerald-500/15"><BellRing className="h-4 w-4" /></span>
        <span className="text-sm"><b className="block text-ink">Request accepted</b><span className="text-muted">Pickup assigned</span></span>
      </div>
    </div>
  )
}

export default function LandingPage() {
  return (
    <>
      <section className="relative overflow-hidden">
        <div className="absolute inset-x-0 top-0 -z-10 h-[32rem] bg-gradient-to-b from-brand-50 to-transparent dark:from-brand-500/5" aria-hidden="true" />
        <div className="mx-auto grid max-w-6xl items-center gap-16 px-4 py-16 sm:px-6 lg:grid-cols-[1.1fr_1fr] lg:py-24">
          <div>
            <Badge tone="success" className="mb-5"><Sparkles className="h-3.5 w-3.5" />Surplus food redistribution</Badge>
            <h1 className="text-4xl font-extrabold tracking-tight text-ink sm:text-5xl lg:text-6xl">Turn surplus food into <span className="text-brand-600">meals</span> for people who need it.</h1>
            <p className="mt-6 max-w-xl text-lg text-muted">FoodRescue connects restaurants, shops and households with charities and neighbours, and coordinates volunteers to collect and deliver the food before it expires.</p>
            <div className="mt-9 flex flex-wrap gap-3">
              <Link to="/register" className={buttonStyles({ size: 'lg' })}>Get started free<ArrowRight className="h-4 w-4" /></Link>
              <a href="#how" className={buttonStyles({ variant: 'secondary', size: 'lg' })}>See how it works</a>
            </div>
            <ul className="mt-9 flex flex-wrap gap-x-6 gap-y-2 text-sm text-muted">
              {['Real-time notifications', 'Role-based access', 'Safety info on every listing'].map((t) => <li key={t} className="flex items-center gap-2"><UserCheck className="h-4 w-4 text-brand-600" aria-hidden="true" />{t}</li>)}
            </ul>
          </div>
          <PreviewCard />
        </div>
      </section>

      <section id="how" className="scroll-mt-20 border-y border-line bg-surface py-20">
        <div className="mx-auto max-w-6xl px-4 sm:px-6">
          <h2 className="text-center text-3xl font-bold tracking-tight text-ink">From surplus to served in four steps</h2>
          <p className="mx-auto mt-3 max-w-2xl text-center text-muted">Every donation follows a tracked lifecycle, so everybody always knows what happens next.</p>
          <ol className="mt-12 grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
            {STEPS.map(([Icon, title, text], i) => (
              <li key={title} className="relative rounded-xl border border-line bg-canvas p-6">
                <span className="absolute right-5 top-5 text-4xl font-black text-line">{i + 1}</span>
                <span className="mb-4 flex h-11 w-11 items-center justify-center rounded-xl bg-brand-600 text-white"><Icon className="h-5 w-5" aria-hidden="true" /></span>
                <h3 className="font-semibold text-ink">{title}</h3>
                <p className="mt-2 text-sm text-muted">{text}</p>
              </li>
            ))}
          </ol>
        </div>
      </section>

      <section id="roles" className="scroll-mt-20 py-20">
        <div className="mx-auto max-w-6xl px-4 sm:px-6">
          <h2 className="text-center text-3xl font-bold tracking-tight text-ink">There is a place for everyone</h2>
          <div className="mt-12 grid gap-6 lg:grid-cols-3">
            {ROLES.map(({ icon: Icon, title, text, points }) => (
              <Card key={title} className="flex flex-col">
                <span className="mb-4 flex h-11 w-11 items-center justify-center rounded-xl bg-brand-50 text-brand-600 dark:bg-brand-500/15 dark:text-brand-300"><Icon className="h-5 w-5" aria-hidden="true" /></span>
                <h3 className="text-lg font-semibold text-ink">{title}</h3>
                <p className="mt-1 text-sm text-muted">{text}</p>
                <ul className="mt-4 flex-1 space-y-2 text-sm">{points.map((p) => <li key={p} className="flex gap-2 text-ink"><ClipboardCheck className="mt-0.5 h-4 w-4 shrink-0 text-brand-600" aria-hidden="true" />{p}</li>)}</ul>
                <Link to="/register" className={buttonStyles({ variant: 'soft', className: 'mt-6' })}>Join as {title.toLowerCase().replace(/s$/, '')}</Link>
              </Card>
            ))}
          </div>
        </div>
      </section>

      <section id="trust" className="scroll-mt-20 border-t border-line bg-surface py-20">
        <div className="mx-auto max-w-6xl px-4 sm:px-6">
          <h2 className="text-center text-3xl font-bold tracking-tight text-ink">Built to be safe and trustworthy</h2>
          <div className="mt-12 grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
            {TRUST.map(([Icon, title, text]) => (
              <div key={title}><span className="mb-3 flex h-10 w-10 items-center justify-center rounded-lg bg-subtle text-brand-600"><Icon className="h-5 w-5" aria-hidden="true" /></span><h3 className="font-semibold text-ink">{title}</h3><p className="mt-1 text-sm text-muted">{text}</p></div>
            ))}
          </div>
        </div>
      </section>

      <section className="px-4 py-20 sm:px-6">
        <div className="mx-auto max-w-4xl rounded-3xl bg-gradient-to-br from-brand-700 to-brand-500 px-8 py-14 text-center text-white shadow-pop">
          <h2 className="text-3xl font-bold tracking-tight">Ready to rescue some food?</h2>
          <p className="mx-auto mt-3 max-w-xl text-brand-50">Create a free account in a minute and start sharing or receiving surplus food today.</p>
          <Link to="/register" className={buttonStyles({ variant: 'secondary', size: 'lg', className: 'mt-8 !border-0 !text-brand-800' })}>Create your account<ArrowRight className="h-4 w-4" /></Link>
        </div>
      </section>
    </>
  )
}
