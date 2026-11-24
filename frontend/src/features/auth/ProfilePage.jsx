import { useEffect, useState } from 'react'
import { Mail, MapPin, Save } from 'lucide-react'
import { Alert, Avatar, Button, Card, Field, Input, ListSkeleton, PageHeader, SectionTitle, useToast } from '../../components/ui'
import { api } from '../../lib/api.js'
import { useAuth } from '../../lib/auth.jsx'
import { fmtDateTime, getBrowserLocation } from '../../lib/format.js'
import { useLoad } from '../../lib/hooks.js'

export default function ProfilePage() {
  const { updateUser } = useAuth()
  const toast = useToast()
  const { data: profile, loading, error: loadError } = useLoad(() => api('/api/users/me'))
  const [form, setForm] = useState(null)
  const [fields, setFields] = useState({})
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    if (profile) setForm({ fullName: profile.fullName, phone: profile.phone || '', address: profile.address || '', lat: profile.lat ?? '', lng: profile.lng ?? '' })
  }, [profile])

  if (loadError) return <Alert>{loadError}</Alert>
  if (loading || !form) return <ListSkeleton rows={3} />
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  const save = async (e) => {
    e.preventDefault(); setBusy(true); setFields({})
    try {
      const body = { ...form, lat: form.lat === '' ? null : Number(form.lat), lng: form.lng === '' ? null : Number(form.lng) }
      updateUser(await api('/api/users/me', { method: 'PUT', body }))
      toast.success('Profile saved')
    } catch (err) { toast.error(err.message); setFields(err.fields || {}) } finally { setBusy(false) }
  }

  const locate = async () => {
    try { const { lat, lng } = await getBrowserLocation(); setForm((f) => ({ ...f, lat: lat.toFixed(5), lng: lng.toFixed(5) })); toast.info('Location captured - remember to save') } catch (err) { toast.error(err.message) }
  }

  return (
    <div className="mx-auto max-w-3xl">
      <PageHeader title="Profile & settings" description="Your contact details and location are used for distance matching and pickups." />
      <Card className="mb-6 flex flex-wrap items-center gap-5">
        <Avatar name={profile.fullName} size="lg" />
        <div className="min-w-0 flex-1">
          <p className="truncate text-lg font-semibold text-ink">{profile.fullName}</p>
          <p className="flex items-center gap-1.5 text-sm text-muted"><Mail className="h-4 w-4" aria-hidden="true" />{profile.email}</p>
          <p className="text-xs text-muted">Member since {fmtDateTime(profile.createdAt)}</p>
        </div>
      </Card>

      <form onSubmit={save}>
        <Card>
          <SectionTitle title="Personal details" />
          <div className="grid gap-5 sm:grid-cols-2">
            <Field label="Full name or organisation" required error={fields.fullName}><Input value={form.fullName} onChange={set('fullName')} /></Field>
            <Field label="Phone" error={fields.phone}><Input type="tel" value={form.phone} onChange={set('phone')} /></Field>
            <Field label="Address" className="sm:col-span-2"><Input value={form.address} onChange={set('address')} /></Field>
          </div>
          <SectionTitle title="Location" description="Used to show how far donations are from you." className="mt-8" />
          <div className="flex flex-wrap items-end gap-4">
            <Field label="Latitude" error={fields.lat} className="min-w-[8rem] flex-1"><Input type="number" step="any" value={form.lat} onChange={set('lat')} /></Field>
            <Field label="Longitude" error={fields.lng} className="min-w-[8rem] flex-1"><Input type="number" step="any" value={form.lng} onChange={set('lng')} /></Field>
            <Button variant="secondary" onClick={locate}><MapPin className="h-4 w-4" />Use my location</Button>
          </div>
          <div className="mt-8 flex justify-end border-t border-line pt-5"><Button type="submit" loading={busy}><Save className="h-4 w-4" />Save changes</Button></div>
        </Card>
      </form>
    </div>
  )
}
