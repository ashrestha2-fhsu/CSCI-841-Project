import React, { useState, useEffect } from "react";
import axiosInstance from "../services/axiosInstance";
import { Loan } from "../types";
import "../styles/loan.css";

interface LoanPaymentFormProps {
  loan: Loan;
  onClose: () => void;
  onSubmit?: (paymentData: {
    loanId: string;
    paymentAmount: number;
    isExtra?: boolean;
  }) => void;
}

const LoanPaymentForm: React.FC<LoanPaymentFormProps> = ({ loan, onClose, onSubmit }) => {
  const [form, setForm] = useState({
    monthlyPayment: parseFloat(loan.monthlyPayment) || 0,
    extraPayment: 0,
    paymentDate: new Date().toISOString().split("T")[0],
    selectedType: "monthly" as "monthly" | "extra",
    remainingBalance: parseFloat(loan.outstandingBalance) || 0,
    nextDueDate: loan.dueDate || "",
  });

  useEffect(() => {
    if (loan) {
      setForm((prev) => ({
        ...prev,
        monthlyPayment: parseFloat(loan.monthlyPayment) || 0,
        remainingBalance: parseFloat(loan.outstandingBalance) || 0,
        nextDueDate: loan.dueDate || "",
      }));
    }
  }, [loan]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleRadioChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value as "monthly" | "extra";
    setForm((prev) => ({ ...prev, selectedType: value }));
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const isExtra = form.selectedType === "extra";
    const amount = isExtra ? parseFloat(form.extraPayment.toString()) : parseFloat(form.monthlyPayment.toString());

    if (isNaN(amount) || amount <= 0) {
      alert("Please enter a valid payment amount.");
      return;
    }

    const payload = {
      loanId: loan.loanId,
      paymentAmount: isExtra ? 0 : amount,
      extraPayment: isExtra ? amount : 0,
      remainingBalance: form.remainingBalance,
      nextDueDate: form.nextDueDate,
      paymentDate: form.paymentDate,
      isExtra,
    };

    try {
      await axiosInstance.post(`/loan-payments/${loan.loanId}/pay`, payload);
      alert("✅ Payment successful");
      onClose();
      onSubmit?.(payload);
    } catch (err) {
      console.error("Payment failed:", err);
      alert("❌ Payment failed. Check required fields or try again.");
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <h2>Make a Payment</h2>
        <form onSubmit={handleSubmit}>
          <table className="form-table">
            <tbody>
              <tr>
                <td>
                  <label>
                    Monthly Payment:
                    <input
                      type="radio"
                      name="selectedType"
                      value="monthly"
                      checked={form.selectedType === "monthly"}
                      onChange={handleRadioChange}
                    />
                  </label>
                </td>
                <td>
                  <input
                    type="number"
                    name="monthlyPayment"
                    value={form.monthlyPayment}
                    onChange={handleChange}
                    disabled={form.selectedType !== "monthly"}
                    required={form.selectedType === "monthly"}
                  />
                </td>
              </tr>

              <tr>
                <td>
                  <label>
                    Extra Payment:
                    <input
                      type="radio"
                      name="selectedType"
                      value="extra"
                      checked={form.selectedType === "extra"}
                      onChange={handleRadioChange}
                    />
                  </label>
                </td>
                <td>
                  <input
                    type="number"
                    name="extraPayment"
                    value={form.extraPayment}
                    onChange={handleChange}
                    disabled={form.selectedType !== "extra"}
                    required={form.selectedType === "extra"}
                  />
                </td>
              </tr>

              <tr>
                <td>
                  <label>Remaining Balance:</label>
                </td>
                <td>
                  <input type="number" name="remainingBalance" value={form.remainingBalance} disabled />
                </td>
              </tr>

              <tr>
                <td>
                  <label>Next Due Date:</label>
                </td>
                <td>
                  <input type="date" name="nextDueDate" value={form.nextDueDate} disabled />
                </td>
              </tr>

              <tr>
                <td colSpan={2}>
                  <label>Payment Date:</label>
                  <input
                    type="date"
                    name="paymentDate"
                    value={form.paymentDate}
                    onChange={handleChange}
                    required
                  />
                </td>
              </tr>

              <tr>
                <td colSpan={2} style={{ textAlign: "center" }}>
                  <button type="submit" className="save-btn">
                    Save
                  </button>
                  <button type="button" onClick={onClose} className="cancel-btn">
                    Cancel
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </form>
      </div>
    </div>
  );
};

export default LoanPaymentForm;
