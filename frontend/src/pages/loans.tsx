import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import dayjs from "dayjs";
import axiosInstance from "../services/axiosInstance";
import LoanForm from "./loanForm";
import LoanPaymentForm from "./loanPaymentForm";
import LoanPayment from "./loanPayment";
import { Loan as LoanType } from "../types";
import "../styles/loan.css";

interface LoanReport {
  numberOfLoans: number;
  totalLoanBorrowed: number;
  totalOutstandingBalance: number;
}

const Loan: React.FC = () => {
  const [loans, setLoans] = useState<LoanType[]>([]);
  const [report, setReport] = useState<LoanReport>({
    numberOfLoans: 0,
    totalLoanBorrowed: 0,
    totalOutstandingBalance: 0,
  });
  const [showForm, setShowForm] = useState(false);
  const [editLoan, setEditLoan] = useState<LoanType | null>(null);
  const [selectedLoan, setSelectedLoan] = useState<LoanType | null>(null);
  const [showPaymentForm, setShowPaymentForm] = useState(false);
  const [showPaymentDetails, setShowPaymentDetails] = useState(false);

  const fetchLoans = async () => {
    try {
      const profile = await axiosInstance.get("/users/profile");
      const userId = profile.data.userId;
      const res = await axiosInstance.get<LoanType[]>(`/loans/user/${userId}`);
      const loansData = res.data;
      setLoans(loansData);

      const totalBorrowed = loansData.reduce(
        (acc, loan) => acc + parseFloat(loan.amountBorrowed || "0"),
        0
      );
      const totalOutstandingBalance = loansData.reduce(
        (acc, loan) => acc + parseFloat(loan.outstandingBalance || "0"),
        0
      );

      setReport({
        numberOfLoans: loansData.length,
        totalLoanBorrowed: totalBorrowed,
        totalOutstandingBalance,
      });
    } catch (err) {
      console.error("Error fetching loans:", err);
    }
  };

  const handleDelete = async (loanId: string) => {
    if (!window.confirm("Are you sure you want to delete this loan?")) return;
    try {
      await axiosInstance.delete(`/loans/${loanId}`);
      fetchLoans();
    } catch (err) {
      console.error("Error deleting loan:", err);
    }
  };

  const handlePaymentSubmit = async (paymentData: {
    loanId: string;
    paymentAmount: number;
    isExtra?: boolean;
  }) => {
    try {
      await axiosInstance.post(
        `/loan-payments/${paymentData.loanId}/pay`,
        paymentData.isExtra
          ? { extraPayment: paymentData.paymentAmount }
          : { paymentAmount: paymentData.paymentAmount }
      );
      alert("✅ Payment successful");
      setShowPaymentForm(false);
      fetchLoans();
    } catch (err) {
      console.error("Payment failed:", err);
      alert("❌ Payment failed. Please try again.");
    }
  };

  useEffect(() => {
    fetchLoans();
  }, []);

  return (
    <div className="loan-container">
      <h2 className="loan-title">
        <i className="fa fa-file-invoice-dollar"></i> My Loans
      </h2>

      <div className="loan-summary-cards">
        <div className="loan-summary-card total-loan">
          <h4>Total Loans</h4>
          <p>{report.numberOfLoans}</p>
        </div>
        <div className="loan-summary-card amount-borrowed">
          <h4>Total Borrowed</h4>
          <p>${report.totalLoanBorrowed.toFixed(2)}</p>
        </div>
        <div className="loan-summary-card outstanding-balance">
          <h4>Outstanding Balance</h4>
          <p>${report.totalOutstandingBalance.toFixed(2)}</p>
        </div>
        <button
          className="loan-add-btn"
          onClick={() => {
            setEditLoan(null);
            setShowForm(true);
          }}
        >
          ➕ Add Loan
        </button>
      </div>

      <table className="loan-table">
        <thead>
          <tr>
            <th>Lender</th>
            <th>Borrowed</th>
            <th>Years</th>
            <th>Rate</th>
            <th>Monthly</th>
            <th>Outstanding</th>
            <th>Due Date</th>
            <th>Status</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {loans.map((loan) => (
            <tr key={loan.loanId}>
              <td>{loan.lenderName}</td>
              <td>${parseFloat(loan.amountBorrowed).toFixed(2)}</td>
              <td>{loan.numberOfYears}</td>
              <td>{parseFloat(loan.interestRate).toFixed(2)}%</td>
              <td>${parseFloat(loan.monthlyPayment).toFixed(2)}</td>
              <td>${parseFloat(loan.outstandingBalance).toFixed(2)}</td>
              <td>{dayjs(loan.dueDate).format("YYYY-MM-DD")}</td>
              <td>{loan.status}</td>
              <td>
                <button
                  className="edit-btn"
                  onClick={() => {
                    setEditLoan(loan);
                    setShowForm(true);
                  }}
                >
                  Edit
                </button>
                <button
                  className="delete-btn"
                  onClick={() => handleDelete(loan.loanId)}
                >
                  Delete
                </button>
                <button
                  className="pay-btn"
                  onClick={() => {
                    setSelectedLoan(loan);
                    setShowPaymentForm(true);
                  }}
                >
                  Pay
                </button>
                <button
                  className="details-btn"
                  onClick={() => {
                    setSelectedLoan(loan);
                    setShowPaymentDetails(true);
                  }}
                >
                  Paym't Details
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {showForm && (
        <LoanForm
          initialData={editLoan}
          onClose={() => {
            setShowForm(false);
            fetchLoans();
          }}
          onSuccess={fetchLoans}
        />
      )}

      {showPaymentForm && selectedLoan && (
        <LoanPaymentForm
          loan={selectedLoan}
          onClose={() => setShowPaymentForm(false)}
          onSubmit={handlePaymentSubmit}
        />
      )}

      {showPaymentDetails && selectedLoan && (
        <LoanPayment
          loan={selectedLoan}
          onClose={() => setShowPaymentDetails(false)}
        />
      )}
    </div>
  );
};

export default Loan;
