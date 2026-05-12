import { useState, useEffect } from 'react'
import { BrowserRouter, Routes, Route, NavLink, Navigate } from 'react-router-dom'
import { getActiveUser, setActiveUser, api } from './api/client'
import FeedsPage from './pages/FeedsPage'
import PostsPage from './pages/PostsPage'
import LoginPage from './pages/LoginPage'
import './App.css'

export default function App() {
  const [user, setUser] = useState(getActiveUser)

  const login = (u) => { setActiveUser(u); setUser(u) }
  const logout = () => { setActiveUser(null); setUser(null) }

  return (
    <BrowserRouter>
      {user ? (
        <div className="layout">
          <Sidebar user={user} onLogout={logout} />
          <main className="main-content">
            <Routes>
              <Route path="/" element={<Navigate to="/posts" replace />} />
              <Route path="/posts" element={<PostsPage user={user} />} />
              <Route path="/feeds" element={<FeedsPage user={user} />} />
              <Route path="*" element={<Navigate to="/posts" replace />} />
            </Routes>
          </main>
        </div>
      ) : (
        <Routes>
          <Route path="*" element={<LoginPage onLogin={login} />} />
        </Routes>
      )}
    </BrowserRouter>
  )
}

function Sidebar({ user, onLogout }) {
  return (
    <nav className="sidebar">
      <div className="sidebar-brand">
        <span className="brand-icon">🐊</span>
        <span className="brand-name">gator</span>
      </div>
      <div className="sidebar-user">
        <span className="user-label">logged in as</span>
        <span className="user-name">{user.name}</span>
      </div>
      <div className="sidebar-nav">
        <NavLink to="/posts" className={({isActive}) => isActive ? 'nav-link active' : 'nav-link'}>
          <span>▶</span> my feed
        </NavLink>
        <NavLink to="/feeds" className={({isActive}) => isActive ? 'nav-link active' : 'nav-link'}>
          <span>◈</span> all feeds
        </NavLink>
      </div>
      <button className="logout-btn" onClick={onLogout}>← logout</button>
    </nav>
  )
}
