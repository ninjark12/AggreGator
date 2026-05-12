import { useState, useEffect, useCallback } from 'react'
import { api } from '../api/client'

export default function PostsPage({ user }) {
  const [posts, setPosts] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(1)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const load = useCallback(async (p) => {
    setLoading(true); setError(null)
    try {
      const data = await api.posts.myFeed(user.id, p)
      setPosts(data.content)
      setTotalPages(data.totalPages)
    } catch(e) { setError(e.message) }
    finally { setLoading(false) }
  }, [user.id])

  useEffect(() => { load(page) }, [page, load])

  const fmt = (iso) => {
    if (!iso) return null
    return new Date(iso).toLocaleDateString('en-US', {month:'short', day:'numeric', year:'numeric'})
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <h2 className="page-title">my feed</h2>
          <p className="page-subtitle">posts from feeds you follow</p>
        </div>
      </div>

      {error && <div className="error-msg">{error}</div>}
      {loading ? <div className="spinner" /> : posts.length === 0 ? (
        <div className="empty-state">
          <div className="big">📭</div>
          <p>No posts yet. Follow some feeds on the Feeds page!</p>
        </div>
      ) : (
        <>
          <div className="posts-list">
            {posts.map(post => (
              <article key={post.id} className="post-card">
                <div className="post-meta">
                  <span className="post-feed">{post.feedName}</span>
                  {fmt(post.publishedAt) && <span className="post-date">{fmt(post.publishedAt)}</span>}
                </div>
                <a href={post.url} target="_blank" rel="noopener noreferrer" className="post-title">
                  {post.title}
                </a>
                {post.description && (
                  <p className="post-desc">{post.description}</p>
                )}
                <a href={post.url} target="_blank" rel="noopener noreferrer" className="post-link">
                  read article →
                </a>
              </article>
            ))}
          </div>
          {totalPages > 1 && (
            <div className="pagination">
              <button className="btn-ghost" onClick={() => setPage(p => p-1)} disabled={page === 0}>← prev</button>
              <span className="page-info">{page + 1} / {totalPages}</span>
              <button className="btn-ghost" onClick={() => setPage(p => p+1)} disabled={page >= totalPages - 1}>next →</button>
            </div>
          )}
        </>
      )}
      <style>{`
        .posts-list { display: flex; flex-direction: column; gap: 2px; }
        .post-card {
          background: var(--surface); border: 1px solid var(--border); border-radius: var(--radius);
          padding: 18px 20px; transition: border-color 0.15s;
        }
        .post-card:hover { border-color: rgba(0,229,122,0.3); }
        .post-meta { display: flex; gap: 12px; align-items: center; margin-bottom: 8px; }
        .post-feed { font-size: 11px; color: var(--green); background: rgba(0,229,122,0.08); padding: 2px 8px; border-radius: 4px; }
        .post-date { font-size: 11px; color: var(--muted); }
        .post-title { display: block; font-size: 15px; font-family: var(--font-display); font-weight: 700; color: var(--text); line-height: 1.3; margin-bottom: 8px; }
        .post-title:hover { color: var(--green); text-decoration: none; }
        .post-desc { font-size: 12px; color: var(--muted); line-height: 1.6; margin-bottom: 10px; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; }
        .post-link { font-size: 11px; color: var(--muted); }
        .post-link:hover { color: var(--green); }
        .pagination { display: flex; align-items: center; gap: 16px; justify-content: center; margin-top: 24px; }
        .page-info { font-size: 12px; color: var(--muted); }
      `}</style>
    </div>
  )
}
