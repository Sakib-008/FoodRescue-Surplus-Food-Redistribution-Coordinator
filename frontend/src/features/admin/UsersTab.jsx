import { useMemo, useState } from 'react'
import { Trash2 } from 'lucide-react'
import { Alert, Avatar, Badge, Button, DataTable, ListSkeleton, Pagination, SearchInput, Select, Switch, useConfirm, useToast } from '../../components/ui'
import { api } from '../../lib/api.js'
import { useAuth } from '../../lib/auth.jsx'
import { fmtDateTime } from '../../lib/format.js'
import { useLoad } from '../../lib/hooks.js'

const ROLES = ['ADMIN', 'DONOR', 'RECIPIENT', 'VOLUNTEER']

export default function UsersTab() {
  const { user: me } = useAuth()
  const toast = useToast()
  const [role, setRole] = useState('')
  const [page, setPage] = useState(0)
  const [query, setQuery] = useState('')
  const { data, loading, error, reload } = useLoad(() => api('/api/admin/users', { params: { role, page, size: 12 } }), [role, page])

  const rows = useMemo(() => {
    const q = query.trim().toLowerCase()
    return (data?.content || []).filter((u) => !q || u.fullName.toLowerCase().includes(q) || u.email.toLowerCase().includes(q))
  }, [data, query])

  const confirm = useConfirm()
  const remove = async (u) => {
    if (!(await confirm({ title: `Delete ${u.fullName}?`, message: 'Only accounts with no donations, requests or pickups can be deleted. Disable the account otherwise.', confirmLabel: 'Delete', danger: true }))) return
    act(() => api(`/api/admin/users/${u.id}`, { method: 'DELETE' }), `${u.fullName} deleted`)
  }

  const act = async (fn, okMessage) => {
    try { await fn(); toast.success(okMessage); reload() } catch (err) { toast.error(err.message) }
  }

  const columns = [
    { key: 'user', header: 'User', render: (u) => (
      <div className="flex items-center gap-3"><Avatar name={u.fullName} size="sm" /><div className="min-w-0"><p className="truncate font-medium text-ink">{u.fullName}{u.id === me.id && <Badge tone="info" className="ml-2">You</Badge>}</p><p className="truncate text-xs text-muted">{u.email}</p></div></div>) },
    { key: 'role', header: 'Role', render: (u) => (
      <Select aria-label={`Role of ${u.fullName}`} value={u.role} disabled={u.id === me.id} className="!w-36 !py-1.5"
        onChange={(e) => act(() => api(`/api/admin/users/${u.id}/role`, { method: 'PATCH', body: { role: e.target.value } }), `${u.fullName} is now ${e.target.value.toLowerCase()}`)}>
        {ROLES.map((r) => <option key={r}>{r}</option>)}
      </Select>) },
    { key: 'joined', header: 'Joined', render: (u) => <span className="text-muted">{fmtDateTime(u.createdAt)}</span> },
    { key: 'enabled', header: 'Active', render: (u) => (
      <div className="flex items-center gap-2">
        <Switch checked={u.enabled} disabled={u.id === me.id} label={`${u.enabled ? 'Disable' : 'Enable'} ${u.fullName}`}
          onChange={(v) => act(() => api(`/api/admin/users/${u.id}/status`, { method: 'PATCH', body: { enabled: v } }), `${u.fullName} ${v ? 'enabled' : 'disabled'}`)} />
        <span className="text-xs text-muted">{u.enabled ? 'Active' : 'Disabled'}</span>
      </div>) },
    { key: 'actions', header: '', render: (u) => u.id !== me.id && (
      <Button variant="ghost" size="sm" onClick={() => remove(u)} aria-label={`Delete ${u.fullName}`}><Trash2 className="h-4 w-4" /></Button>) },
  ]

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap gap-3">
        <SearchInput className="w-full sm:w-72" placeholder="Search this page by name or email" aria-label="Search users" value={query} onChange={(e) => setQuery(e.target.value)} />
        <Select aria-label="Filter by role" className="!w-44" value={role} onChange={(e) => { setPage(0); setRole(e.target.value) }}>
          <option value="">All roles</option>{ROLES.map((r) => <option key={r}>{r}</option>)}
        </Select>
      </div>
      {error && <Alert>{error}</Alert>}
      {loading || !data ? <ListSkeleton rows={5} /> : <DataTable columns={columns} rows={rows} empty="No users match." />}
      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}
    </div>
  )
}
