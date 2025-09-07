# Frontend - User Management System

A modern React frontend application for managing users, accounts, and roles. Built with React 19, TypeScript, and Vite.

## Features

- **Dashboard**: Overview of users, accounts, and system statistics
- **User Management**: Create, read, update, and delete users
- **Account Management**: Manage user accounts with different types (Checking, Savings, Credit, Investment)
- **Role Management**: Define roles and permissions for users
- **Responsive Design**: Mobile-friendly interface
- **Modern UI**: Clean and intuitive user interface

## Tech Stack

- **React 19** - Frontend framework
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **React Router DOM** - Client-side routing
- **Axios** - HTTP client for API calls
- **Day.js** - Date manipulation
- **ESLint** - Code linting

## Getting Started

### Prerequisites

- Node.js (version 18 or higher)
- npm or yarn

### Installation

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

4. Open your browser and navigate to `http://localhost:3000`

### Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## Project Structure

```
src/
├── components/          # Reusable UI components
│   ├── Navbar.tsx      # Navigation component
│   ├── UserForm.tsx    # User creation/editing form
│   ├── UserTable.tsx   # User data table
│   ├── AccountForm.tsx # Account creation/editing form
│   ├── AccountTable.tsx# Account data table
│   ├── RoleForm.tsx    # Role creation/editing form
│   └── RoleTable.tsx   # Role data table
├── pages/              # Page components
│   ├── Dashboard.tsx   # Dashboard page
│   ├── Users.tsx       # Users management page
│   ├── Accounts.tsx    # Accounts management page
│   └── Roles.tsx       # Roles management page
├── services/           # API services
│   └── api.ts          # Axios API client
├── types/              # TypeScript type definitions
│   └── index.ts        # All type definitions
├── App.tsx             # Main app component
├── main.tsx            # App entry point
└── index.css           # Global styles
```

## API Integration

The frontend communicates with the backend API through the `/api` proxy configured in `vite.config.ts`. The API service is located in `src/services/api.ts` and provides methods for:

- User CRUD operations
- Account CRUD operations  
- Role CRUD operations

## Features Overview

### Dashboard
- System statistics and metrics
- Recent users overview
- Account type distribution
- Quick access to all sections

### User Management
- Create new users with username, email, and personal information
- Edit existing user details
- Delete users with confirmation
- View user list with pagination

### Account Management
- Create accounts for users with different types
- Manage account balances
- Activate/deactivate accounts
- Associate accounts with users

### Role Management
- Define custom roles with specific permissions
- Assign multiple permissions to roles
- Manage role types (Admin, User, Manager, Customer Service)
- View role assignments

## Styling

The application uses custom CSS with a modern design system including:
- Responsive grid layouts
- Consistent color scheme
- Interactive form elements
- Hover effects and transitions
- Mobile-first responsive design

## Development

The project uses ESLint for code quality and follows React best practices. The development server includes hot module replacement for fast development.

## Building for Production

To build the application for production:

```bash
npm run build
```

The built files will be in the `dist` directory and can be served by any static file server.
