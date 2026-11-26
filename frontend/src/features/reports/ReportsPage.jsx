import { CheckCheck, ClipboardList, Download, PackageX, Truck, Users, Utensils, Wheat } from 'lucide-react'
import { Alert, Button, Card, ListSkeleton, PageHeader, SectionTitle, StatCard, useToast } from '../../components/ui'
import { api, download } from '../../lib/api.js'
import { fmtQty } from '../../lib/format.js'
import { useLoad } from '../../lib/hooks.js'
import { BarList, DonutChart } from './Charts.jsx'

const DONATION_COLORS = { AVAILABLE: '#10b981', RESERVED: '#f59e0b', COMPLETED: '#0ea5e9', EXPIRED: '#94a3b8', CANCELLED: '#cbd5e1' }
const ROLE_COLORS = { ADMIN: '#6366f1', DONOR: '#10b981', RECIPIENT: '#0ea5e9', VOLUNTEER: '#f59e0b' }
const title = (s) => s.charAt(0) + s.slice(1).toLowerCase().replace('_', ' ')
const sum = (o) => Object.values(o).reduce((a, b) => a + b, 0)

export default function ReportsPage() {
  const stats = useLoad(() => api('/api/admin/stats'))
  const report = useLoad(() => api('/api/admin/reports/rescued'))
  const toast = useToast()
  const save = (report, format) => download(`/api/admin/reports/${report}/export?format=${format}`, `foodrescue-${report}.${format}`)
    .catch((err) => toast.error(err.message))
  const header = <PageHeader title="Reports" description="Food rescued, in transit and lost to expiry, plus a live overview of the platform."
    actions={<div className="flex flex-wrap gap-2">
      <Button variant="secondary" size="sm" onClick={() => save('rescued-food', 'csv')}><Download className="h-4 w-4" />Rescued food (CSV)</Button>
      <Button variant="secondary" size="sm" onClick={() => save('statistics', 'txt')}><Download className="h-4 w-4" />Statistics (TXT)</Button>
    </div>} />
  if (stats.loading || report.loading) return <div>{header}<ListSkeleton rows={4} /></div>
  if (stats.error || report.error) return <div>{header}<Alert>{stats.error || report.error}</Alert></div>

  const s = stats.data
  const r = report.data
  const qty = (list) => (list.length ? list.map((q) => `${fmtQty(q.quantity)} ${q.unit}`).join(' · ') : '0')
  const toData = (obj, colors) => Object.entries(obj).map(([k, v]) => ({ label: title(k), value: v, color: colors?.[k] }))

  return (
    <div className="space-y-6">
      {header}
      <section aria-label="Rescue report" className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <StatCard icon={Wheat} tone="brand" label="Food rescued" value={qty(r.rescued)} hint="Delivered to recipients" />
        <StatCard icon={Truck} tone="violet" label="In transit" value={qty(r.inTransit)} hint="Accepted, not yet delivered" />
        <StatCard icon={PackageX} tone="red" label="Expired unclaimed" value={qty(r.expiredUnclaimed)} hint={`${r.expiredDonations} expired donation(s)`} />
        <StatCard icon={CheckCheck} tone="sky" label="Completed / cancelled" value={`${r.completedDistributions} / ${r.cancelledDistributions}`} hint="Distributions" />
      </section>

      <section aria-label="Totals" className="grid gap-4 sm:grid-cols-3">
        <StatCard icon={Users} tone="brand" label="Registered users" value={sum(s.users)} />
        <StatCard icon={Utensils} tone="amber" label="Donations posted" value={sum(s.donations)} />
        <StatCard icon={ClipboardList} tone="sky" label="Requests made" value={sum(s.requests)} />
      </section>

      <div className="grid gap-6 lg:grid-cols-2">
        <Card><SectionTitle title="Donations by status" /><DonutChart data={toData(s.donations, DONATION_COLORS)} centerLabel="donations" /></Card>
        <Card><SectionTitle title="Users by role" /><DonutChart data={toData(s.users, ROLE_COLORS)} centerLabel="users" /></Card>
        <Card><SectionTitle title="Requests by status" /><BarList data={toData(s.requests)} /></Card>
        <Card><SectionTitle title="Pickups by status" /><BarList data={toData(s.pickups)} color="bg-violet-500" /></Card>
      </div>
    </div>
  )
}
