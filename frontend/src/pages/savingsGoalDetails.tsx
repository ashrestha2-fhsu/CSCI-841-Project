

import React, { useState } from "react";
import type { SavingsGoal } from "./savingsGoal";
import axiosInstance from "../services/axiosInstance";

interface Props {
  savingsGoals: SavingsGoal[];
  onEdit: (goal: SavingsGoal) => void;
  onContribute: (goal: SavingsGoal) => void;
  onClose: () => void;
  onRefresh: () => Promise<void> | void;
}

const SavingsGoalDetails: React.FC<Props> = ({
  savingsGoals,
  onEdit,
  onContribute,
  onClose,
  onRefresh,
}) => {
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);

  const fmtMoney = (n: number | undefined) =>
    typeof n === "number" ? `$${n.toFixed(2)}` : "-";

  const fmtDate = (d?: string) => (d ? d.split("T")[0] : "-");

  const handleDelete = async (goalId: number) => {
    if (!confirm("Delete this savings goal? This action cannot be undone.")) return;
    try {
      setDeletingId(goalId);
      setError(null);
      await axiosInstance.delete(`/savings-goals/${goalId}`);
      await onRefresh();
    } catch (e) {
      console.error(e);
      setError("Failed to delete savings goal.");
    } finally {
      setDeletingId(null);
    }
  };

  /** Close this modal first, then open the target form (prevents stacking underlay). */
  const closeThen = (fn: () => void) => {
    onClose();
    // let the modal unmount before opening the form
    setTimeout(fn, 0);
  };

  return (
    <div className="modal-overlay">
      <div className="modals">
        <button className="closes-btn" onClick={onClose} aria-label="Close">X</button>
        <h2 className="header">All Savings Goals</h2>

        {error && (
          <div style={{ color: "#dc3545", marginBottom: 10, textAlign: "center" }}>
            {error}
          </div>
        )}

        <table className="accounts-table">
          <thead>
            <tr>
              <th>Goal Name</th>
              <th>Target</th>
              <th>Saved</th>
              <th>Deadline</th>
              <th>Status</th>
              <th>Auto-Save</th>
              <th>Priority</th>
              <th>Frequency</th>
              <th>Created</th>
              <th>Updated</th>
              <th>Actions</th>
            </tr>
          </thead>

          <tbody>
            {savingsGoals.map((g) => (
              <tr key={g.goalId}>
                <td>{g.goalName}</td>
                <td className="num">{fmtMoney(g.targetAmount)}</td>
                <td className="num">{fmtMoney(g.currentAmount)}</td>
                <td>{fmtDate(g.deadline)}</td>
                <td>{g.status ?? "-"}</td>
                <td>{g.autoSave ? "Yes" : "No"}</td>
                <td>{g.priorityLevel ?? "-"}</td>
                <td>{g.contributionFrequency ?? "-"}</td>
                <td>{fmtDate(g.dateCreated)}</td>
                <td>{fmtDate(g.dateUpdated)}</td>
                <td>
                  {/* Order matters to match your nth-child button colors:
                      (1) blue, (2) red, (3) green */}
                  <button className ="goal-btn" onClick={() => closeThen(() => onEdit(g))}>Edit</button>
                  <button className ="goal-btn"
                    onClick={() => handleDelete(g.goalId)}
                    disabled={deletingId === g.goalId}
                  >
                    {deletingId === g.goalId ? "Deleting…" : "Delete"}
                  </button>
                  <button className ="goal-btn" onClick={() => closeThen(() => onContribute(g))}>
                    Contribute
                  </button>
                </td>
              </tr>
            ))}

            {savingsGoals.length === 0 && (
              <tr>
                <td colSpan={11} style={{ textAlign: "center", color: "#666" }}>
                  No savings goals found.
                </td>
              </tr>
            )}
          </tbody>
        </table>

        <div className="form-buttons">
          <button className="cancel-btn" onClick={onClose}>Close</button>
        </div>
      </div>
    </div>
  );
};

export default SavingsGoalDetails;

