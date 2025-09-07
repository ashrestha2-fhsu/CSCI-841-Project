import React from 'react'
import { Link } from 'react-router-dom'

const Home: React.FC = () => {
  return (
    <div className="home-page">
      {/* Hero Section */}
      <div className="hero-section">
        <div className="hero-background">
          <div className="hero-pattern"></div>
        </div>
        
        <div className="container">
          <div className="hero-content">
            {/* Main Heading */}
            <h1 className="hero-title">
              <span className="hero-title-main">Personal Finance</span>
              <span className="hero-title-sub">TrackerZ</span>
            </h1>
            <p className="hero-description">
              Your one-stop solution for managing finances, tracking expenses, and achieving your financial goals
            </p>
            
            {/* CTA Buttons */}
            <div className="hero-buttons">
              <Link to="/dashboard" className="btn btn-primary hero-btn-primary">
                Get Started
              </Link>
              <Link to="/users" className="btn btn-secondary hero-btn-secondary">
                Explore Features
              </Link>
            </div>
          </div>
        </div>
      </div>

      {/* Features Section */}
      <div className="features-section">
        <div className="container">
          <div className="features-header">
            <h2 className="features-title">Everything you need to manage your finances</h2>
            <p className="features-description">
              Powerful tools and insights to help you take control of your financial future
            </p>
          </div>

          <div className="grid grid-cols-3">
            {/* Feature 1 */}
            <div className="feature-card">
              <div className="feature-icon blue">
                💰
              </div>
              <h3 className="feature-title">Smart Budgeting</h3>
              <p className="feature-description">
                Create and manage budgets with intelligent insights and automated tracking to stay on top of your spending.
              </p>
            </div>

            {/* Feature 2 */}
            <div className="feature-card">
              <div className="feature-icon purple">
                📊
              </div>
              <h3 className="feature-title">Expense Tracking</h3>
              <p className="feature-description">
                Monitor your expenses in real-time with detailed categorization and spending analytics.
              </p>
            </div>

            {/* Feature 3 */}
            <div className="feature-card">
              <div className="feature-icon green">
                🎯
              </div>
              <h3 className="feature-title">Financial Goals</h3>
              <p className="feature-description">
                Set and track your financial goals with personalized recommendations and progress monitoring.
              </p>
            </div>
          </div>
        </div>
      </div>


      {/* CTA Section */}
      <div className="cta-section">
        <div className="container">
          <div className="cta-content">
            <h2 className="cta-title">Ready to take control of your finances?</h2>
            <p className="cta-description">
              Start your financial journey today with our comprehensive tools and insights.
            </p>
            <Link to="/dashboard" className="btn btn-primary cta-button">
              Start Free Trial
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Home