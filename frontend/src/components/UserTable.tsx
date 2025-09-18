import React from 'react'
import type { User } from '../types'

interface UserTableProps {
  users: User[]
  onEdit: (user: User) => void
  onDelete: (id: number) => void
}

const UserTable: React.FC<UserTableProps> = ({ users, onEdit, onDelete }) => {
  if (users.length === 0) {
    return (
      <div className="text-center py-8 text-gray-500">
        No users found. Create your first user to get started.
      </div>
    )
  }

  return (
    <div className="overflow-x-auto">
      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>Name</th>
            <th>Created</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              <td>{user.id}</td>
              <td className="font-medium">{user.username}</td>
              <td>{user.email}</td>
              <td>{user.firstName} {user.lastName}</td>
              <td>{new Date(user.createdAt).toLocaleDateString()}</td>
              <td>
                <div className="flex gap-2">
                  <button
                    onClick={() => onEdit(user)}
                    className="btn btn-secondary"
                    style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem' }}
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => onDelete(user.id)}
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

export default UserTable
