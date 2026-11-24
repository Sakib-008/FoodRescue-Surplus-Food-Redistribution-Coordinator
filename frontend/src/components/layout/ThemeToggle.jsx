import { Monitor, Moon, Sun } from 'lucide-react'
import { IconButton } from '../ui'
import { useTheme } from '../../lib/theme.jsx'

const NEXT = { light: 'dark', dark: 'system', system: 'light' }
const ICON = { light: Sun, dark: Moon, system: Monitor }

export default function ThemeToggle() {
  const { theme, setTheme } = useTheme()
  const Icon = ICON[theme] || Monitor
  return (
    <IconButton label={`Theme: ${theme} (click to change)`} onClick={() => setTheme(NEXT[theme] || 'light')}>
      <Icon className="h-[18px] w-[18px]" />
    </IconButton>
  )
}
