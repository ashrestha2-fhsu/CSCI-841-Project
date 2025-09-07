import React from 'react'
import type { Role } from '../types'

interface RoleTableProps {
  roles: Role[]
  onEdit: (role: Role) => void
  onDelete: (id: number) => void
}

const RoleTable: React.FC<RoleTableProps> = ({ roles, onEdit, onDelete }) => {
  if (roles.length === 0) {
    return (
      <div className="text-center py-8 text-gray-500">
        No roles found. Create your first role to get started.
      </div>
    )
  }

  return (
    <div className="overflow-x-auto">
      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Role Name</th>
            <th>Type</th>
            <th>Permissions</th>
            <th>Users</th>
            <th>Created</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {roles.map((role) => (
            <tr key={role.id}>
              <td>{role.id}</td>
              <td className="font-medium">{role.roleName}</td>
              <td>
                <span className="px-2 py-1 bg-purple-100 text-purple-800 rounded text-xs">
                  {role.roleType}
                </span>
              </td>
              <td>
                <div className="flex flex-wrap gap-1">
                  {role.permissions.slice(0, 3).map((permission) => (
                    <span
                      key={permission}
                      className="px-2 py-1 bg-gray-100 text-gray-700 rounded text-xs"
                    >
                      {permission.replace(/_/g, ' ').toLowerCase()}
                    </span>
                  ))}
                  {role.permissions.length > 3 && (
                    <span className="px-2 py-1 bg-gray-200 text-gray-600 rounded text-xs">
                      +{role.permissions.length - 3} more
                    </span>
                  )}
                </div>
              </td>
              <td>
                <span className="px-2 py-1 bg-blue-100 text-blue-800 rounded text-xs">
                  {role.users?.length || 0} users
                </span>
              </td>
              <td>{new Date(role.createdAt).toLocaleDateString()}</td>
              <td>
                <div className="flex gap-2">
                  <button
                    onClick={() => onEdit(role)}
                    className="btn btn-secondary"
                    style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem' }}
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => onDelete(role.id)}
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

export default RoleTable
