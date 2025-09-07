import React, { useState, useEffect } from 'react'
import type { Account, User, CreateAccountRequest, UpdateAccountRequest, AccountType } from '../types'

interface AccountFormProps {
  account?: Account | null
  users: User[]
  onSubmit: (data: CreateAccountRequest | UpdateAccountRequest) => void
  onCancel: () => void
}

const AccountForm: React.FC<AccountFormProps> = ({ account, users, onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    accountNumber: '',
    accountType: 'CHECKING' as AccountType,
    balance: 0,
    userId: 0,
    isActive: true
  })

  useEffect(() => {
    if (account) {
      setFormData({
        accountNumber: account.accountNumber,
        accountType: account.accountType,
        balance: account.balance,
        userId: account.user?.id || 0,
        isActive: account.isActive
      })
    }
  }, [account])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: type === 'number' ? Number(value) : 
              type === 'checkbox' ? (e.target as HTMLInputElement).checked :
              value
    }))
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (account) {
      // For updates, exclude userId and isActive if not changing
      const { userId, ...updateData } = formData
      onSubmit(updateData)
    } else {
      // For creation, include userId
      onSubmit(formData as CreateAccountRequest)
    }
  }

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-md">
        <h2 className="text-xl font-semibold mb-4">
          {account ? 'Edit Account' : 'Add New Account'}
        </h2>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="form-group">
            <label htmlFor="accountNumber" className="form-label">
              Account Number
            </label>
            <input
              type="text"
              id="accountNumber"
              name="accountNumber"
              value={formData.accountNumber}
              onChange={handleChange}
              className="form-input"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="accountType" className="form-label">
              Account Type
            </label>
            <select
              id="accountType"
              name="accountType"
              value={formData.accountType}
              onChange={handleChange}
              className="form-select"
              required
            >
              <option value="CHECKING">Checking</option>
              <option value="SAVINGS">Savings</option>
              <option value="CREDIT">Credit</option>
              <option value="INVESTMENT">Investment</option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="balance" className="form-label">
              Balance
            </label>
            <input
              type="number"
              id="balance"
              name="balance"
              value={formData.balance}
              onChange={handleChange}
              className="form-input"
              step="0.01"
              min="0"
              required
            />
          </div>

          {!account && (
            <div className="form-group">
              <label htmlFor="userId" className="form-label">
                User
              </label>
              <select
                id="userId"
                name="userId"
                value={formData.userId}
                onChange={handleChange}
                className="form-select"
                required
              >
                <option value={0}>Select a user</option>
                {users.map((user) => (
                  <option key={user.id} value={user.id}>
                    {user.firstName} {user.lastName} ({user.email})
                  </option>
                ))}
              </select>
            </div>
          )}

          {account && (
            <div className="form-group">
              <label className="flex items-center">
                <input
                  type="checkbox"
                  name="isActive"
                  checked={formData.isActive}
                  onChange={handleChange}
                  className="mr-2"
                />
                Active Account
              </label>
            </div>
          )}

          <div className="flex gap-2 justify-end">
            <button
              type="button"
              onClick={onCancel}
              className="btn btn-secondary"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
            >
              {account ? 'Update' : 'Create'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default AccountForm
