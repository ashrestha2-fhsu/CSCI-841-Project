import React, { useEffect, useState } from "react";
import axiosInstance from "../service/axiosInstance";
import "../styles/loan.css";

const LoanPayment = ({ loan, onClose }) => {
  const [payments, setPayments] = useState([]);

  useEffect(() => {
    if (loan?.loanId) {
      fetchPayments();
    }
  }, [loan]);

  const fetchPayments = async () => {
    try {
      const res = await axiosInstance.get(`/loan-payments/${loan.loanId}`);
      if (Array.isArray(res.data)) {
        setPayments(res.data);
      } else {
        console.warn("⚠️ Unexpected payment data:", res.data);
        setPayments([]);
      }
    } catch (err) {
      console.error("Error fetching payments:", err);
      setPayments([]);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal loan-payment-modal">
        <h2>Payments for Loan - {loan?.lenderName || "Loading..."}</h2>
        <button onClick={onClose} className="close-btns">
          ✖
        </button>

        <table className="loan-table">
          <thead>
            <tr>
              <th>Payment</th>
              <th>Extra</th>
              <th>Principal</th>
              <th>Interest</th>
              <th>Total Paid</th>
              <th>Remaining</th>
              <th>Last Paid</th>
              <th>Next Due</th>
              <th>Payment Date</th>
            </tr>
          </thead>
          <tbody>
            {Array.isArray(payments) && payments.length > 0 ? (
              payments.map((p) => (
                <tr key={p.paymentId}>
                  <td>${p.paymentAmount?.toFixed(2)}</td>
                  <td>${p.extraPayment?.toFixed(2)}</td>
                  <td>${p.principalPaid?.toFixed(2)}</td>
                  <td>${p.interestPaid?.toFixed(2)}</td>
                  <td>${p.totalAmountPaid?.toFixed(2)}</td>
                  <td>${p.remainingBalance?.toFixed(2)}</td>
                  <td>{p.lastPaymentDate?.split("T")[0]}</td>
                  <td>{p.nextDueDate?.split("T")[0]}</td>
                  <td>{p.paymentDate?.split("T")[0]}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="9">No payments found for this loan.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default LoanPayment;