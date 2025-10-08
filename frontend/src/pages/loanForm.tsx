import React, { useEffect, useState } from "react";
import axiosInstance from "../services/axiosInstance";
import { Loan } from "../types";
import "../styles/loan.css";

interface LoanFormProps {
  initialData: Loan | null;
  onClose: () => void;
  onSuccess: () => void;
}

const LoanForm: React.FC<LoanFormProps> = ({ initialData, onClose, onSuccess }) => {
  const [form, setForm] = useState({
    lenderName: "",
    amountBorrowed: "",
    numberOfYears: 0,
    interestRate: "",
    dueDate: "",
    status: "ACTIVE" as Loan["status"],
  });

  useEffect(() => {
    if (initialData) {
      setForm({
        lenderName: initialData.lenderName,
        amountBorrowed: initialData.amountBorrowed,
        numberOfYears: initialData.numberOfYears,
        interestRate: initialData.interestRate,
        dueDate: initialData.dueDate,
        status: initialData.status,
      });
    }
  }, [initialData]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      const profile = await axiosInstance.get("/users/profile");
      const userId = profile.data.userId;
      const payload = { ...form, userId };

      if (initialData) {
        await axiosInstance.put(`/loans/${initialData.loanId}`, payload);
        alert("✅ Loan updated successfully");
      } else {
        await axiosInstance.post("/loans", payload);
        alert("✅ Loan added successfully");
      }

      onSuccess();
      onClose();
    } catch (err) {
      console.error("Error submitting loan:", err);
      alert("❌ Failed to submit loan. Please try again.");
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal loan-form-modal">
        <h2>{initialData ? "Edit Loan" : "Add Loan"}</h2>
        <form onSubmit={handleSubmit}>
          <table className="form-table">
            <tbody>
              <tr>
                <td><label>Lender:</label></td>
                <td><input name="lenderName" value={form.lenderName} onChange={handleChange} required /></td>
                <td><label>Years:</label></td>
                <td><input type="number" name="numberOfYears" value={form.numberOfYears} onChange={handleChange} required /></td>
              </tr>
              <tr>
                <td><label>Borrowed:</label></td>
                <td><input type="number" name="amountBorrowed" value={form.amountBorrowed} onChange={handleChange} required /></td>
                <td><label>Rate (%):</label></td>
                <td><input type="number" name="interestRate" value={form.interestRate} onChange={handleChange} required /></td>
              </tr>
              <tr>
                <td><label>Status:</label></td>
                <td>
                  <select name="status" value={form.status} onChange={handleChange}>
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="PAID_OFF">PAID OFF</option>
                    <option value="DEFAULTED">DEFAULTED</option>
                  </select>
                </td>
                <td><label>Due Date:</label></td>
                <td><input type="date" name="dueDate" value={form.dueDate} onChange={handleChange} required /></td>
              </tr>
            </tbody>
          </table>
          <div className="loan-form-buttons">
            <button type="submit" className="btn btn-save">{initialData ? "Update" : "Save"}</button>
            <button type="button" className="btn btn-cancel" onClick={onClose}>Cancel</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LoanForm;
