import { Navigate } from 'react-router-dom'
import { useAuth, homeFor } from '../../lib/auth.jsx'
import LandingPage from './LandingPage.jsx'

/** "/" shows the marketing page to visitors and sends signed-in users to their role's home page. */
export default function HomeRoute() {
  const { user } = useAuth()
  return user ? <Navigate to={homeFor(user.role)} replace /> : <LandingPage />
}
