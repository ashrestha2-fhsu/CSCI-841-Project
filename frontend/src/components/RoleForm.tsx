import React, { useState, useEffect } from 'react'
import type { Role, CreateRoleRequest, UpdateRoleRequest, RoleType, RolePermission } from '../types'

interface RoleFormProps {
  role?: Role | null
  onSubmit: (data: CreateRoleRequest | UpdateRoleRequest) => void
  onCancel: () => void
}

const RoleForm: React.FC<RoleFormProps> = ({ role, onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    roleName: '',
    roleType: 'USER' as RoleType,
    permissions: [] as RolePermission[]
  })

  useEffect(() => {
    if (role) {
      setFormData({
        roleName: role.roleName,
        roleType: role.roleType,
        permissions: role.permissions
      })
    }
  }, [role])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handlePermissionChange = (permission: RolePermission, checked: boolean) => {
    setFormData(prev => ({
      ...prev,
      permissions: checked
        ? [...prev.permissions, permission]
        : prev.permissions.filter(p => p !== permission)
    }))
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    onSubmit(formData)
  }

  const allPermissions: { value: RolePermission; label: string }[] = [
    { value: RolePermission.READ_USERS, label: 'Read Users' },
    { value: RolePermission.WRITE_USERS, label: 'Write Users' },
    { value: RolePermission.DELETE_USERS, label: 'Delete Users' },
    { value: RolePermission.READ_ACCOUNTS, label: 'Read Accounts' },
    { value: RolePermission.WRITE_ACCOUNTS, label: 'Write Accounts' },
    { value: RolePermission.DELETE_ACCOUNTS, label: 'Delete Accounts' },
    { value: RolePermission.READ_ROLES, label: 'Read Roles' },
    { value: RolePermission.WRITE_ROLES, label: 'Write Roles' },
    { value: RolePermission.DELETE_ROLES, label: 'Delete Roles' },
    { value: RolePermission.ADMIN_ACCESS, label: 'Admin Access' }
  ]

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-lg max-h-[90vh] overflow-y-auto">
        <h2 className="text-xl font-semibold mb-4">
          {role ? 'Edit Role' : 'Add New Role'}
        </h2>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="form-group">
            <label htmlFor="roleName" className="form-label">
              Role Name
            </label>
            <input
              type="text"
              id="roleName"
              name="roleName"
              value={formData.roleName}
              onChange={handleChange}
              className="form-input"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="roleType" className="form-label">
              Role Type
            </label>
            <select
              id="roleType"
              name="roleType"
              value={formData.roleType}
              onChange={handleChange}
              className="form-select"
              required
            >
              <option value="USER">User</option>
              <option value="ADMIN">Admin</option>
              <option value="MANAGER">Manager</option>
              <option value="CUSTOMER_SERVICE">Customer Service</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Permissions</label>
            <div className="grid grid-cols-2 gap-2">
              {allPermissions.map((permission) => (
                <label key={permission.value} className="flex items-center">
                  <input
                    type="checkbox"
                    checked={formData.permissions.includes(permission.value)}
                    onChange={(e) => handlePermissionChange(permission.value, e.target.checked)}
                    className="mr-2"
                  />
                  <span className="text-sm">{permission.label}</span>
                </label>
              ))}
            </div>
          </div>

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
              {role ? 'Update' : 'Create'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default RoleForm
