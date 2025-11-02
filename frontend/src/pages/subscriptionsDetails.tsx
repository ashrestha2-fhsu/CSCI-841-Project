

import React from "react";
import type { Subscription } from "./subscriptions";
import "../styles/subscriptions.css";

interface Props {
  subscriptions: Subscription[];
  onEdit: (s: Subscription) => void;
  onPause: (id: number) => void;
  onResume: (id: number) => void;
  onCancel: (id: number) => void;
  onPayNow: (s: Subscription) => void;
  onClose: () => void;
  onRefresh: () => Promise<void> | void;
}

const SubscriptionDetails: React.FC<Props> = ({
  subscriptions,
  onEdit,
  onPause,
  onResume,
  onCancel,
  onPayNow,
  onClose,
}) => {
  const closeThen = (fn: () => void) => {
    onClose();
    setTimeout(fn, 0);
  };

  const fmtMoney = (n: number | undefined) =>
    typeof n === "number" ? `$${n.toFixed(2)}` : "-";

  return (
    <div className="savings-modal-overlay">
      <div className="savings-modal modal--wide">
        <button className="s-close" onClick={onClose} aria-label="Close">X</button>
        <h2 className="s-title">All Subscriptions</h2>

        <div className="savings-table-wrap">
          <table className="savings-table">
            <thead>
              <tr>
                <th>Name</th>
                <th className="num">Amount</th>
                <th>Next Billing</th>
                <th>Status</th>
                <th>Auto-Renew</th>
                <th>Payment Method</th>
                <th className="actions-col">Actions</th>
              </tr>
            </thead>
            <tbody>
              {subscriptions.map((s) => {
                const statusUC = String(s.status).toUpperCase();
                const isPaused = statusUC === "PAUSED";
                const isCancelled = statusUC === "CANCELLED";

                return (
                  <tr key={s.subscriptionId}>
                    <td className="truncate">{s.name}</td>
                    <td className="num">{fmtMoney(s.amount)}</td>
                    <td>{s.nextBillingDate?.split("T").join(" ")}</td>
                    <td>
                      <span className={`badge ${statusUC.toLowerCase()}`}>{s.status}</span>
                    </td>
                    <td>{s.autoRenew ? "Yes" : "No"}</td>
                    <td>{s.paymentMethodName ?? `#${s.paymentMethodId}`}</td>
                    <td className="actions-col">
                      <div className="row-actions no-wrap">
                        {!isCancelled && (
                          isPaused ? (
                            <button
                              className="btn-sm btn-green"
                              onClick={() => onResume(s.subscriptionId)}
                            >
                              Resume
                            </button>
                          ) : (
                            <button
                              className="btn-sm btn-blue"
                              onClick={() => onPause(s.subscriptionId)}
                            >
                              Pause
                            </button>
                          )
                        )}
                        <button
                          className="btn-sm btn-red"
                          onClick={() => onCancel(s.subscriptionId)}
                        >
                          Cancel
                        </button>
                        <button
                          className="btn-sm btn-blue"
                          onClick={() => closeThen(() => onEdit(s))}
                        >
                          Edit
                        </button>
                        <button
                          className="btn-sm btn-green"
                          onClick={() => closeThen(() => onPayNow(s))}
                          disabled={isPaused || isCancelled}
                          title={
                            isPaused || isCancelled
                              ? "Disabled while paused/cancelled"
                              : undefined
                          }
                        >
                          Pay Now
                        </button>
                      </div>
                    </td>
                  </tr>
                );
              })}
              {!subscriptions.length && (
                <tr><td className="empty" colSpan={7}>No subscriptions found.</td></tr>
              )}
            </tbody>
          </table>
        </div>

        <div className="s-footer">
          <button className="btn-gray" onClick={onClose}>Close</button>
        </div>
      </div>
    </div>
  );
};

export default SubscriptionDetails;
