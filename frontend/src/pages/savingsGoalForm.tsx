

// src/pages/savingsGoalForm.tsx
import React, { useEffect, useMemo, useState } from "react";
import axiosInstance from "../services/axiosInstance";
import type { SavingsGoal } from "./savingsGoal";
import "../styles/savingsGoal.css";
import {
  PriorityLevel,
  ContributionFrequency,
  SavingsGoalStatus,
} from "../enums/savingsGoalEnums";

type Mode = "create" | "edit" | "contribute";

interface Account {
  accountId: number;
  name: string;
  currency: string;
  balance: number;
}

interface Props {
  initialData?: SavingsGoal;
  mode?: Mode;
  onClose: () => void;
  onSuccess: () => void;
}

type FormState = {
  goalName: string;
  targetAmount: string;
  currentAmount: string; // used as "amount to contribute" in contribute mode
  deadline: string;
  autoSave: boolean;
  priorityLevel: PriorityLevel;
  contributionFrequency: ContributionFrequency;
  accountId: string; // for contribute
  status?: SavingsGoalStatus;
};

const priorities = Object.values(PriorityLevel);
const frequencies = Object.values(ContributionFrequency);

const SavingsGoalForm: React.FC<Props> = ({
  initialData,
  mode = "create",
  onClose,
  onSuccess,
}) => {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [form, setForm] = useState<FormState>({
    goalName: "",
    targetAmount: "",
    currentAmount: "",
    deadline: "",
    autoSave: false,
    priorityLevel: PriorityLevel.MEDIUM,
    contributionFrequency: ContributionFrequency.MONTHLY,
    accountId: "",
  });

  // hydrate from initialData
  useEffect(() => {
    if (!initialData) return;
    setForm((prev) => ({
      ...prev,
      goalName: initialData.goalName ?? "",
      targetAmount:
        initialData.targetAmount != null ? String(initialData.targetAmount) : "",
      currentAmount:
        mode === "contribute"
          ? ""
          : initialData.currentAmount != null
          ? String(initialData.currentAmount)
          : "",
      deadline: initialData.deadline ? initialData.deadline.split("T")[0] : "",
      autoSave: !!initialData.autoSave,
      priorityLevel:
        (initialData.priorityLevel as PriorityLevel) ?? PriorityLevel.MEDIUM,
      contributionFrequency:
        (initialData.contributionFrequency as ContributionFrequency) ??
        ContributionFrequency.MONTHLY,
      accountId: "",
    }));
  }, [initialData, mode]);

  // ✅ Correctly fetch accounts for the signed-in user in contribute mode
  useEffect(() => {
    const fetchAccounts = async () => {
      if (mode !== "contribute") return;
      try {
        const profile = await axiosInstance.get("/users/profile");
        const userId = profile?.data?.userId;
        if (!userId) return;

        // Use the same endpoint as Accounts.tsx
        const res = await axiosInstance.get<Account[]>(`/accounts/user/${userId}`);
        setAccounts(Array.isArray(res.data) ? res.data : []);
      } catch (err) {
        console.error("Error loading accounts:", err);
        setAccounts([]);
      }
    };
    fetchAccounts();
  }, [mode]);

  const title = useMemo(() => {
    if (mode === "edit") return "Edit Savings Goal";
    if (mode === "contribute") return "Contribute to Savings Goal";
    return "Create Savings Goal";
  }, [mode]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, type, value, checked } = e.currentTarget;
    setForm((p) => ({ ...p, [name]: type === "checkbox" ? checked : value }));
  };

  const handleSelectChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const { name, value } = e.currentTarget;
    setForm((p) => ({ ...p, [name]: value as any }));
  };

  const isReadOnly = (field: keyof FormState) =>
    mode === "contribute" && (field === "goalName" || field === "targetAmount");

  const toNumberOrZero = (val: string) => {
    const n = Number(val);
    return Number.isFinite(n) ? n : 0;
  };

  const handleSubmit: React.FormEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      const profile = await axiosInstance.get("/users/profile");
      const userId = profile?.data?.userId;

      if (mode === "edit" && initialData?.goalId) {
        await axiosInstance.put(`/savings-goals/${initialData.goalId}`, {
          goalName: form.goalName.trim(),
          targetAmount: toNumberOrZero(form.targetAmount),
          currentAmount: toNumberOrZero(form.currentAmount),
          deadline: form.deadline || null,
          autoSave: form.autoSave,
          priorityLevel: form.priorityLevel,
          contributionFrequency: form.contributionFrequency,
          userId,
        });
      } else if (mode === "contribute" && initialData?.goalId) {
        const amount = toNumberOrZero(form.currentAmount);
        if (amount <= 0) {
          setError("Enter an amount greater than 0.");
          setSubmitting(false);
          return;
        }
        if (!form.accountId) {
          setError("Select an account to fund this contribution.");
          setSubmitting(false);
          return;
        }

        // Backend expects params: amount & accountId
        await axiosInstance.post(
          `/savings-goals/${initialData.goalId}/contribute`,
          null,
          {
            params: {
              amount,
              accountId: Number(form.accountId),
            },
          }
        );
      } else {
        await axiosInstance.post("/savings-goals", {
          goalName: form.goalName.trim(),
          targetAmount: toNumberOrZero(form.targetAmount),
          currentAmount: toNumberOrZero(form.currentAmount),
          deadline: form.deadline || null,
          autoSave: form.autoSave,
          priorityLevel: form.priorityLevel,
          contributionFrequency: form.contributionFrequency,
          status: SavingsGoalStatus.ACTIVE,
          userId,
        });
      }

      onSuccess();
    } catch (err: any) {
      console.error(err);
      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        "Failed to submit savings goal. Please check your inputs.";
      setError(String(msg));
    } finally {
      setSubmitting(false);
    }
  };

  const fmtMoney = (n?: number, c?: string) =>
    typeof n === "number" ? `${c ?? "USD"} ${n.toFixed(2)}` : "-";

  return (
    <div className="fixed inset-0 bg-black/40 z-40 flex items-center justify-center p-3">
      <div className="bg-white w-full max-w-lg rounded-2xl shadow-lg p-5">
        <div className="flex items-center justify-between mb-3">
          <h2 className="text-xl font-semibold">{title}</h2>
          <button
            className="p-2 rounded-lg hover:bg-gray-100"
            onClick={onClose}
            aria-label="Close"
          >
            ✕
          </button>
        </div>

        {error && <div className="mb-3 text-sm text-red-600">{error}</div>}

        <form onSubmit={handleSubmit} className="grid gap-3">
          <input
            name="goalName"
            placeholder="Goal Name"
            value={form.goalName}
            onChange={handleInputChange}
            readOnly={isReadOnly("goalName")}
            required
            className="border rounded-lg px-3 py-2"
          />

          <input
            name="targetAmount"
            type="number"
            placeholder="Target Amount"
            value={form.targetAmount}
            onChange={handleInputChange}
            readOnly={isReadOnly("targetAmount")}
            required
            className="border rounded-lg px-3 py-2"
            min="0"
            step="0.01"
          />

          {mode !== "create" && (
            <input
              name="currentAmount"
              type="number"
              placeholder={mode === "contribute" ? "Amount to contribute" : "Current Amount"}
              value={form.currentAmount}
              onChange={handleInputChange}
              required={mode === "contribute"}
              className="border rounded-lg px-3 py-2"
              min="0"
              step="0.01"
            />
          )}

          {mode === "contribute" && (
            <select
              name="accountId"
              value={form.accountId}
              onChange={handleSelectChange}
              required
              className="border rounded-lg px-3 py-2"
            >
              <option value="">-- Select Account --</option>
              {accounts.map((acc) => (
                <option key={acc.accountId} value={acc.accountId}>
                  {acc.name} (Balance: {fmtMoney(acc.balance, acc.currency)})
                </option>
              ))}
            </select>
          )}

          {mode !== "contribute" && (
            <>
              <input
                name="deadline"
                type="date"
                value={form.deadline}
                onChange={handleInputChange}
                className="border rounded-lg px-3 py-2"
              />

              <select
                name="priorityLevel"
                value={form.priorityLevel}
                onChange={handleSelectChange}
                className="border rounded-lg px-3 py-2"
              >
                {priorities.map((p) => (
                  <option key={p} value={p}>{p}</option>
                ))}
              </select>

              <select
                name="contributionFrequency"
                value={form.contributionFrequency}
                onChange={handleSelectChange}
                className="border rounded-lg px-3 py-2"
              >
                {frequencies.map((f) => (
                  <option key={f} value={f}>{f}</option>
                ))}
              </select>

              <label className="inline-flex items-center gap-2 text-sm">
                <input
                  type="checkbox"
                  name="autoSave"
                  checked={form.autoSave}
                  onChange={handleInputChange}
                  className="rounded"
                />
                Auto-Save
              </label>
            </>
          )}

          <div className="flex gap-2 mt-2">
            <button
              type="submit"
              disabled={submitting}
              className="px-4 py-2 rounded-xl bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-60"
            >
              {submitting
                ? "Saving…"
                : mode === "edit"
                ? "Update"
                : mode === "contribute"
                ? "Contribute"
                : "Create"}
            </button>
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 rounded-xl bg-gray-200 hover:bg-gray-300"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default SavingsGoalForm;