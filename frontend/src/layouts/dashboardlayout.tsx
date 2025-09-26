// src/layouts/DashboardLayout.tsx
import { Outlet, NavLink, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import axiosInstance from "../services/axiosInstance";
import Profile from "../pages/Profile";
import "../styles/dashboard.css";

interface User {
  userId: number;
  firstName?: string;
  lastName?: string;
  userName?: string;
  email: string;
  phoneNumber?: string;
  address?: string;
}

interface Transaction {
  transactionId: number;
  date: string; // ISO string
  transactionType: string;
  amount: number;
  accountName?: string;
  status: string;
}

const DashboardLayout: React.FC = () => {
  const navigate = useNavigate();

  const [showProfile, setShowProfile] = useState<boolean>(false);
  const [showEditProfile, setShowEditProfile] = useState<boolean>(false);
  const [user, setUser] = useState<User | null>(null);
  const [recentTransactions, setRecentTransactions] = useState<Transaction[]>([]);

  // Fetch User Profile Data
  useEffect(() => {
    const fetchUserProfile = async () => {
      try {
        const response = await axiosInstance.get<User>("/users/profile");
        setUser(response.data);
      } catch (error: any) {
        console.error("❌ Error fetching user profile:", error?.response?.data || error?.message);
      }
    };
    fetchUserProfile();
  }, []);

  // Logout
  const handleLogout = (): void => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    navigate("/");
  };

  // Delete Account
  const handleDeleteAccount = async (): Promise<void> => {
    const confirmDelete = window.confirm(
      "Are you sure you want to delete your account? This action cannot be undone."
    );
    if (!confirmDelete || !user) return;

    try {
      const userId = user.userId;
      await axiosInstance.delete(`/users/remove/${userId}`);

      alert("✅ Your account has been deleted.");
      localStorage.removeItem("token");
      localStorage.removeItem("role");
      window.location.href = "/login";
    } catch (error: any) {
      console.error("❌ Error deleting account:", error?.response?.data || error?.message);
      alert("❌ Failed to delete account. Please try again.");
    }
  };

  // Fetch Recent Transactions
  useEffect(() => {
    const fetchRecent = async () => {
      try {
        const profile = await axiosInstance.get<User>("/users/profile");
        const userId = profile.data.userId;

        const res = await axiosInstance.get<Transaction[]>(`/transactions/user/${userId}`);
        const recent = res.data
          .slice() // shallow copy
          .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
          .slice(0, 5);
        setRecentTransactions(recent);
      } catch (err) {
        console.error("❌ Error fetching recent transactions:", err);
      }
    };

    fetchRecent();
  }, []);

  return (
    <div className="dashboard-container">
      {/* Sidebar */}
      <aside className="sidebar">
        <h2>Finance Tracker</h2>

        {/* User Profile Section */}
        <div className="user-profile">
          {/* Profile Picture */}
          <img
            src={"/img/user-2.png"}
            alt="User Profile"
            className="profile-icon"
            onClick={() => setShowProfile(!showProfile)}
          />

          {/* Show Name/Username Below Profile Picture */}
          <h4 className="profile-name">
            {user?.firstName} {user?.lastName || user?.userName}
          </h4>

          {/* Profile Overlay */}
          {showProfile && user && (
            <div className="profile-overlay">
              {/* Close Button (X) */}
              <button
                className="close-profile-btn"
                onClick={() => setShowProfile(false)}
              >
                ✖
              </button>
              <p className="profile-names">
                {user.firstName} {user.lastName}
              </p>
              <p className="profile-info">
                <span>📧</span> {user.email}
              </p>
              <p className="profile-info">
                <span>📞</span> {user.phoneNumber || "No phone number"}
              </p>
              <p className="profile-info">
                <span>🏠</span> {user.address || "No address"}
              </p>

              {/* Edit Profile */}
              <button
                className="edit-profile-btn"
                onClick={() => setShowEditProfile(true)}
              >
                Edit Profile
              </button>
              {/* Delete Account */}
              <button
                className="delete-account-btn"
                onClick={handleDeleteAccount}
              >
                Delete Account
              </button>
            </div>
          )}
        </div>

        {/* Sidebar Navigation */}
        <nav>
          <ul>
            <li>
              <NavLink to="/dashboard">Dashboard</NavLink>
            </li>
            <li>
              <NavLink to="/dashboard/accounts">Accounts</NavLink>
            </li>
            <li>
              <NavLink to="/dashboard/transactions">Transactions</NavLink>
            </li>
            <li>
              <NavLink to="/dashboard/budget">My Budgets</NavLink>
            </li>
            <li>
              <NavLink to="/dashboard/loans">Loans</NavLink>
            </li>
            <li>
              <NavLink to="/dashboard/savings">My Savings Goals</NavLink>
            </li>
            <li>
              <NavLink to="/dashboard/investments">Investments</NavLink>
            </li>
            <li>
              <NavLink to="/dashboard/reports">Reports</NavLink>
            </li>
            <li>
              <NavLink to="/dashboard/subscriptions">Subscriptions</NavLink>
            </li>
          </ul>
        </nav>

        {/* Logout Button at Bottom */}
        <button className="logout-btn" onClick={handleLogout}>
          Logout
        </button>
      </aside>

      {/* Main Content */}
      <main className="main-content">
        {/* Header stays fixed */}
        <header className="top-bar">
          <div className="stats">
            <div className="stat-items">
              <h4>Account Balance</h4>
              <p>$12,345.67</p>
            </div>
            <div className="stat-items">
              <h4>Investments</h4>
              <p>$8,000 / 10,000</p>
            </div>
            <div className="stat-items">
              <h4>Loans</h4>
              <p>$5,000 / 15,000</p>
            </div>
          </div>
        </header>

        {/* Only this part will scroll */}
        <div className="dashboard-scrollable">
          <section className="content">
            <Outlet />
          </section>

          <footer className="bottom-section">
            <div className="recent-transactions">
              <h3 className="recent-transactions-header">Recent Transactions</h3>
              <table className="recent-table">
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Type</th>
                    <th>Amount</th>
                    <th>Account</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {recentTransactions.length === 0 ? (
                    <tr>
                      <td colSpan={5}>No transactions found.</td>
                    </tr>
                  ) : (
                    recentTransactions.map((tx) => (
                      <tr key={tx.transactionId}>
                        <td>{new Date(tx.date).toLocaleDateString()}</td>
                        <td>{tx.transactionType}</td>
                        <td>${tx.amount}</td>
                        <td>{tx.accountName || "N/A"}</td>
                        <td>{tx.status}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            <div className="quick-reports">
              <h3 className="quick-reports-header">Quick Reports</h3>
              <p>📊 Monthly Expenses: $2,500</p>
              <p>📈 Investment Growth: 8%</p>
            </div>
          </footer>
        </div>
      </main>

      {/* Edit Profile Modal (Properly Closeable) */}
      {showEditProfile && (
        <Profile closeModal={() => setShowEditProfile(false)} />
      )}
    </div>
  );
};

export default DashboardLayout;
