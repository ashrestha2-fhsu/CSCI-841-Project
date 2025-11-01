

import { useEffect, useState } from "react";
import axiosInstance from "../services/axiosInstance";
import SavingsGoalForm from "./savingsGoalForm";
import SavingsGoalDetails from "./savingsGoalDetails";
import "../styles/savingsGoal.css";

export interface SavingsGoal {
  goalId: number;
  goalName: string;
  targetAmount: number;
  currentAmount: number;
  status: "ACTIVE" | "COMPLETED" | "IN_PROGRESS" | "CANCELLED";
  deadline?: string;
  autoSave?: boolean;
  priorityLevel?: "LOW" | "MEDIUM" | "HIGH";
  contributionFrequency?: "DAILY" | "WEEKLY" | "BIWEEKLY" | "MONTHLY";
  dateCreated?: string;
  dateUpdated?: string;
}

type FormMode = "create" | "edit" | "contribute";

const SavingGoal: React.FC = () => {
  const [goals, setGoals] = useState<SavingsGoal[]>([]);
  const [expandedGoal, setExpandedGoal] = useState<number | null>(null); // ✅ one open at a time
  const [showForm, setShowForm] = useState(false);
  const [showTable, setShowTable] = useState(false);
  const [formMode, setFormMode] = useState<FormMode>("create");
  const [selectedGoal, setSelectedGoal] = useState<SavingsGoal | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const fetchGoals = async () => {
    try {
      setLoading(true);
      setError(null);
      const profile = await axiosInstance.get("/users/profile");
      const userId = profile?.data?.userId;
      const res = await axiosInstance.get(`/savings-goals/user/${userId}`);
      setGoals(Array.isArray(res.data) ? res.data : []);
    } catch (e: any) {
      console.error(e);
      setError("Failed to load savings goals.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchGoals();
  }, []);

  const toggleDetails = (goalId: number) => {
    setExpandedGoal((prev) => (prev === goalId ? null : goalId));
  };

  const handleAdd = () => {
    setFormMode("create");
    setSelectedGoal(null);
    setShowForm(true);
  };

  const openEdit = (g: SavingsGoal) => {
    setFormMode("edit");
    setSelectedGoal(g);
    setShowForm(true);
  };

  const openContribute = (g: SavingsGoal) => {
    setFormMode("contribute");
    setSelectedGoal(g);
    setShowForm(true);
  };

  const closeForm = () => {
    setShowForm(false);
    setSelectedGoal(null);
  };

  const fmtMoney = (n?: number) =>
    typeof n === "number" ? `USD ${n.toFixed(2)}` : "USD 0.00";

  return (
    <div className="savings-container">
      <h2 className="header">My Savings Goals</h2>

      <div className="savings-actions">
        <button onClick={handleAdd}>Add New</button>
        <button onClick={() => setShowTable(true)}>View All</button>
      </div>

      {error && <p className="err">{error}</p>}
      {loading && <p className="muted">Loading…</p>}

      <div className="savings-list">
        {goals.length > 0 ? (
          goals.map((g) => {
            const percent =
              g.targetAmount > 0
                ? Math.min((g.currentAmount / g.targetAmount) * 100, 100)
                : 0;

            return (
              <div className="savings-card" key={g.goalId}>
                <h3>{g.goalName}</h3>
                <p className="savings-line">
                  {fmtMoney(g.currentAmount)} / {fmtMoney(g.targetAmount)}
                </p>

                <div className="card-btn">
                  <button type="button" onClick={() => toggleDetails(g.goalId)}>
                    {expandedGoal === g.goalId ? "Hide Details" : "View Details"}
                  </button>
                </div>

                {expandedGoal === g.goalId && (
                  <div className="savings-details">
                    <table>
                      <tbody>
                        <tr>
                          <td><strong>Target:</strong></td>
                          <td>{fmtMoney(g.targetAmount)}</td>
                        </tr>
                        <tr>
                          <td><strong>Saved:</strong></td>
                          <td>{fmtMoney(g.currentAmount)}</td>
                        </tr>
                        <tr>
                          <td><strong>Status:</strong></td>
                          <td>{g.status}</td>
                        </tr>
                        <tr>
                          <td><strong>Progress:</strong></td>
                          <td>{percent.toFixed(0)}%</td>
                        </tr>
                      </tbody>
                    </table>

                    <div className="savings-progress">
                      <div
                        className="savings-progress__fill"
                        style={{ width: `${percent}%` }}
                      />
                    </div>
                  </div>
                )}
              </div>
            );
          })
        ) : (
          <p>No savings goals found. Create one to get started!</p>
        )}
      </div>

      {/* Forms/Modals */}
      {showForm && (
        <SavingsGoalForm
          initialData={selectedGoal ?? undefined}
          mode={formMode}
          onClose={closeForm}
          onSuccess={async () => {
            await fetchGoals();
            closeForm();
          }}
        />
      )}

      {showTable && (
        <SavingsGoalDetails
          savingsGoals={goals}
          onClose={() => setShowTable(false)}
          onRefresh={fetchGoals}
          onEdit={openEdit}
          onContribute={openContribute}
        />
      )}
    </div>
  );
};

export default SavingGoal;

