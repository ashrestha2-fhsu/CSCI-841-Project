import React, { useState, useEffect } from 'react'
import { roleApi } from '../services/api'
import type { Role, CreateRoleRequest, UpdateRoleRequest } from '../types'
import RoleForm from '../components/RoleForm'
import RoleTable from '../components/RoleTable'

const Roles: React.FC = () => {
  const [roles, setRoles] = useState<Role[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)
  const [showForm, setShowForm] = useState(false)
  const [editingRole, setEditingRole] = useState<Role | null>(null)

  const fetchRoles = async () => {
    try {
      setLoading(true)
      setError(null)
      const rolesData = await roleApi.getAll()
      setRoles(rolesData)
    } catch (err) {
      setError('Failed to fetch roles')
      console.error('Roles fetch error:', err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchRoles()
  }, [])

  const handleCreateRole = async (roleData: CreateRoleRequest) => {
    try {
      const newRole = await roleApi.create(roleData)
      setRoles([...roles, newRole])
      setSuccess('Role created successfully')
      setShowForm(false)
      setTimeout(() => setSuccess(null), 3000)
    } catch (err) {
      setError('Failed to create role')
      console.error('Create role error:', err)
    }
  }

  const handleUpdateRole = async (id: number, roleData: UpdateRoleRequest) => {
    try {
      const updatedRole = await roleApi.update(id, roleData)
      setRoles(roles.map(role => role.id === id ? updatedRole : role))
      setSuccess('Role updated successfully')
      setEditingRole(null)
      setTimeout(() => setSuccess(null), 3000)
    } catch (err) {
      setError('Failed to update role')
      console.error('Update role error:', err)
    }
  }

  const handleDeleteRole = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this role?')) {
      return
    }

    try {
      await roleApi.delete(id)
      setRoles(roles.filter(role => role.id !== id))
      setSuccess('Role deleted successfully')
      setTimeout(() => setSuccess(null), 3000)
    } catch (err) {
      setError('Failed to delete role')
      console.error('Delete role error:', err)
    }
  }

  const handleEditRole = (role: Role) => {
    setEditingRole(role)
    setShowForm(true)
  }

  const handleCloseForm = () => {
    setShowForm(false)
    setEditingRole(null)
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
        <h1 className="page-title">Roles</h1>
        <p className="page-description">
          Manage user roles and permissions
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
          <h2 className="text-xl font-semibold">Role List</h2>
          <button
            onClick={() => setShowForm(true)}
            className="btn btn-primary"
          >
            Add New Role
          </button>
        </div>

        <RoleTable
          roles={roles}
          onEdit={handleEditRole}
          onDelete={handleDeleteRole}
        />
      </div>

      {showForm && (
        <RoleForm
          role={editingRole}
          onSubmit={editingRole ? 
            (data) => handleUpdateRole(editingRole.id, data) : 
            handleCreateRole
          }
          onCancel={handleCloseForm}
        />
      )}
    </div>
  )
}

export default Roles
