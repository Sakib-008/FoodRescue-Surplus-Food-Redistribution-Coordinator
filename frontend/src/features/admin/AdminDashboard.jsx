import { PageHeader } from '../../components/ui'
import UsersTab from './UsersTab.jsx'


export default function AdminDashboard() {
  return (
    <div>
      <PageHeader title="Administration" description="Manage accounts: roles, access and removal of unused accounts." />
      <UsersTab />
    </div>
  )
}
