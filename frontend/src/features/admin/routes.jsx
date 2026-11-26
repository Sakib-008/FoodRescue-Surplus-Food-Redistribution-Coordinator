import { ShieldCheck } from 'lucide-react'
import AdminDashboard from './AdminDashboard.jsx'

export default [
  { path: '/admin', element: <AdminDashboard />, roles: ['ADMIN'], nav: { label: 'Administration', icon: ShieldCheck, order: 11 } },
]
