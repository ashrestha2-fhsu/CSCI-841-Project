import React from 'react'
import type { Account, User } from '../types'

interface AccountTableProps {
  accounts: Account[]
  users: User[]
  onEdit: (account: Account) => void
  onDelete: (id: number) => void
}

const AccountTable: React.FC<AccountTableProps> = ({ accounts, users, onEdit, onDelete }) => {
  const getUserName = (userId?: number) => {
    if (!userId) return 'N/A'
    const user = users.find(u => u.id === userId)
    return user ? `${user.firstName} ${user.lastName}` : 'Unknown User'
  }

  if (accounts.length === 0) {
    return (
      <div className="text-center py-8 text-gray-500">
        No accounts found. Create your first account to get started.
      </div>
    )
  }

  return (
    <div className="overflow-x-auto">
      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Account Number</th>
            <th>Type</th>
            <th>Balance</th>
            <th>User</th>
            <th>Status</th>
            <th>Created</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {accounts.map((account) => (
            <tr key={account.id}>
              <td>{account.id}</td>
              <td className="font-medium">{account.accountNumber}</td>
              <td>
                <span className="px-2 py-1 bg-blue-100 text-blue-800 rounded text-xs">
                  {account.accountType}
                </span>
              </td>
              <td className="font-semibold">
                ${account.balance.toLocaleString()}
              </td>
              <td>{getUserName(account.user?.id)}</td>
              <td>
                <span className={`px-2 py-1 rounded text-xs ${
                  account.isActive 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-red-100 text-red-800'
                }`}>
                  {account.isActive ? 'Active' : 'Inactive'}
                </span>
              </td>
              <td>{new Date(account.createdAt).toLocaleDateString()}</td>
              <td>
                <div className="flex gap-2">
                  <button
                    onClick={() => onEdit(account)}
                    className="btn btn-secondary"
                    style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem' }}
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => onDelete(account.id)}
                    className="btn btn-danger"
                    style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem' }}
                  >
                    Delete
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default AccountTable
