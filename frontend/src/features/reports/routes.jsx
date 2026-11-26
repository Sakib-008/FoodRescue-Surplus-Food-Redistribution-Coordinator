import { BarChart3 } from 'lucide-react'
import ReportsPage from './ReportsPage.jsx'

export default [
  { path: '/admin/reports', element: <ReportsPage />, roles: ['ADMIN'], nav: { label: 'Reports', icon: BarChart3, order: 10 } },
]
