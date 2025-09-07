import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'

const Login: React.FC = () => {
  const navigate = useNavigate()
  const [userName, setUserName] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")
  const [successMessage, setSuccessMessage] = useState("")

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")
    setSuccessMessage("")

    try {
      console.log("🔹 Sending login request:", { userName, password })
      const response = await axios.post(
        "http://localhost:8080/api/auth/login",
        { userName, password }
      )

      const { token, role } = response.data
      localStorage.setItem("token", token)
      localStorage.setItem(
        "role",
        typeof role === "string" ? role : role?.roleName || JSON.stringify(role)
      )

      console.log("✅ Login Successful:", response.data)
      setSuccessMessage("✅ Login successful! Redirecting...")

      // Redirect based on role
      setTimeout(() => {
        if (role === "ADMIN") {
          navigate("/admin-dashboard")
        } else {
          navigate("/dashboard")
        }
      }, 2000)
    } catch (err: any) {
      console.error("❌ Login Error:", err.response?.data || err.message)
      setError("Invalid username or password.")
    }
  }

  return (
    <div className="page-container">
      <div className="container">
        <div className="page-header">
          <h1 className="page-title">Login to Your Account</h1>
          <p className="page-description">
            Welcome back! Please sign in to continue managing your finances.
          </p>
        </div>

        <div className="auth-container">
          <div className="card auth-card">
            {successMessage && <div className="alert alert-success">{successMessage}</div>}
            {error && <div className="alert alert-error">{error}</div>}
            
            <form onSubmit={handleLogin} className="auth-form">
              <div className="form-group">
                <label htmlFor="userName" className="form-label">
                  Username
                </label>
                <input
                  type="text"
                  id="userName"
                  name="userName"
                  value={userName}
                  onChange={(e) => setUserName(e.target.value)}
                  className="form-input"
                  required
                  placeholder="Enter your username"
                />
              </div>

              <div className="form-group">
                <label htmlFor="password" className="form-label">
                  Password
                </label>
                <input
                  type="password"
                  id="password"
                  name="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="form-input"
                  required
                  placeholder="Enter your password"
                />
              </div>

              <div className="form-group">
                <label className="checkbox-label">
                  <input type="checkbox" />
                  <span>Remember me</span>
                </label>
              </div>

              <button type="submit" className="btn btn-primary auth-button">
                Sign In
              </button>

              <div className="auth-links">
                <a href="#" className="auth-link">Forgot your password?</a>
                <p>
                  Don't have an account? <a href="/register" className="auth-link">Sign up here</a>
                </p>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Login
