

// src/pages/subscriptionsForm.tsx
import React, { useEffect, useMemo, useState } from "react";
import axiosInstance from "../services/axiosInstance";
import type { Subscription } from "./subscriptions";

type Mode = "create" | "edit";

interface Account {
  accountId: number;
  name: string;
  balance: number;
  currency?: string;
}

interface Props {
  initialData?: Subscription;
  mode?: Mode;
  onClose: () => void;
  onSuccess: () => void;
}

type FormState = {
  name: string;
  amount: string;
  nextBillingDate: string;   // yyyy-MM-ddTHH:mm
  paymentMethodId: string;
  autoRenew: boolean;
  status: "ACTIVE" | "PAUSED" | "CANCELLED" | "PAST_DUE";
};

const SubscriptionForm: React.FC<Props> = ({
  initialData,
  mode = "create",
  onClose,
  onSuccess,
}) => {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [form, setForm] = useState<FormState>({
    name: "",
    amount: "",
    nextBillingDate: "",
    paymentMethodId: "",
    autoRenew: true,
    status: "ACTIVE",
  });

  const title = useMemo(
    () => (mode === "edit" ? "Edit Subscription" : "Create Subscription"),
    [mode]
  );

  // Fetch accounts (tries /accounts/user then /accounts/user/{id})
  const loadAccounts = async () => {
    try {
      try {
        const r1 = await axiosInstance.get("/accounts/user");
        if (Array.isArray(r1.data)) {
          setAccounts(r1.data);
          return;
        }
      } catch (_) {
        // fall back
      }
      const profile = await axiosInstance.get("/users/profile");
      const userId = profile?.data?.userId;
      const r2 = await axiosInstance.get(`/accounts/user/${userId}`);
      setAccounts(Array.isArray(r2.data) ? r2.data : []);
    } catch (e) {
      console.error("Error loading accounts for subscriptions form:", e);
      setAccounts([]);
    }
  };

  useEffect(() => {
    loadAccounts();
  }, []);

  // Seed form from initialData
  useEffect(() => {
    if (!initialData) return;
    setForm({
      name: initialData.name ?? "",
      amount: initialData.amount != null ? String(initialData.amount) : "",
      nextBillingDate: initialData.nextBillingDate
        ? initialData.nextBillingDate.slice(0, 16) // "yyyy-MM-ddTHH:mm"
        : "",
      paymentMethodId: String(initialData.paymentMethodId ?? ""),
      autoRenew: !!initialData.autoRenew,
      status: initialData.status ?? "ACTIVE",
    });
  }, [initialData]);

  // If status is set to CANCELLED or PAUSED, force autoRenew=false
  useEffect(() => {
    if (form.status === "CANCELLED" || form.status === "PAUSED") {
      setForm((p) => (p.autoRenew ? { ...p, autoRenew: false } : p));
    }
  }, [form.status]);

  const onInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, type, value, checked } = e.currentTarget;
    setForm((p) => ({ ...p, [name]: type === "checkbox" ? checked : value }));
  };

  const onSelect = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const { name, value } = e.currentTarget;
    setForm((p) => ({ ...p, [name]: value as any }));
  };

  const toNum = (s: string) => {
    const n = Number(s);
    return Number.isFinite(n) ? n : 0;
  };

  const isoOrDefaultNextMonth = (localDt?: string) => {
    if (localDt) return new Date(localDt).toISOString();
    const d = new Date();
    d.setMonth(d.getMonth() + 1);
    return d.toISOString();
  };

  const submit: React.FormEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      const profile = await axiosInstance.get("/users/profile");
      const userId = profile?.data?.userId;

      const body = {
        userId,
        name: form.name.trim(),
        amount: toNum(form.amount),
        nextBillingDate:
          mode === "create"
            ? isoOrDefaultNextMonth(form.nextBillingDate)
            : form.nextBillingDate
            ? new Date(form.nextBillingDate).toISOString()
            : null,
        paymentMethodId:
          form.paymentMethodId ? Number(form.paymentMethodId) : undefined,
        autoRenew:
          form.status === "CANCELLED" || form.status === "PAUSED"
            ? false
            : form.autoRenew,
        status: form.status,
      };

      if (mode === "edit" && initialData?.subscriptionId) {
        await axiosInstance.put(`/subscriptions/${initialData.subscriptionId}`, body);
      } else {
        // require a payment method on create
        if (!body.paymentMethodId) {
          setSubmitting(false);
          setError("Please select a payment method.");
          return;
        }
        await axiosInstance.post(`/subscriptions`, body);
      }

      onSuccess();
    } catch (err) {
      console.error(err);
      setError("Failed to submit subscription. Please check your inputs.");
    } finally {
      setSubmitting(false);
    }
  };

  const autoRenewDisabled =
    form.status === "CANCELLED" || form.status === "PAUSED";

  return (
    <div className="modal-overlay">
      <div className="modal modal--wide">
        <button onClick={onClose} className="closes-btn" aria-label="Close">
          X
        </button>
        <h2 className="s-title">{title}</h2>

        {error && <div className="s-error">{error}</div>}

        <form onSubmit={submit} className="form-grid">
          <label>
            Name
            <input name="name" value={form.name} onChange={onInput} required />
          </label>

          <label>
            Amount (monthly)
            <input
              name="amount"
              type="number"
              min="0"
              step="0.01"
              value={form.amount}
              onChange={onInput}
              required
            />
          </label>

          <label>
            Next Billing (local)
            <input
              name="nextBillingDate"
              type="datetime-local"
              value={form.nextBillingDate}
              onChange={onInput}
            />
          </label>

          <label>
            Payment Method
            <select
              name="paymentMethodId"
              value={form.paymentMethodId}
              onChange={onSelect}
              required={mode === "create"}
            >
              <option value="">-- Select Account --</option>
              {accounts.map((a) => (
                <option key={a.accountId} value={a.accountId}>
                  {a.name} {a.currency ? `(${a.currency})` : ""} — Balance:{" "}
                  {a.balance.toFixed ? a.balance.toFixed(2) : a.balance}
                </option>
              ))}
            </select>
          </label>

          <label className="inline-flex">
            <input
              type="checkbox"
              name="autoRenew"
              checked={form.autoRenew}
              onChange={onInput}
              disabled={autoRenewDisabled}
              title={
                autoRenewDisabled
                  ? "Auto-renew is disabled when status is PAUSED or CANCELLED."
                  : undefined
              }
            />
            Auto-Renew
          </label>

          <label>
            Status
            <select name="status" value={form.status} onChange={onSelect}>
              <option value="ACTIVE">ACTIVE</option>
              <option value="PAUSED">PAUSED</option>
              <option value="CANCELLED">CANCELLED</option>
              <option value="PAST_DUE">PAST_DUE</option>
            </select>
          </label>

          <div className="form-buttons">
            <button type="submit" className="btn-save" disabled={submitting}>
              {submitting
                ? "Saving…"
                : mode === "edit"
                ? "Update"
                : "Create"}
            </button>
            <button type="button" className="btn-gray" onClick={onClose}>
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default SubscriptionForm;
