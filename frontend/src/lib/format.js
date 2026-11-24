export const fmtDateTime = (iso) => (iso ? new Date(iso).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' }) : '—')
export const fmtQty = (n) => (n === null || n === undefined ? '—' : Number(n).toString())

// <input type="datetime-local"> <-> ISO-8601 UTC
export const toLocalInput = (d) => {
  const date = d instanceof Date ? d : new Date(d)
  const pad = (n) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`
}
export const fromLocalInput = (v) => new Date(v).toISOString()

export const hoursFromNow = (h) => new Date(Date.now() + h * 3600 * 1000)

export function timeLeft(iso) {
  const ms = new Date(iso).getTime() - Date.now()
  if (ms <= 0) return 'expired'
  const m = Math.floor(ms / 60000)
  if (m < 60) return `${m} min left`
  const h = Math.floor(m / 60)
  if (h < 48) return `${h} h left`
  return `${Math.floor(h / 24)} d left`
}

export function getBrowserLocation() {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) return reject(new Error('Geolocation is not supported by this browser'))
    navigator.geolocation.getCurrentPosition(
      (p) => resolve({ lat: p.coords.latitude, lng: p.coords.longitude }),
      () => reject(new Error('Could not get your location (permission denied?)')),
      { timeout: 8000 },
    )
  })
}

export const initials = (name = '') =>
  name.split(/\s+/).filter(Boolean).slice(0, 2).map((s) => s[0].toUpperCase()).join('') || '?'

export function fmtRelative(iso) {
  const s = Math.round((Date.now() - new Date(iso).getTime()) / 1000)
  if (s < 45) return 'just now'
  const m = Math.round(s / 60)
  if (m < 60) return `${m} min ago`
  const h = Math.round(m / 60)
  if (h < 24) return `${h} h ago`
  const d = Math.round(h / 24)
  return d < 30 ? `${d} d ago` : fmtDateTime(iso)
}

/** 'urgent' (< 3h), 'soon' (< 24h), 'ok' */
export function expiryTone(iso) {
  const h = (new Date(iso).getTime() - Date.now()) / 3600000
  return h < 3 ? 'urgent' : h < 24 ? 'soon' : 'ok'
}

/** "Milk, Eggs, baked next to nuts": standard allergens (many-to-many) followed by the free-text notes. */
export const allergenText = (d) =>
  [...(d.allergens || []).map((a) => a.name), d.allergenNotes].filter(Boolean).join(', ')
