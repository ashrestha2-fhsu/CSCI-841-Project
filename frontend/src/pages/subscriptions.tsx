

// src/pages/subscriptions.tsx
import { useEffect, useState } from "react";
import axiosInstance from "../services/axiosInstance";
import SubscriptionForm from "./subscriptionsForm";
import SubscriptionDetails from "./subscriptionsDetails";
import PayNowModal from "../pages/subscriptionsPayNow";
import "../styles/subscriptions.css";

export interface Subscription {
  subscriptionId: number;
  name: string;
  amount: number;
  nextBillingDate: string;     // ISO
  paymentMethodId: number;     // FK (Account)
  paymentMethodName?: string;  // optional convenience
  autoRenew: boolean;
  status: "ACTIVE" | "CANCELLED" | "PAUSED" | "PAST_DUE";
  dateCreated?: string;
  dateUpdated?: string;
}

type FormMode = "create" | "edit";

const Subscriptions: React.FC = () => {
  const [subs, setSubs] = useState<Subscription[]>([]);
  const [expandedId, setExpandedId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [showTable, setShowTable] = useState(false);
  const [formMode, setFormMode] = useState<FormMode>("create");
  const [selected, setSelected] = useState<Subscription | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  // Pay Now modal state
  const [payNowFor, setPayNowFor] = useState<Subscription | null>(null);

  // Per-row busy state
  const [rowBusy, setRowBusy] = useState<Record<number, boolean>>({});

  const fmtMoney = (n?: number) =>
    typeof n === "number" ? `USD ${n.toFixed(2)}` : "USD 0.00";

  const daysUntil = (iso?: string) => {
    if (!iso) return "-";
    const d = new Date(iso);
    const now = new Date();
    const diff = Math.ceil((d.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
    return diff >= 0 ? `${diff} day${diff === 1 ? "" : "s"}` : `${-diff} day${diff === -1 ? "" : "s"} ago`;
  };

  const fetchSubs = async () => {
    try {
      setLoading(true);
      setError(null);
      const profile = await axiosInstance.get("/users/profile");
      const userId = profile?.data?.userId;
      const res = await axiosInstance.get(`/subscriptions/user/${userId}`);
      const normalized = (Array.isArray(res.data) ? res.data : []).map((s: any) => ({
        ...s,
        paymentMethodId:
          s.paymentMethodId ??
          s.paymentMethod?.accountId ??
          s.payment_method_id ??
          s.payment_method?.accountId,
        paymentMethodName:
          s.paymentMethodName ??
          s.paymentMethod?.name ??
          s.payment_method?.name ??
          undefined,
      }));
      setSubs(normalized);
    } catch (e) {
      console.error(e);
      setError("Failed to load subscriptions.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSubs();
  }, []);

  const toggleDetails = (id: number) =>
    setExpandedId((prev) => (prev === id ? null : id));

  const handleAdd = () => {
    setFormMode("create");
    setSelected(null);
    setShowForm(true);
  };

  const openEdit = (sub: Subscription) => {
    setFormMode("edit");
    setSelected(sub);
    setShowForm(true);
  };

  const closeForm = () => {
    setShowForm(false);
    setSelected(null);
  };

  const setBusy = (id: number, val: boolean) =>
    setRowBusy((m) => ({ ...m, [id]: val }));

  // ---- Optimistic helpers (with rollback)
  const withOptimisticStatus = async (
    id: number,
    next: Subscription["status"],
    fn: () => Promise<any>
  ) => {
    const prev = subs.find((x) => x.subscriptionId === id)?.status;
    if (!prev) return;

    setSubs((list) => list.map((x) => (x.subscriptionId === id ? { ...x, status: next } : x)));

    try {
      setBusy(id, true);
      await fn();
      await fetchSubs(); // ensure truth from server
    } catch (e: any) {
      console.error(e);
      setError(
        typeof e?.response?.data === "string"
          ? e.response.data
          : "Action failed. Please try again."
      );
      // rollback
      setSubs((list) => list.map((x) => (x.subscriptionId === id ? { ...x, status: prev } : x)));
    } finally {
      setBusy(id, false);
    }
  };

  const pause = (id: number) =>
    withOptimisticStatus(id, "PAUSED", () => axiosInstance.put(`/subscriptions/${id}/pause`));

  const resume = (id: number) =>
    withOptimisticStatus(id, "ACTIVE", () => axiosInstance.put(`/subscriptions/${id}/resume`));

  const cancel = async (id: number) => {
    if (!confirm("Cancel this subscription?")) return;
    try {
      setBusy(id, true);
      await axiosInstance.put(`/subscriptions/${id}/cancel`);
      // Remove from card list immediately (cards hide CANCELLED anyway, but this feels snappier)
      setSubs((list) => list.map((x) => (x.subscriptionId === id ? { ...x, status: "CANCELLED" } : x)));
      await fetchSubs();
    } catch (e) {
      console.error(e);
      setError("Failed to cancel subscription.");
    } finally {
      setBusy(id, false);
    }
  };

  // Hide CANCELLED from cards
  const visibleCards = subs.filter((s) => String(s.status).toUpperCase() !== "CANCELLED");

  return (
    <div className="savings-container">
      <h2 className="header">My Subscriptions</h2>

      <div className="savings-actions">
        <button onClick={handleAdd}>Add New</button>
        <button onClick={() => setShowTable(true)}>View All</button>
      </div>

      {error && <p className="err">{error}</p>}
      {loading && <p className="muted">Loading…</p>}

      <div className="savings-list">
        {visibleCards.length ? (
          visibleCards.map((s) => {
            const statusUC = String(s.status).toUpperCase();
            const isPaused = statusUC === "PAUSED";
            const isCancelled = statusUC === "CANCELLED";
            const isActive = statusUC === "ACTIVE";
            const busy = !!rowBusy[s.subscriptionId];

            return (
              <div className="savings-card" key={s.subscriptionId}>
                <h3>{s.name}</h3>
                <p className="savings-line">{fmtMoney(s.amount)} / month</p>

                <div className="card-btn">
                  <button
                    type="button"
                    onClick={() => toggleDetails(s.subscriptionId)}
                  >
                    {expandedId === s.subscriptionId ? "Hide Details" : "View Details"}
                  </button>
                </div>

                {expandedId === s.subscriptionId && (
                  <div className="savings-details">
                    <table>
                      <tbody>
                        <tr>
                          <td><strong>Status:</strong></td>
                          <td>{s.status}</td>
                        </tr>
                        <tr>
                          <td><strong>Next Billing:</strong></td>
                          <td>
                            {s.nextBillingDate?.split("T").join(" ")} ({daysUntil(s.nextBillingDate)})
                          </td>
                        </tr>
                        <tr>
                          <td><strong>Auto-Renew:</strong></td>
                          <td>{s.autoRenew ? "Yes" : "No"}</td>
                        </tr>
                        <tr>
                          <td><strong>Payment Method:</strong></td>
                          <td>{s.paymentMethodName ?? `Account #${s.paymentMethodId}`}</td>
                        </tr>
                      </tbody>
                    </table>

                    <div className="row-actions" style={{ marginTop: 10 }}>
                      {!isCancelled && (
                        isPaused ? (
                          <button
                            className="btn-sm btn-green"
                            onClick={() => resume(s.subscriptionId)}
                            disabled={busy}
                            title={busy ? "Please wait…" : "Resume this subscription"}
                          >
                            {busy ? "Resuming…" : "Resume"}
                          </button>
                        ) : (
                          <button
                            className="btn-sm btn-blue"
                            onClick={() => pause(s.subscriptionId)}
                            disabled={busy}
                            title={busy ? "Please wait…" : "Pause this subscription"}
                          >
                            {busy ? "Pausing…" : "Pause"}
                          </button>
                        )
                      )}

                      <button
                        className="btn-sm btn-red"
                        onClick={() => cancel(s.subscriptionId)}
                        disabled={busy}
                        title={busy ? "Please wait…" : "Cancel this subscription"}
                      >
                        {busy ? "Cancelling…" : "Cancel"}
                      </button>

                      <button
                        className="btn-sm btn-blue"
                        onClick={() => openEdit(s)}
                        disabled={busy}
                      >
                        Edit
                      </button>

                      {/* Disabled unless ACTIVE */}
                      <button
                        className="btn-sm btn-green"
                        onClick={() => setPayNowFor(s)}
                        disabled={busy || !isActive}
                        title={
                          !isActive
                            ? "Payment only allowed when status is ACTIVE"
                            : busy ? "Please wait…" : undefined
                        }
                      >
                        Pay Now
                      </button>
                    </div>
                  </div>
                )}
              </div>
            );
          })
        ) : (
          <p>No subscriptions found. Add your first one!</p>
        )}
      </div>

      {showForm && (
        <SubscriptionForm
          initialData={selected ?? undefined}
          mode={formMode}
          onClose={closeForm}
          onSuccess={async () => {
            await fetchSubs();
            closeForm();
          }}
        />
      )}

      {showTable && (
        <SubscriptionDetails
          subscriptions={subs}
          onClose={() => setShowTable(false)}
          onRefresh={fetchSubs}
          onEdit={(s) => {
            setShowTable(false);
            setTimeout(() => openEdit(s), 0);
          }}
          onPause={pause}
          onResume={resume}
          onCancel={cancel}
          onPayNow={(s) => {
            setShowTable(false);
            setTimeout(() => setPayNowFor(s), 0);
          }}
        />
      )}

      {payNowFor && (
        <PayNowModal
          subscription={payNowFor}
          onClose={() => setPayNowFor(null)}
          onSuccess={async () => {
            await fetchSubs();
            setPayNowFor(null);
          }}
        />
      )}
    </div>
  );
};

export default Subscriptions;
