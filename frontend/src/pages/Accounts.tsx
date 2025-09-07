import React, { useState, useEffect } from 'react'
import { accountApi, userApi } from '../services/api'
import type { Account, User, CreateAccountRequest, UpdateAccountRequest } from '../types'
import AccountForm from '../components/AccountForm'
import AccountTable from '../components/AccountTable'

const Accounts: React.FC = () => {
  const [accounts, setAccounts] = useState<Account[]>([])
  const [users, setUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)
  const [showForm, setShowForm] = useState(false)
  const [editingAccount, setEditingAccount] = useState<Account | null>(null)

  const fetchData = async () => {
    try {
      setLoading(true)
      setError(null)
      const [accountsData, usersData] = await Promise.all([
        accountApi.getAll(),
        userApi.getAll()
      ])
      setAccounts(accountsData)
      setUsers(usersData)
    } catch (err) {
      setError('Failed to fetch accounts data')
      console.error('Accounts fetch error:', err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchData()
  }, [])

  const handleCreateAccount = async (accountData: CreateAccountRequest) => {
    try {
      const newAccount = await accountApi.create(accountData)
      setAccounts([...accounts, newAccount])
      setSuccess('Account created successfully')
      setShowForm(false)
      setTimeout(() => setSuccess(null), 3000)
    } catch (err) {
      setError('Failed to create account')
      console.error('Create account error:', err)
    }
  }

  const handleUpdateAccount = async (id: number, accountData: UpdateAccountRequest) => {
    try {
      const updatedAccount = await accountApi.update(id, accountData)
      setAccounts(accounts.map(account => account.id === id ? updatedAccount : account))
      setSuccess('Account updated successfully')
      setEditingAccount(null)
      setTimeout(() => setSuccess(null), 3000)
    } catch (err) {
      setError('Failed to update account')
      console.error('Update account error:', err)
    }
  }

  const handleDeleteAccount = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this account?')) {
      return
    }

    try {
      await accountApi.delete(id)
      setAccounts(accounts.filter(account => account.id !== id))
      setSuccess('Account deleted successfully')
      setTimeout(() => setSuccess(null), 3000)
    } catch (err) {
      setError('Failed to delete account')
      console.error('Delete account error:', err)
    }
  }

  const handleEditAccount = (account: Account) => {
    setEditingAccount(account)
    setShowForm(true)
  }

  const handleCloseForm = () => {
    setShowForm(false)
    setEditingAccount(null)
  }

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
      </div>
    )
  }

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Accounts</h1>
        <p className="page-description">
          Manage user accounts and financial information
        </p>
      </div>

      {error && (
        <div className="alert alert-error">
          {error}
        </div>
      )}

      {success && (
        <div className="alert alert-success">
          {success}
        </div>
      )}

      <div className="card">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold">Account List</h2>
          <button
            onClick={() => setShowForm(true)}
            className="btn btn-primary"
          >
            Add New Account
          </button>
        </div>

        <AccountTable
          accounts={accounts}
          users={users}
          onEdit={handleEditAccount}
          onDelete={handleDeleteAccount}
        />
      </div>

      {showForm && (
        <AccountForm
          account={editingAccount}
          users={users}
          onSubmit={editingAccount ? 
            (data) => handleUpdateAccount(editingAccount.id, data) : 
            handleCreateAccount
          }
          onCancel={handleCloseForm}
        />
      )}
    </div>
  )
}

export default Accounts
