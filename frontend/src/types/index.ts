export interface User {
  id: number
  username: string
  email: string
  firstName: string
  lastName: string
  createdAt: string
  updatedAt: string
  accounts?: Account[]
  roles?: Role[]
}

export interface Account {
  id: number
  accountNumber: string
  accountType: AccountType
  balance: number
  isActive: boolean
  createdAt: string
  updatedAt: string
  user?: User
}

export interface Role {
  id: number
  roleName: string
  roleType: RoleType
  permissions: RolePermission[]
  createdAt: string
  updatedAt: string
  users?: User[]
}

export enum AccountType {
  CHECKING = 'CHECKING',
  SAVINGS = 'SAVINGS',
  CREDIT = 'CREDIT',
  INVESTMENT = 'INVESTMENT'
}

export enum RoleType {
  ADMIN = 'ADMIN',
  USER = 'USER',
  MANAGER = 'MANAGER',
  CUSTOMER_SERVICE = 'CUSTOMER_SERVICE'
}

export enum RolePermission {
  READ_USERS = 'READ_USERS',
  WRITE_USERS = 'WRITE_USERS',
  DELETE_USERS = 'DELETE_USERS',
  READ_ACCOUNTS = 'READ_ACCOUNTS',
  WRITE_ACCOUNTS = 'WRITE_ACCOUNTS',
  DELETE_ACCOUNTS = 'DELETE_ACCOUNTS',
  READ_ROLES = 'READ_ROLES',
  WRITE_ROLES = 'WRITE_ROLES',
  DELETE_ROLES = 'DELETE_ROLES',
  ADMIN_ACCESS = 'ADMIN_ACCESS'
}

export interface CreateUserRequest {
  username: string
  email: string
  firstName: string
  lastName: string
}

export interface UpdateUserRequest {
  username?: string
  email?: string
  firstName?: string
  lastName?: string
}

export interface CreateAccountRequest {
  accountNumber: string
  accountType: AccountType
  balance: number
  userId: number
}

export interface UpdateAccountRequest {
  accountNumber?: string
  accountType?: AccountType
  balance?: number
  isActive?: boolean
}

export interface CreateRoleRequest {
  roleName: string
  roleType: RoleType
  permissions: RolePermission[]
}

export interface UpdateRoleRequest {
  roleName?: string
  roleType?: RoleType
  permissions?: RolePermission[]
}

export interface ApiResponse<T> {
  data: T
  message?: string
  success: boolean
}

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}
