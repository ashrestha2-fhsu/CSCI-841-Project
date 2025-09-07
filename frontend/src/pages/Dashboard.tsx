import React, { useState, useEffect } from 'react'
import { userApi, accountApi, roleApi } from '../services/api'
import type { User, Account, Role } from '../types'

const Dashboard: React.FC = () => {
  const [users, setUsers] = useState<User[]>([])
  const [accounts, setAccounts] = useState<Account[]>([])
  const [roles, setRoles] = useState<Role[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true)
        const [usersData, accountsData, rolesData] = await Promise.all([
          userApi.getAll(),
          accountApi.getAll(),
          roleApi.getAll()
        ])
        setUsers(usersData)
        setAccounts(accountsData)
        setRoles(rolesData)
      } catch (err) {
        setError('Failed to fetch dashboard data')
        console.error('Dashboard error:', err)
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [])

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="alert alert-error">
        {error}
      </div>
    )
  }

  const totalBalance = accounts.reduce((sum, account) => sum + account.balance, 0)
  const activeAccounts = accounts.filter(account => account.isActive).length
  const activeUsers = users.length

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">Dashboard</h1>
          <p className="text-xl text-gray-600">
            Overview of your user management system
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div className="bg-white rounded-xl shadow-lg p-6 border border-gray-200">
            <h3 className="text-lg font-semibold mb-2 text-gray-700">Total Users</h3>
            <p className="text-3xl font-bold text-blue-600">{activeUsers}</p>
          </div>

          <div className="bg-white rounded-xl shadow-lg p-6 border border-gray-200">
            <h3 className="text-lg font-semibold mb-2 text-gray-700">Active Accounts</h3>
            <p className="text-3xl font-bold text-green-600">{activeAccounts}</p>
          </div>

          <div className="bg-white rounded-xl shadow-lg p-6 border border-gray-200">
            <h3 className="text-lg font-semibold mb-2 text-gray-700">Total Balance</h3>
            <p className="text-3xl font-bold text-purple-600">
              ${totalBalance.toLocaleString()}
            </p>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div className="bg-white rounded-xl shadow-lg p-6 border border-gray-200">
            <h3 className="text-lg font-semibold mb-4 text-gray-800">Recent Users</h3>
            {users.slice(0, 5).length > 0 ? (
              <div className="space-y-3">
                {users.slice(0, 5).map((user) => (
                  <div key={user.id} className="flex justify-between items-center p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                    <div>
                      <p className="font-medium text-gray-900">{user.firstName} {user.lastName}</p>
                      <p className="text-sm text-gray-600">{user.email}</p>
                    </div>
                    <span className="text-xs text-gray-500 bg-white px-2 py-1 rounded">
                      {new Date(user.createdAt).toLocaleDateString()}
                    </span>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-500 text-center py-4">No users found</p>
            )}
          </div>

          <div className="bg-white rounded-xl shadow-lg p-6 border border-gray-200">
            <h3 className="text-lg font-semibold mb-4 text-gray-800">Account Types</h3>
            <div className="space-y-3">
              {Object.values(['CHECKING', 'SAVINGS', 'CREDIT', 'INVESTMENT']).map((type) => {
                const count = accounts.filter(account => account.accountType === type).length
                return (
                  <div key={type} className="flex justify-between items-center p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                    <span className="capitalize font-medium text-gray-700">{type.toLowerCase()}</span>
                    <span className="font-semibold text-gray-900 bg-white px-3 py-1 rounded-full">{count}</span>
                  </div>
                )
              })}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Dashboard
