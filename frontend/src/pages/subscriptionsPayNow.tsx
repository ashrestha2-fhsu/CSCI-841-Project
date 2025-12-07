
// src/pages/subscriptionsPayNow.tsx
import React, { useEffect, useMemo, useState } from "react";
import axiosInstance from "../services/axiosInstance";
import type { Subscription } from "./subscriptions";

interface Account {
  accountId: number;
  name: string;
  balance: number;
  currency?: string;
}

interface Props {
  subscription: Subscription;
  onClose: () => void;
  onSuccess: () => void;
}

const PayNowModal: React.FC<Props> = ({ subscription, onClose, onSuccess }) => {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [accountId, setAccountId] = useState<string>("");
  const [amount, setAmount] = useState<string>(
    subscription?.amount != null ? String(subscription.amount) : ""
  );
  const [submitting, setSubmitting] = useState(false);
  const [err, setErr] = useState<string | null>(null);

  const isActive = useMemo(
    () => String(subscription.status).toUpperCase() === "ACTIVE",
    [subscription.status]
  );

  useEffect(() => {
    const loadAccounts = async () => {
      try {
        // Try /accounts/user
        try {
          const r1 = await axiosInstance.get("/accounts/user");
          if (Array.isArray(r1.data)) {
            setAccounts(r1.data);
            return;
          }
        } catch { /* fall through */ }

        // Fallback /accounts/user/{id}
        const profile = await axiosInstance.get("/users/profile");
        const userId = profile?.data?.userId;
        const r2 = await axiosInstance.get(`/accounts/user/${userId}`);
        setAccounts(Array.isArray(r2.data) ? r2.data : []);
      } catch (e) {
        console.error(e);
        setAccounts([]);
      }
    };
    loadAccounts();
  }, []);

  const selectedAccount = useMemo(
    () => accounts.find(a => String(a.accountId) === accountId),
    [accounts, accountId]
  );

  const submit: React.FormEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();
    setErr(null);

    if (!isActive) {
      setErr("Payment is only allowed when the subscription status is ACTIVE. Please resume the subscription first.");
      return;
    }

    const amt = Number(amount);
    if (!accountId) {
      setErr("Please choose a source account.");
      return;
    }
    if (!Number.isFinite(amt) || amt <= 0) {
      setErr("Please enter a valid payment amount greater than 0.");
      return;
    }

    // Frontend balance check (nice UX; backend still validates)
    if (selectedAccount && selectedAccount.balance < amt) {
      setErr("Insufficient funds in the selected account.");
      return;
    }

    try {
      setSubmitting(true);
      await axiosInstance.post(`/subscriptions/${subscription.subscriptionId}/payNow`, null, {
        params: {
          accountId: Number(accountId),
          amount: amt,
        },
      });
      onSuccess();
    } catch (e: any) {
      console.error(e);
      const msg =
        e?.response?.data?.message ||
        e?.response?.data ||
        "Payment failed. Please verify account funds and try again.";
      setErr(String(msg));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal modal--wide">
        <button onClick={onClose} className="closes-btn" aria-label="Close">X</button>
        <h2 className="s-title">Pay Now — {subscription.name}</h2>

        {err && <div className="s-error" style={{ marginBottom: 10 }}>{err}</div>}

        <form onSubmit={submit} className="form-grid">
          <label>
            From Account
            <select
              value={accountId}
              onChange={(e) => setAccountId(e.currentTarget.value)}
              required
            >
              <option value="">-- Select Account --</option>
              {accounts.map((a) => (
                <option key={a.accountId} value={a.accountId}>
                  {a.name} {a.currency ? `(${a.currency})` : ""} — Balance: {a.balance}
                </option>
              ))}
            </select>
          </label>

          <label>
            Amount
            <input
              type="number"
              min="0"
              step="0.01"
              value={amount}
              onChange={(e) => setAmount(e.currentTarget.value)}
              required
            />
          </label>

          <div className="form-buttons">
            <button
              type="submit"
              className="btn-save"
              disabled={submitting || !isActive}
              title={!isActive ? "Payment allowed only when subscription is ACTIVE" : undefined}
            >
              {submitting ? "Processing…" : "Pay Now"}
            </button>
            <button type="button" className="btn-gray" onClick={onClose}>
              Cancel
            </button>
          </div>
        </form>

        {!isActive && (
          <p className="muted" style={{ marginTop: 8 }}>
            Tip: This subscription is <strong>{subscription.status}</strong>. Resume it before paying.
          </p>
        )}
        {selectedAccount && (
          <p className="muted" style={{ marginTop: 6 }}>
            Selected account balance: <strong>{selectedAccount.balance}</strong>
          </p>
        )}
      </div>
    </div>
  );
};

export default PayNowModal;

