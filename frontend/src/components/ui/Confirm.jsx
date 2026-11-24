import { createContext, useCallback, useContext, useRef, useState } from 'react'
import { Button } from './Button.jsx'
import { Modal } from './Modal.jsx'

const ConfirmContext = createContext(() => Promise.resolve(true))

/** const confirm = useConfirm();  if (await confirm({ title, message, confirmLabel, danger })) { ... } */
export function ConfirmProvider({ children }) {
  const [state, setState] = useState(null)
  const resolver = useRef(null)

  const confirm = useCallback((opts) => new Promise((resolve) => {
    resolver.current = resolve
    setState(opts)
  }), [])

  const close = (answer) => {
    resolver.current?.(answer)
    setState(null)
  }

  return (
    <ConfirmContext.Provider value={confirm}>
      {children}
      <Modal open={!!state} size="sm" onClose={() => close(false)} title={state?.title || 'Are you sure?'}
        footer={<>
          <Button variant="secondary" onClick={() => close(false)}>{state?.cancelLabel || 'Keep it'}</Button>
          <Button variant={state?.danger ? 'danger' : 'primary'} onClick={() => close(true)}>{state?.confirmLabel || 'Confirm'}</Button>
        </>}>
        <p className="text-sm text-muted">{state?.message}</p>
      </Modal>
    </ConfirmContext.Provider>
  )
}

export const useConfirm = () => useContext(ConfirmContext)
