// Thin fetch wrapper around the shared response envelope:
//   success: { success: true, data, message }   error: { success: false, error: { code, message, fields } }
const TOKEN_KEY = 'fr_token'

export class ApiError extends Error {
  constructor(status, code, message, fields) {
    super(message)
    this.status = status
    this.code = code
    this.fields = fields || {}
  }
}

export const tokenStore = {
  get: () => localStorage.getItem(TOKEN_KEY),
  set: (t) => localStorage.setItem(TOKEN_KEY, t),
  clear: () => localStorage.removeItem(TOKEN_KEY),
}

let onUnauthorized = () => {}
export const setUnauthorizedHandler = (fn) => { onUnauthorized = fn }

export async function api(path, { method = 'GET', body, form, params } = {}) {
  const headers = {}
  const token = tokenStore.get()
  if (token) headers.Authorization = `Bearer ${token}`
  let payload
  if (form) {
    payload = form
  } else if (body !== undefined) {
    headers['Content-Type'] = 'application/json'
    payload = JSON.stringify(body)
  }
  let url = path
  if (params) {
    const qs = new URLSearchParams()
    Object.entries(params).forEach(([k, v]) => {
      if (v !== undefined && v !== null && v !== '') qs.append(k, v)
    })
    const s = qs.toString()
    if (s) url += (path.includes('?') ? '&' : '?') + s
  }

  let res
  try {
    res = await fetch(url, { method, headers, body: payload })
  } catch {
    throw new ApiError(0, 'NETWORK', 'Cannot reach the server. Is the backend running?')
  }
  let json = null
  try { json = await res.json() } catch { /* empty body */ }

  if (!res.ok || (json && json.success === false)) {
    const err = json?.error || {}
    if (res.status === 401 && token) onUnauthorized()
    throw new ApiError(res.status, err.code || 'ERROR', err.message || `Request failed (${res.status})`, err.fields)
  }
  return json ? json.data : null
}

/** Downloads a file endpoint (e.g. a CSV report) with the user's token and saves it in the browser. */
export async function download(path, fileName) {
  const token = tokenStore.get()
  const res = await fetch(path, { headers: token ? { Authorization: `Bearer ${token}` } : {} })
  if (!res.ok) throw new ApiError(res.status, 'DOWNLOAD_FAILED', `Download failed (${res.status})`)
  const url = URL.createObjectURL(await res.blob())
  const a = document.createElement('a')
  a.href = url
  a.download = fileName
  a.click()
  URL.revokeObjectURL(url)
}
