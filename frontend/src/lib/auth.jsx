import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { api, setUnauthorizedHandler, tokenStore } from './api.js'

const AuthContext = createContext(null)
const USER_KEY = 'fr_user'

export const homeFor = (role) => ({
  DONOR: '/donor/donations',
  RECIPIENT: '/browse',
  VOLUNTEER: '/volunteer/pickups',
  ADMIN: '/admin',
}[role] || '/login')

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem(USER_KEY)) } catch { return null }
  })

  const clearSession = useCallback(() => {
    tokenStore.clear()
    localStorage.removeItem(USER_KEY)
    setUser(null)
  }, [])

  const login = useCallback(({ token, user }) => {
    tokenStore.set(token)
    localStorage.setItem(USER_KEY, JSON.stringify(user))
    setUser(user)
  }, [])

  const updateUser = useCallback((u) => {
    localStorage.setItem(USER_KEY, JSON.stringify(u))
    setUser(u)
  }, [])

  /** Revokes the token on the server (POST /api/auth/logout), then forgets it locally. */
  const logout = useCallback(async () => {
    try { await api('/api/auth/logout', { method: 'POST' }) } catch { /* token may already be invalid */ }
    clearSession()
  }, [clearSession])

  useEffect(() => { setUnauthorizedHandler(clearSession) }, [clearSession])

  const value = useMemo(() => ({ user, login, logout, updateUser }), [user, login, logout, updateUser])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)

export async function loginRequest(email, password) {
  return api('/api/auth/login', { method: 'POST', body: { email, password } })
}
