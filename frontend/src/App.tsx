// src/App.tsx
import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import About from "./pages/About";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Profile from "./pages/Profile";               // ← ensure file is Profile.tsx
import DashboardLayout from "./layouts/dashboardlayout"; // ← ensure file is DashboardLayout.tsx
import Dashboard from "./pages/Dashboard";
import ProtectedRoute from "./routes/protectedRoute";
import Accounts from "./pages/Accounts";
import Transactions from "./pages/transactions";
import Budget from "./pages/budget";
import Loan from "./pages/loans";

const App: React.FC = () => {
  return (
    <Router>
      <div className="App">
        <Navbar />
        <main>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/about" element={<About />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
SS
            {/* Protected Dashboard Routes */}
            <Route element={<ProtectedRoute />}>
              <Route path="/dashboard" element={<DashboardLayout />}>
                <Route index element={<Dashboard />} />
                <Route path="profile" element={<Profile />} />
                <Route path="accounts" element={<Accounts />} />
                <Route path="transactions" element={<Transactions />} />
                <Route path="budget" element={<Budget />} />
                <Route path="loans" element={<Loan />} />
                {/* You don't need an absolute duplicate like "/dashboard/profile" here */}
              </Route>
            </Route>
          </Routes>
        </main>
      </div>
    </Router>
  );
};

export default App;
