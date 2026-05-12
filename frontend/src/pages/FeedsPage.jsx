import { useState, useEffect, useCallback } from 'react'
import { api } from '../api/client'

export default function FeedsPage({ user }) {
  const [feeds, setFeeds] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({ name: '', url: '' })
  const [submitting, setSubmitting] = useState(false)

  const load = useCallback(async () => {
    setLoading(true); setError(null)
    try { setFeeds(await api.feeds.list(user.id)) }
    catch (e) { setError(e.message) }
    finally { setLoading(false) }
  }, [user.id])

  useEffect(() => { load() }, [load])

  const handleAdd = async (e) => {
    e.preventDefault()
    setSubmitting(true); setError(null)
    try {
      await api.feeds.create(user.id, form.name, form.url)
      setForm({ name: '', url: '' }); setShowForm(false)
      load()
    } catch (e) { setError(e.message) }
    finally { setSubmitting(false) }
  }

  const toggleFollow = async (feed) => {
    try {
      if (feed.following) await api.feeds.unfollow(user.id, feed.id)
      else await api.feeds.follow(user.id, feed.id)
      load()
    } catch (e) { setError(e.message) }
  }

  const formatDate = (iso) => iso ? new Date(iso).toLocaleDateString() : 'never'

  return (
    <div>
      <div className="page-header">
        <div>
          <h2 className="page-title">all feeds</h2>
          <p className="page-subtitle">{feeds.length} feed{feeds.length !== 1 ? 's' : ''} available</p>
        </div>
        <button className="btn-primary" onClick={() => setShowForm(f => !f)}>
          {showForm ? '✕ cancel' : '+ add feed'}
        </button>
      </div>

      {showForm && (
        <form className="add-form" onSubmit={handleAdd}>
          <h3>add new feed</h3>
          <div className="form-row">
            <div className="form-group">
              <label>feed name</label>
              <input value={form.name} onChange={e => setForm(f=>({...f,name:e.target.value}))} placeholder="e.g. Hacker News" required />
            </div>
            <div className="form-group">
              <label>rss url</label>
              <input value={form.url} onChange={e => setForm(f=>({...f,url:e.target.value}))} placeholder="https://..." type="url" required />
            </div>
            <button type="submit" className="btn-primary" disabled={submitting} style={{alignSelf:'flex-end'}}>
              {submitting ? '...' : 'add'}
            </button>
          </div>
        </form>
      )}

      {error && <div className="error-msg">{error}</div>}
      {loading ? <div className="spinner" /> : feeds.length === 0 ? (
        <div className="empty-state">
          <div className="big">📡</div>
          <p>No feeds yet. Add one above!</p>
        </div>
      ) : (
        <div className="feeds-table">
          <div className="feeds-header">
            <span>feed</span>
            <span>added by</span>
            <span>last fetched</span>
            <span></span>
          </div>
          {feeds.map(feed => (
            <div key={feed.id} className={'feed-row' + (feed.following ? ' followed' : '')}>
              <div className="feed-info">
                {feed.following && <span className="followed-dot" title="following" />}
                <div>
                  <div className="feed-name">{feed.name}</div>
                  <a href={feed.url} target="_blank" rel="noopener noreferrer" className="feed-url">{feed.url}</a>
                </div>
              </div>
              <span className="feed-meta">{feed.userName}</span>
              <span className="feed-meta">{formatDate(feed.lastFetchedAt)}</span>
              <div className="feed-actions">
                {feed.userId === user.id ? (
                  <span className="feed-owner">you own this</span>
                ) : (
                  <button
                    className={feed.following ? 'btn-danger' : 'btn-ghost'}
                    onClick={() => toggleFollow(feed)}
                    style={{fontSize:'12px', padding:'5px 12px'}}
                  >
                    {feed.following ? 'unfollow' : '+ follow'}
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
      <style>{`
        .add-form {
          background: var(--surface); border: 1px solid var(--green); border-radius: var(--radius);
          padding: 20px; margin-bottom: 24px;
        }
        .add-form h3 { font-size: 13px; color: var(--green); margin-bottom: 14px; }
        .form-row { display: grid; grid-template-columns: 1fr 1fr auto; gap: 12px; align-items: flex-start; }
        .form-group label { display: block; font-size: 11px; color: var(--muted); margin-bottom: 6px; }
        .feeds-table { border: 1px solid var(--border); border-radius: var(--radius); overflow: hidden; }
        .feeds-header, .feed-row {
          display: grid; grid-template-columns: 2fr 1fr 1fr auto;
          align-items: center; gap: 16px; padding: 12px 16px;
        }
        .feeds-header { background: var(--surface2); font-size: 11px; color: var(--muted); border-bottom: 1px solid var(--border); }
        .feed-row { border-bottom: 1px solid var(--border); transition: background 0.1s; }
        .feed-row:last-child { border-bottom: none; }
        .feed-row:hover { background: var(--surface); }
        .feed-row.followed { background: rgba(0,229,122,0.03); }
        .feed-info { display: flex; align-items: center; gap: 10px; }
        .followed-dot { width: 6px; height: 6px; border-radius: 50%; background: var(--green); flex-shrink: 0; }
        .feed-name { font-size: 13px; font-weight: 500; }
        .feed-url { font-size: 11px; color: var(--muted); display: block; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 280px; }
        .feed-meta { font-size: 12px; color: var(--muted); }
        .feed-owner { font-size: 11px; color: var(--muted); font-style: italic; }
        .feed-actions { display: flex; justify-content: flex-end; }
      `}</style>
    </div>
  )
}
