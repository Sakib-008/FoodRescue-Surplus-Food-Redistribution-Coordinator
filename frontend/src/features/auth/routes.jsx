import LoginPage from './LoginPage.jsx'
import ProfilePage from './ProfilePage.jsx'
import RegisterPage from './RegisterPage.jsx'

export default [
  { path: '/login', element: <LoginPage />, layout: 'auth' },
  { path: '/register', element: <RegisterPage />, layout: 'auth' },
  { path: '/profile', element: <ProfilePage /> },
]
