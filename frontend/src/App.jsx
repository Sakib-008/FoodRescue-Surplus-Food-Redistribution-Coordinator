import { Navigate, Route, Routes } from 'react-router-dom'
import { ShieldX } from 'lucide-react'
import AppShell from './components/layout/AppShell.jsx'
import AuthLayout from './components/layout/AuthLayout.jsx'
import NotFound from './components/layout/NotFound.jsx'
import PublicLayout from './components/layout/PublicLayout.jsx'
import { EmptyState } from './components/ui'
import { homeFor, useAuth } from './lib/auth.jsx'

// Every folder in src/features/ exports its own routes.jsx. They are discovered here automatically, so team
// members add pages without ever editing this shared file (no merge conflicts).
//   route: { path, element, layout?: 'public' | 'auth', roles?: [...], nav?: { label, icon, order } }
//   no layout = signed-in area (sidebar shell); roles = who may open it.
const modules = import.meta.glob('./features/*/routes.jsx', { eager: true })
const routes = Object.values(modules).flatMap((m) => m.default)

function RequireAuth({ children }) {
  const { user } = useAuth()
  return user ? children : <Navigate to="/login" replace />
}

/** Fallback for '/' when no feature registers a landing page (e.g. a team member's partial checkout). */
function DefaultHome() {
  const { user } = useAuth()
  return <Navigate to={user ? homeFor(user.role) : '/login'} replace />
}

function RoleGate({ roles, children }) {
  const { user } = useAuth()
  if (roles && !roles.includes(user.role)) {
    return <EmptyState icon={ShieldX} title="You do not have access to this page" description="Your account role is not allowed to open it." />
  }
  return children
}

export default function App() {
  const navItems = routes.filter((r) => r.nav)
    .map((r) => ({ path: r.path, label: r.nav.label, icon: r.nav.icon, order: r.nav.order ?? 100, roles: r.roles }))
    .sort((a, b) => a.order - b.order)
  const of = (layout) => routes.filter((r) => r.layout === layout)

  return (
    <Routes>
      <Route element={<PublicLayout />}>
        {of('public').map((r) => <Route key={r.path} path={r.path} element={r.element} />)}
      </Route>
      {!routes.some((r) => r.path === '/') && <Route path="/" element={<DefaultHome />} />}
      <Route element={<AuthLayout />}>
        {of('auth').map((r) => <Route key={r.path} path={r.path} element={r.element} />)}
      </Route>
      <Route element={<RequireAuth><AppShell navItems={navItems} /></RequireAuth>}>
        {routes.filter((r) => !r.layout).map((r) => (
          <Route key={r.path} path={r.path} element={<RoleGate roles={r.roles}>{r.element}</RoleGate>} />
        ))}
      </Route>
      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}
