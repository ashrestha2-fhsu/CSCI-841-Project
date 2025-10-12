
// src/pages/Budget.tsx
import React, { useEffect, useState } from "react";
import axiosInstance from "../services/axiosInstance";
import "../styles/budget.css";

type BudgetType = "FLEXIBLE" | "STRICT";

interface Profile {
  userId: number;
}

interface BudgetItem {
  budgetId: number;
  description: string;
  amountLimit: number;
  startDate: string; // ISO date
  endDate: string;   // ISO date
  budgetType: BudgetType;
  rolloverAmount: number;
  categoryId: number | string;
  category?: string;
  spent: number;
  percentageUsed: number;
  isDeleted?: boolean;
  dateCreated?: string;
}

interface BudgetReport {
  totalBudgetLimit: number;
  totalRolloverAmount: number;
  budgets: BudgetItem[];
}

interface Category {
  categoryId: number | string;
  name: string;
}

interface BudgetFormData {
  description: string;
  amountLimit: string;
  startDate: string;
  endDate: string;
  budgetType: BudgetType;
  rolloverAmount: string;
  categoryId: string | number;
  budgetId?: number;
}

const Budget: React.FC = () => {
  const [budgets, setBudgets] = useState<BudgetItem[]>([]);
  const [filteredBudgets, setFilteredBudgets] = useState<BudgetItem[]>([]);
  const [report, setReport] = useState<BudgetReport | null>(null);
  const [searchQuery, setSearchQuery] = useState<string>("");
  const [startDate, setStartDate] = useState<string>("");
  const [endDate, setEndDate] = useState<string>("");
  const [showForm, setShowForm] = useState<boolean>(false);
  const [editBudget, setEditBudget] = useState<BudgetItem | null>(null);
  const [formData, setFormData] = useState<BudgetFormData>({
    description: "",
    amountLimit: "",
    startDate: "",
    endDate: "",
    budgetType: "FLEXIBLE",
    rolloverAmount: "",
    categoryId: "",
  });
  const [categories, setCategories] = useState<Category[]>([]);
  const [showConfirmRestore, setShowConfirmRestore] = useState<boolean>(false);
  const [restoreId, setRestoreId] = useState<number | null>(null);
  const [error, setError] = useState<string>("");

  useEffect(() => {
    fetchBudgets();
    fetchCategories();
  }, []);

  const fetchBudgets = async (): Promise<void> => {
    try {
      const profile = await axiosInstance.get<Profile>("/users/profile");
      const userId = profile.data.userId;
      const res = await axiosInstance.get<BudgetReport>(`/budgets/report?userId=${userId}`);
      setBudgets(res.data.budgets);
      setReport(res.data);
      setFilteredBudgets(res.data.budgets);
    } catch (err) {
      console.error("Error fetching budgets:", err);
    }
  };

  const fetchCategories = async (): Promise<void> => {
    try {
      const profile = await axiosInstance.get<Profile>("/users/profile");
      const res = await axiosInstance.get<Category[]>(`/categories/user/${profile.data.userId}`);
      setCategories(res.data);
    } catch (err) {
      console.error("Error fetching categories:", err);
    }
  };

  const applyFilters = (): void => {
    let result = budgets;
    if (searchQuery) {
      const q = searchQuery.toLowerCase();
      result = result.filter(
        (b) =>
          b.description?.toLowerCase().includes(q) ||
          b.category?.toLowerCase().includes(q) ||
          b.budgetType?.toLowerCase().includes(q)
      );
    }
    if (startDate) {
      result = result.filter((b) => new Date(b.startDate) >= new Date(startDate));
    }
    if (endDate) {
      result = result.filter((b) => new Date(b.endDate) <= new Date(endDate));
    }
    setFilteredBudgets(result);
  };

  const handleReset = (): void => {
    setSearchQuery("");
    setStartDate("");
    setEndDate("");
    setFilteredBudgets(budgets);
  };

  const openForm = (budget: BudgetItem | null = null): void => {
    setError("");
    setEditBudget(budget);
    setFormData(
      budget
        ? {
            ...budget,
            budgetId: budget.budgetId,
            categoryId:
              categories.find((c) => c.name === budget.category)?.categoryId ?? "",
            // coerce to the form's string-friendly types
            amountLimit: String(budget.amountLimit ?? ""),
            rolloverAmount: String(budget.rolloverAmount ?? ""),
          }
        : {
            description: "",
            amountLimit: "",
            startDate: "",
            endDate: "",
            budgetType: "FLEXIBLE",
            rolloverAmount: "",
            categoryId: "",
          }
    );
    setShowForm(true);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>): void => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value } as BudgetFormData));
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    setError("");

    const overlap = budgets.some((b) => {
      return (
        b.categoryId === formData.categoryId &&
        b.budgetId !== formData.budgetId &&
        !(
          new Date(formData.endDate) < new Date(b.startDate) ||
          new Date(formData.startDate) > new Date(b.endDate)
        )
      );
    });

    if (overlap) {
      setError("⚠️ A budget already exists for this category and date range.");
      return;
    }

    try {
      if (formData.budgetId) {
        await axiosInstance.put(`/budgets/${formData.budgetId}`, formData);
      } else {
        const profile = await axiosInstance.get<Profile>("/users/profile");
        const userId = profile.data.userId;
        await axiosInstance.post("/budgets", { ...formData, userId });
      }

      setShowForm(false);
      setEditBudget(null);
      fetchBudgets();
    } catch (err) {
      console.error("Error saving budget:", err);
    }
  };

  const handleDelete = async (id?: number): Promise<void> => {
    if (!id) {
      console.warn("⛔ Tried to delete a budget with no ID.");
      return;
    }

    try {
      await axiosInstance.delete(`/budgets/${id}`);
      fetchBudgets();
    } catch (err) {
      console.error("❌ Error deleting budget:", err);
      alert("Failed to delete. Please try again.");
    }
  };

  const handleConfirmRestore = (id: number): void => {
    setRestoreId(id);
    setShowConfirmRestore(true);
  };

  const confirmRestore = async (): Promise<void> => {
    setShowConfirmRestore(false);
    setRestoreId(null);
    fetchBudgets();
  };

  return (
    <div className="budget-page">
      <h2 className="budget-head">📊 My Budgets</h2>

      <div className="budget-header">
        <input
          type="text"
          placeholder="Search by description, category, type"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
        <input
          type="date"
          value={startDate}
          onChange={(e) => setStartDate(e.target.value)}
        />
        <input
          type="date"
          value={endDate}
          onChange={(e) => setEndDate(e.target.value)}
        />
        <button onClick={applyFilters}>Apply Filters</button>
        <button onClick={handleReset}>Reset</button>
      </div>

      <div className="totals-wrapper">
        <div className="totals-item">
          <span>Total Limit: ${report?.totalBudgetLimit ?? 0}</span>
        </div>
        <div className="totals-item">
          <span>Rollover: ${report?.totalRolloverAmount ?? 0}</span>
        </div>
      </div>

      <div className="budget-controls">
        <button onClick={() => openForm(null)}>➕ Add New Budget</button>
      </div>

      <table className="budget-table">
        <thead>
          <tr>
            <th>Description</th>
            <th>Spent / Limit</th>
            <th>Start</th>
            <th>End</th>
            <th>Type</th>
            <th>Rollover</th>
            <th>Created</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody className="budget-tbody">
          {filteredBudgets.map((b) => (
            <tr key={b.budgetId} className={b.isDeleted ? "deleted-row" : ""}>
              <td>{b.description}</td>
              <td>
                <strong>
                  ${b.spent} / ${b.amountLimit}
                </strong>
                <div className="budget-bar">
                  <div
                    className="budget-progress"
                    style={{
                      width: `${Math.min(b.percentageUsed, 100)}%`,
                      backgroundColor:
                        b.percentageUsed > 100
                          ? "red"
                          : b.percentageUsed >= 80
                          ? "orange"
                          : "green",
                    }}
                  />
                </div>
              </td>
              <td>{b.startDate}</td>
              <td>{b.endDate}</td>
              <td>{b.budgetType}</td>
              <td>${b.rolloverAmount}</td>
              <td>{b.dateCreated ? new Date(b.dateCreated).toLocaleDateString() : "N/A"}</td>
              <td className="actions">
                {!b.isDeleted ? (
                  <>
                    <button className="edit-btns" onClick={() => openForm(b)}>
                      ✏️ Edit
                    </button>
                    <button
                      className="delete-btns"
                      onClick={() => {
                        console.log("Deleting budget with ID:", b.budgetId);
                        handleDelete(b.budgetId);
                      }}
                    >
                      🗑 Delete
                    </button>
                  </>
                ) : (
                  <button onClick={() => handleConfirmRestore(b.budgetId)}>
                    ♻️ Restore
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Modal Form */}
      {showForm && (
        <div className="budget-modal-overlay">
          <div className="budget-modal">
            <button className="close-btn" onClick={() => setShowForm(false)}>
              ×
            </button>
            <h3>{editBudget ? "Edit Budget" : "Add Budget"}</h3>
            {error && <p className="error-msg">{error}</p>}
            <form onSubmit={handleSubmit}>
              <input
                name="description"
                placeholder="Description"
                value={formData.description}
                onChange={handleChange}
                required
              />
              <input
                type="number"
                name="amountLimit"
                placeholder="Amount Limit"
                value={formData.amountLimit}
                onChange={handleChange}
                required
              />
              <input
                type="date"
                name="startDate"
                value={formData.startDate}
                onChange={handleChange}
                required
              />
              <input
                type="date"
                name="endDate"
                value={formData.endDate}
                onChange={handleChange}
                required
              />
              <select
                name="budgetType"
                value={formData.budgetType}
                onChange={handleChange}
              >
                <option value="FLEXIBLE">FLEXIBLE</option>
                <option value="STRICT">STRICT</option>
              </select>
              <input
                type="number"
                name="rolloverAmount"
                placeholder="Rollover Amount"
                value={formData.rolloverAmount}
                onChange={handleChange}
              />
              <select
                name="categoryId"
                value={formData.categoryId}
                onChange={handleChange}
                required
              >
                <option value="">Select Category</option>
                {categories.map((cat) => (
                  <option key={cat.categoryId} value={cat.categoryId}>
                    {cat.name}
                  </option>
                ))}
              </select>
              <div className="form-buttons">
                <button type="submit">💾 Save</button>
                <button type="button" onClick={() => setShowForm(false)}>
                  ❌ Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Restore Confirmation Modal */}
      {showConfirmRestore && (
        <div className="modal-overlay">
          <div className="modal">
            <p>Are you sure you want to restore this budget?</p>
            <button onClick={confirmRestore}>Yes, Restore</button>
            <button onClick={() => setShowConfirmRestore(false)}>Cancel</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Budget;
