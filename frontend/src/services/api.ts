// src/services/api.ts
import axiosInstance from "./axiosInstance";
import type {
  User,
  Account,
  Role,
  CreateUserRequest,
  UpdateUserRequest,
  CreateAccountRequest,
  UpdateAccountRequest,
  CreateRoleRequest,
  UpdateRoleRequest,
} from "../types";

const api = axiosInstance;

/* -------- Users -------- */
export const userApi = {
  profile: async (): Promise<User> => {
    const res = await api.get<User>("/users/profile");
    return res.data;
  },
  getAll: async (): Promise<User[]> => {
    const res = await api.get<User[]>("/users");
    return res.data;
  },
  getById: async (id: number): Promise<User> => {
    const res = await api.get<User>(`/users/${id}`);
    return res.data;
  },
  create: async (payload: CreateUserRequest): Promise<User> => {
    const res = await api.post<User>("/users", payload);
    return res.data;
  },
  update: async (id: number, payload: UpdateUserRequest): Promise<User> => {
    const res = await api.put<User>(`/users/${id}`, payload);
    return res.data;
  },
  delete: async (id: number): Promise<void> => {
    await api.delete(`/users/${id}`);
  },
};

/* -------- Accounts -------- */
export const accountApi = {
  // ✅ Use the endpoints your controller actually exposes
  getByUserId: async (userId: number): Promise<Account[]> => {
    const res = await api.get<Account[]>(`/accounts/user/${userId}`);
    return res.data;
  },
  getAllForUserIncludingDeleted: async (userId: number): Promise<Account[]> => {
    const res = await api.get<Account[]>(`/accounts/user/${userId}/all`);
    return res.data;
  },
  createForUser: async (userId: number, payload: CreateAccountRequest): Promise<Account> => {
    const res = await api.post<Account>(`/accounts/create/${userId}`, payload);
    return res.data;
  },
  getById: async (id: number): Promise<Account> => {
    const res = await api.get<Account>(`/accounts/${id}`);
    return res.data;
  },
  update: async (id: number, payload: UpdateAccountRequest): Promise<Account> => {
    const res = await api.put<Account>(`/accounts/${id}`, payload);
    return res.data;
  },
  delete: async (id: number): Promise<void> => {
    await api.delete(`/accounts/${id}`);
  },
  restore: async (id: number): Promise<void> => {
    await api.put(`/accounts/${id}/restore`);
  },
};

/* -------- Roles (AdminController) -------- */
export const roleApi = {
  getAll: async (): Promise<Role[]> => {
    const res = await api.get<Role[]>("/admin/roles");
    return res.data;
  },
  upsert: async (payload: CreateRoleRequest | UpdateRoleRequest): Promise<Role> => {
    const res = await api.post<Role>("/admin/roles", payload);
    return res.data;
  },
  delete: async (roleId: string): Promise<void> => {
    await api.delete(`/admin/roles/${roleId}`);
  },
};

export default api;


// import axios from 'axios'
// import type {
//   User,
//   Account,
//   Role,
//   CreateUserRequest,
//   UpdateUserRequest,
//   CreateAccountRequest,
//   UpdateAccountRequest,
//   CreateRoleRequest,
//   UpdateRoleRequest,
//   ApiResponse,
//   PaginatedResponse
// } from '../types'

// const API_BASE_URL = '/api'

// const api = axios.create({
//   baseURL: API_BASE_URL,
//   headers: {
//     'Content-Type': 'application/json',
//   },
// })

// // Request interceptor
// api.interceptors.request.use(
//   (config) => {
//     // Add any auth tokens here if needed
//     return config
//   },
//   (error) => {
//     return Promise.reject(error)
//   }
// )

// // Response interceptor
// api.interceptors.response.use(
//   (response) => response,
//   (error) => {
//     console.error('API Error:', error.response?.data || error.message)
//     return Promise.reject(error)
//   }
// )

// // User API
// export const userApi = {
//   getAll: async (): Promise<User[]> => {
//     const response = await api.get<User[]>('/users')
//     return response.data
//   },

//   getById: async (id: number): Promise<User> => {
//     const response = await api.get<User>(`/users/${id}`)
//     return response.data
//   },

//   create: async (userData: CreateUserRequest): Promise<User> => {
//     const response = await api.post<User>('/users', userData)
//     return response.data
//   },

//   update: async (id: number, userData: UpdateUserRequest): Promise<User> => {
//     const response = await api.put<User>(`/users/${id}`, userData)
//     return response.data
//   },

//   delete: async (id: number): Promise<void> => {
//     await api.delete(`/users/${id}`)
//   }
// }

// // Account API
// export const accountApi = {
//   getAll: async (): Promise<Account[]> => {
//     const response = await api.get<Account[]>('/accounts')
//     return response.data
//   },

//   getById: async (id: number): Promise<Account> => {
//     const response = await api.get<Account>(`/accounts/${id}`)
//     return response.data
//   },

//   create: async (accountData: CreateAccountRequest): Promise<Account> => {
//     const response = await api.post<Account>('/accounts', accountData)
//     return response.data
//   },

//   update: async (id: number, accountData: UpdateAccountRequest): Promise<Account> => {
//     const response = await api.put<Account>(`/accounts/${id}`, accountData)
//     return response.data
//   },

//   delete: async (id: number): Promise<void> => {
//     await api.delete(`/accounts/${id}`)
//   },

//   getByUserId: async (userId: number): Promise<Account[]> => {
//     const response = await api.get<Account[]>(`/accounts/user/${userId}`)
//     return response.data
//   }
// }

// // Role API
// export const roleApi = {
//   getAll: async (): Promise<Role[]> => {
//     const response = await api.get<Role[]>('/roles')
//     return response.data
//   },

//   getById: async (id: number): Promise<Role> => {
//     const response = await api.get<Role>(`/roles/${id}`)
//     return response.data
//   },

//   create: async (roleData: CreateRoleRequest): Promise<Role> => {
//     const response = await api.post<Role>('/roles', roleData)
//     return response.data
//   },

//   update: async (id: number, roleData: UpdateRoleRequest): Promise<Role> => {
//     const response = await api.put<Role>(`/roles/${id}`, roleData)
//     return response.data
//   },

//   delete: async (id: number): Promise<void> => {
//     await api.delete(`/roles/${id}`)
//   }
// }

// export default api
