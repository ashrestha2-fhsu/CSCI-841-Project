import React from 'react'

const About: React.FC = () => {
  return (
    <div className="page-container">
      <div className="container">
        <div className="page-header">
          <h1 className="page-title">About Personal Finance TrackerZ</h1>
          <p className="page-description">
            Learn more about our mission to help you achieve financial freedom
          </p>
        </div>

        <div className="about-content">
          <div className="card">
            <h2>Our Mission</h2>
            <p>
              At Personal Finance TrackerZ, we believe that everyone deserves to have control over their financial future. 
              Our mission is to provide intuitive, powerful tools that make financial management accessible to everyone, 
              regardless of their financial background or experience level.
            </p>
          </div>

          <div className="card">
            <h2>What We Offer</h2>
            <p>
              Our comprehensive platform provides everything you need to take control of your finances:
            </p>
            <ul>
              <li>Smart budgeting tools with automated categorization</li>
              <li>Real-time expense tracking and analysis</li>
              <li>Goal setting and progress monitoring</li>
              <li>Financial insights and recommendations</li>
              <li>Secure and private data handling</li>
            </ul>
          </div>

          <div className="card">
            <h2>Why Choose Us</h2>
            <p>
              We understand that managing finances can be overwhelming. That's why we've designed our platform 
              to be simple, intuitive, and powerful. Our team of financial experts and developers work tirelessly 
              to ensure you have the best tools at your fingertips.
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default About
