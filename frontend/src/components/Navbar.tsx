import React, { useState, useEffect } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'

const Navbar: React.FC = () => {
  const location = useLocation()
  const navigate = useNavigate()
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const [isAuthenticated, setIsAuthenticated] = useState(false)

  // Check if user is authenticated
  useEffect(() => {
    const token = localStorage.getItem('token')
    setIsAuthenticated(!!token)

    // Listen for storage changes (when user logs in/out in another tab)
    const handleStorageChange = () => {
      const token = localStorage.getItem('token')
      setIsAuthenticated(!!token)
    }

    window.addEventListener('storage', handleStorageChange)
    return () => window.removeEventListener('storage', handleStorageChange)
  }, [])

  // Also check on location change (when navigating)
  useEffect(() => {
    const token = localStorage.getItem('token')
    setIsAuthenticated(!!token)
  }, [location])

  const isActive = (path: string) => {
    return location.pathname === path
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    setIsAuthenticated(false)
    navigate('/')
  }

  // Base navigation items (always shown)
  const baseNavItems = [
    { path: '/', label: 'Home', icon: '🏠' },
    { path: '/about', label: 'About', icon: 'ℹ️' },
  ]

  // Conditional navigation items
  const authNavItems = isAuthenticated
    ? [
        { path: '/dashboard', label: 'Dashboard', icon: '📊' },
        { 
          path: '#', 
          label: 'Logout', 
          icon: '🚪', 
          onClick: handleLogout,
          isButton: true 
        },
      ]
    : [
        { path: '/login', label: 'Login', icon: '🔑' },
        { path: '/register', label: 'Register', icon: '📝' },
      ]

  const navItems = [...baseNavItems, ...authNavItems]

  return (
    <nav className="navbar">
      <div className="navbar-content">
        <Link to="/" className="navbar-brand">
          <div className="navbar-logo">PFT</div>
          <span>Personal Finance TrackerZ</span>
        </Link>

        {/* Desktop Navigation */}
        <ul className="navbar-nav">
          {(navItems as (typeof baseNavItems[0] & { isButton?: boolean; onClick?: () => void })[]).map((item) => (
            <li key={item.path}>
              {'isButton' in item && item.isButton ? (
                <button
                  onClick={item.onClick}
                  className={isActive(item.path) ? 'active' : ''}
                  style={{ 
                    background: 'none', 
                    border: 'none', 
                    cursor: 'pointer',
                    color: 'inherit',
                    fontSize: 'inherit',
                    fontFamily: 'inherit',
                    padding: '0.5rem 1rem',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '0.5rem'
                  }}
                >
                  <span className="icon">{item.icon}</span>
                  <span>{item.label}</span>
                </button>
              ) : (
                <Link
                  to={item.path}
                  className={isActive(item.path) ? 'active' : ''}
                >
                  <span className="icon">{item.icon}</span>
                  <span>{item.label}</span>
                </Link>
              )}
            </li>
          ))}
        </ul>

        {/* Mobile menu button */}
        <button
          className="mobile-menu-btn"
          onClick={() => setIsMenuOpen(!isMenuOpen)}
        >
          <svg width="24" height="24" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            {isMenuOpen ? (
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            ) : (
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
            )}
          </svg>
        </button>
      </div>

      {/* Mobile Navigation */}
      <div className={`mobile-menu ${isMenuOpen ? 'active' : ''}`}>
        <ul className="navbar-nav">
          {navItems.map((item: any) => (
            <li key={item.path}>
              {item.isButton ? (
                <button
                  onClick={() => {
                    if (typeof item.onClick === 'function') item.onClick()
                    setIsMenuOpen(false)
                  }}
                  className={isActive(item.path) ? 'active' : ''}
                  style={{ 
                    background: 'none', 
                    border: 'none', 
                    cursor: 'pointer',
                    color: 'inherit',
                    fontSize: 'inherit',
                    fontFamily: 'inherit',
                    padding: '0.5rem 1rem',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '0.5rem',
                    width: '100%',
                    textAlign: 'left'
                  }}
                >
                  <span className="icon">{item.icon}</span>
                  <span>{item.label}</span>
                </button>
              ) : (
                <Link
                  to={item.path}
                  className={isActive(item.path) ? 'active' : ''}
                  onClick={() => setIsMenuOpen(false)}
                >
                  <span className="icon">{item.icon}</span>
                  <span>{item.label}</span>
                </Link>
              )}
            </li>
          ))}
        </ul>
      </div>
    </nav>
  )
}

export default Navbar