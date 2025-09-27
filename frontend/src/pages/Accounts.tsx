
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



// import React, { useState, useEffect } from 'react'
// import { accountApi, userApi } from '../services/api'
// import type { Account, User, CreateAccountRequest, UpdateAccountRequest } from '../types'
// import AccountForm from '../components/AccountForm'
// import AccountTable from '../components/AccountTable'

// const Accounts: React.FC = () => {
//   const [accounts, setAccounts] = useState<Account[]>([])
//   const [users, setUsers] = useState<User[]>([])
//   const [loading, setLoading] = useState(true)
//   const [error, setError] = useState<string | null>(null)
//   const [success, setSuccess] = useState<string | null>(null)
//   const [showForm, setShowForm] = useState(false)
//   const [editingAccount, setEditingAccount] = useState<Account | null>(null)

//   const fetchData = async () => {
//     try {
//       setLoading(true)
//       setError(null)
//       const [accountsData, usersData] = await Promise.all([
//         accountApi.getAll(),
//         userApi.getAll()
//       ])
//       setAccounts(accountsData)
//       setUsers(usersData)
//     } catch (err) {
//       setError('Failed to fetch accounts data')
//       console.error('Accounts fetch error:', err)
//     } finally {
//       setLoading(false)
//     }
//   }

//   useEffect(() => {
//     fetchData()
//   }, [])

//   const handleCreateAccount = async (accountData: CreateAccountRequest) => {
//     try {
//       const newAccount = await accountApi.create(accountData)
//       setAccounts([...accounts, newAccount])
//       setSuccess('Account created successfully')
//       setShowForm(false)
//       setTimeout(() => setSuccess(null), 3000)
//     } catch (err) {
//       setError('Failed to create account')
//       console.error('Create account error:', err)
//     }
//   }

//   const handleUpdateAccount = async (id: number, accountData: UpdateAccountRequest) => {
//     try {
//       const updatedAccount = await accountApi.update(id, accountData)
//       setAccounts(accounts.map(account => account.id === id ? updatedAccount : account))
//       setSuccess('Account updated successfully')
//       setEditingAccount(null)
//       setTimeout(() => setSuccess(null), 3000)
//     } catch (err) {
//       setError('Failed to update account')
//       console.error('Update account error:', err)
//     }
//   }

//   const handleDeleteAccount = async (id: number) => {
//     if (!window.confirm('Are you sure you want to delete this account?')) {
//       return
//     }

//     try {
//       await accountApi.delete(id)
//       setAccounts(accounts.filter(account => account.id !== id))
//       setSuccess('Account deleted successfully')
//       setTimeout(() => setSuccess(null), 3000)
//     } catch (err) {
//       setError('Failed to delete account')
//       console.error('Delete account error:', err)
//     }
//   }

//   const handleEditAccount = (account: Account) => {
//     setEditingAccount(account)
//     setShowForm(true)
//   }

//   const handleCloseForm = () => {
//     setShowForm(false)
//     setEditingAccount(null)
//   }

//   if (loading) {
//     return (
//       <div className="loading">
//         <div className="spinner"></div>
//       </div>
//     )
//   }

//   return (
//     <div>
//       <div className="page-header">
//         <h1 className="page-title">Accounts</h1>
//         <p className="page-description">
//           Manage user accounts and financial information
//         </p>
//       </div>

//       {error && (
//         <div className="alert alert-error">
//           {error}
//         </div>
//       )}

//       {success && (
//         <div className="alert alert-success">
//           {success}
//         </div>
//       )}

//       <div className="card">
//         <div className="flex justify-between items-center mb-4">
//           <h2 className="text-xl font-semibold">Account List</h2>
//           <button
//             onClick={() => setShowForm(true)}
//             className="btn btn-primary"
//           >
//             Add New Account
//           </button>
//         </div>

//         <AccountTable
//           accounts={accounts}
//           users={users}
//           onEdit={handleEditAccount}
//           onDelete={handleDeleteAccount}
//         />
//       </div>

//       {showForm && (
//         <AccountForm
//           account={editingAccount}
//           users={users}
//           onSubmit={editingAccount ? 
//             (data) => handleUpdateAccount(editingAccount.id, data) : 
//             handleCreateAccount
//           }
//           onCancel={handleCloseForm}
//         />
//       )}
//     </div>
//   )
// }

// export default Accounts
