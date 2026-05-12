import { useState } from 'react'
import { api } from '../api/client'

export default function LoginPage({ onLogin }) {
  const [tab, setTab] = useState('login')   // 'login' | 'register'
  const [name, setName] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      if (tab === 'register') {
        const user = await api.users.create(name.trim())
        onLogin(user)
      } else {
        const users = await api.users.list()
        const found = users.find(u => u.name === name.trim())
        if (!found) { setError('No user found with that name. Register first?'); return; }
        onLogin(found)
      }
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-brand">
          <span>🐊</span>
          <h1>gator</h1>
          <p>RSS aggregator</p>
        </div>

        <div className="login-tabs">
          <button className={'tab' + (tab === 'login' ? ' active' : '')} onClick={() => setTab('login')}>
            sign in
          </button>
          <button className={'tab' + (tab === 'register' ? ' active' : '')} onClick={() => setTab('register')}>
            register
          </button>
        </div>

        {error && <div className="error-msg">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>username</label>
            <input
              value={name}
              onChange={e => setName(e.target.value)}
              placeholder="e.g. alice"
              autoFocus
              required
            />
          </div>
          <button type="submit" className="btn-primary" style={{width:'100%'}} disabled={loading}>
            {loading ? '...' : tab === 'register' ? 'create account' : 'sign in'}
          </button>
        </form>

        <p className="login-note">
          No passwords — just pick a username. Your feed history is saved server-side.
        </p>
      </div>
      <style>{`
        .login-page {
          min-height: 100vh; display: flex; align-items: center; justify-content: center;
          background: radial-gradient(ellipse at 30% 20%, rgba(0,229,122,0.06) 0%, transparent 60%), var(--bg);
        }
        .login-card {
          background: var(--surface); border: 1px solid var(--border); border-radius: 12px;
          padding: 40px; width: 360px;
        }
        .login-brand { text-align: center; margin-bottom: 28px; }
        .login-brand span { font-size: 48px; display: block; }
        .login-brand h1 { font-family: var(--font-display); font-size: 32px; font-weight: 800; color: var(--green); letter-spacing: -1px; }
        .login-brand p { color: var(--muted); font-size: 12px; }
        .login-tabs { display: flex; gap: 4px; background: var(--surface2); border-radius: var(--radius); padding: 4px; margin-bottom: 20px; }
        .tab { flex: 1; background: none; color: var(--muted); border-radius: 6px; padding: 7px; }
        .tab.active { background: var(--border); color: var(--text); }
        .form-group { margin-bottom: 16px; }
        .form-group label { display: block; font-size: 11px; color: var(--muted); margin-bottom: 6px; }
        .login-note { text-align: center; color: var(--muted); font-size: 11px; margin-top: 20px; }
      `}</style>
    </div>
  )
}
