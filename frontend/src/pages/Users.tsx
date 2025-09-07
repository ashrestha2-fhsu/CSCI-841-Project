import React, { useState, useEffect } from 'react'
import { userApi } from '../services/api'
import type { User, CreateUserRequest, UpdateUserRequest } from '../types'
import UserForm from '../components/UserForm'
import UserTable from '../components/UserTable'

const Users: React.FC = () => {
  const [users, setUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)
  const [showForm, setShowForm] = useState(false)
  const [editingUser, setEditingUser] = useState<User | null>(null)

  const fetchUsers = async () => {
    try {
      setLoading(true)
      setError(null)
      const usersData = await userApi.getAll()
      setUsers(usersData)
    } catch (err) {
      setError('Failed to fetch users')
      console.error('Users fetch error:', err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchUsers()
  }, [])

  const handleCreateUser = async (userData: CreateUserRequest) => {
    try {
      const newUser = await userApi.create(userData)
      setUsers([...users, newUser])
      setSuccess('User created successfully')
      setShowForm(false)
      setTimeout(() => setSuccess(null), 3000)
    } catch (err) {
      setError('Failed to create user')
      console.error('Create user error:', err)
    }
  }

  const handleUpdateUser = async (id: number, userData: UpdateUserRequest) => {
    try {
      const updatedUser = await userApi.update(id, userData)
      setUsers(users.map(user => user.id === id ? updatedUser : user))
      setSuccess('User updated successfully')
      setEditingUser(null)
      setTimeout(() => setSuccess(null), 3000)
    } catch (err) {
      setError('Failed to update user')
      console.error('Update user error:', err)
    }
  }

  const handleDeleteUser = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this user?')) {
      return
    }

    try {
      await userApi.delete(id)
      setUsers(users.filter(user => user.id !== id))
      setSuccess('User deleted successfully')
      setTimeout(() => setSuccess(null), 3000)
    } catch (err) {
      setError('Failed to delete user')
      console.error('Delete user error:', err)
    }
  }

  const handleEditUser = (user: User) => {
    setEditingUser(user)
    setShowForm(true)
  }

  const handleCloseForm = () => {
    setShowForm(false)
    setEditingUser(null)
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
        <h1 className="page-title">Users</h1>
        <p className="page-description">
          Manage user accounts and information
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
          <h2 className="text-xl font-semibold">User List</h2>
          <button
            onClick={() => setShowForm(true)}
            className="btn btn-primary"
          >
            Add New User
          </button>
        </div>

        <UserTable
          users={users}
          onEdit={handleEditUser}
          onDelete={handleDeleteUser}
        />
      </div>

      {showForm && (
        <UserForm
          user={editingUser}
          onSubmit={editingUser ? 
            (data) => handleUpdateUser(editingUser.id, data) : 
            handleCreateUser
          }
          onCancel={handleCloseForm}
        />
      )}
    </div>
  )
}

export default Users
