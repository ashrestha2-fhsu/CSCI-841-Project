
// src/pages/Accounts.tsx
import { useState, useEffect } from "react";
import axiosInstance from "../services/axiosInstance.ts";
import AccountForm from "../pages/accountForm.tsx";
import AllAccountsModal from "../pages/accountDetails.tsx";

import "../styles/accounts.css";

interface Account {
  accountId: number;
  name: string;
  currency: string;
  balance: number;
  institutionName?: string;
  accountNumber?: string;
  interestRate?: number;
  dateCreated: string;
  dateUpdated: string;
}

interface AccountFormData {
  name: string;
  type: string;
  balance: string | number;
  currency: string;
  institutionName: string;
  accountNumber: string;
  interestRate: string | number;
}

const Accounts: React.FC = () => {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [expandedAccount, setExpandedAccount] = useState<number | null>(null);
  const [userId, setUserId] = useState<number | null>(null);
  const [showAddModal, setShowAddModal] = useState<boolean>(false);
  const [showAllModal, setShowAllModal] = useState<boolean>(false);
  const [editAccount, setEditAccount] = useState<Account | null>(null);

  // ✅ Fetch user profile
  useEffect(() => {
    const fetchUserProfile = async () => {
      try {
        const response = await axiosInstance.get<{ userId: number }>("/users/profile");
        setUserId(response.data.userId);
      } catch (error: any) {
        console.error("❌ Error fetching user profile:", error?.response?.data || error?.message);
      }
    };
    fetchUserProfile();
  }, []);

  // ✅ Fetch accounts
  const fetchAccounts = async () => {
    try {
      const response = await axiosInstance.get<Account[]>(`/accounts/user/${userId}`);
      setAccounts(response.data);
    } catch (error: any) {
      console.error("❌ Error fetching accounts:", error?.response?.data || error?.message);
    }
  };

  useEffect(() => {
    if (userId) fetchAccounts();
  }, [userId]);

  // ✅ Toggle card details
  const toggleDetails = (accountId: number) => {
    setExpandedAccount((prev) => (prev === accountId ? null : accountId));
  };

  // ✅ Add or Edit Account
  const handleSaveAccount = async (formData: AccountFormData) => {
    try {
      if (editAccount) {
        await axiosInstance.put(`/accounts/${editAccount.accountId}`, formData);
      } else {
        await axiosInstance.post(`/accounts/create/${userId}`, formData);
      }

      setShowAddModal(false);
      setEditAccount(null);
      fetchAccounts();
    } catch (error: any) {
      console.error("❌ Error saving account:", error?.response?.data || error?.message);
    }
  };

  return (
    <div className="accounts-container">
      <h2 className="header">My Accounts</h2>

      <div className="account-actions">
        <button
          onClick={() => {
            setShowAddModal(false); // Close if open
            setEditAccount(null); // Clear old data
            setTimeout(() => {
              setShowAddModal(true); // Then reopen cleanly
            }, 0);
          }}
        >
          Add New
        </button>
        <button onClick={() => setShowAllModal(true)}>View All</button>
      </div>

      <div className="account-list">
        {accounts.length > 0 ? (
          accounts.map((account) => (
            <div className="account-card" key={account.accountId}>
              <h3>{account.name}</h3>
              <p>
                Balance: {account.currency} {account.balance.toFixed(2)}
              </p>
              <div className="card-btn">
                <button onClick={() => toggleDetails(account.accountId)}>
                  {expandedAccount === account.accountId ? "Hide Details" : "View Details"}
                </button>
              </div>

              {expandedAccount === account.accountId && (
                <div className="account-details">
                  <table>
                    <tbody>
                      <tr>
                        <td>
                          <strong>Institution:</strong>
                        </td>
                        <td>{account.institutionName || "N/A"}</td>
                      </tr>
                      <tr>
                        <td>
                          <strong>Account Number:</strong>
                        </td>
                        <td>{account.accountNumber || "N/A"}</td>
                      </tr>
                      <tr>
                        <td>
                          <strong>Interest Rate:</strong>
                        </td>
                        <td>{account.interestRate ? `${account.interestRate}%` : "N/A"}</td>
                      </tr>
                      <tr>
                        <td>
                          <strong>Created On:</strong>
                        </td>
                        <td>{new Date(account.dateCreated).toLocaleDateString()}</td>
                      </tr>
                      <tr>
                        <td>
                          <strong>Last Updated:</strong>
                        </td>
                        <td>{new Date(account.dateUpdated).toLocaleDateString()}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          ))
        ) : (
          <p>No accounts found. Create one to get started!</p>
        )}
      </div>

      {/* ✅ Account Form Modal */}
      <AccountForm
        isOpen={showAddModal}
        onClose={() => {
          setEditAccount(null);
          setShowAddModal(false);
        }}
        onSubmit={handleSaveAccount}
        initialData={editAccount as any}
      />

      {/* ✅ All Accounts Modal */}
      <AllAccountsModal
        isOpen={showAllModal}
        onClose={() => setShowAllModal(false)}
        userId={userId as any}
        onEdit={(acc: any) => {
          setEditAccount(acc);
          setShowAllModal(false);
          setShowAddModal(true);
        }}
      />
    </div>
  );
};

export default Accounts;

