// LoanPaymentForm.jsx - Modal form for making a loan payment
import React, { useState, useEffect } from "react";
import axiosInstance from "../service/axiosInstance";
import "../styles/loan.css";

const LoanPaymentForm = ({ loan, onClose, onSubmit }) => {
  const [form, setForm] = useState({
    monthlyPayment: loan?.monthlyPayment || 0,
    extraPayment: 0,
    paymentDate: new Date().toISOString().split("T")[0],
    selectedType: "monthly",
    userId: null,
    remainingBalance: loan?.outstandingBalance || 0,
    nextDueDate: loan?.dueDate || "",
  });

  useEffect(() => {
    if (loan) {
      setForm((prev) => ({
        ...prev,
        monthlyPayment: loan.monthlyPayment || 0,
        remainingBalance: loan.outstandingBalance || 0,
        nextDueDate: loan.dueDate || "",
      }));
    }
    fetchUser();
  }, [loan]);

  const fetchUser = async () => {
    try {
      const profile = await axiosInstance.get("/users/profile");
      setForm((prev) => ({ ...prev, userId: profile.data.userId }));
    } catch (err) {
      console.error("Error fetching user:", err);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleRadioChange = (e) => {
    const value = e.target.value;
    setForm((prev) => ({
      ...prev,
      selectedType: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const isExtra = form.selectedType === "extra";
    const amount = isExtra
      ? parseFloat(form.extraPayment)
      : parseFloat(form.monthlyPayment);
    if (isNaN(amount) || amount <= 0) {
      alert("Please enter a valid payment amount.");
      return;
    }

    try {
      const payload = {
        loanId: loan.loanId,
        paymentAmount: isExtra ? 0 : amount,
        extraPayment: isExtra ? amount : 0,
        remainingBalance: parseFloat(form.remainingBalance),
        nextDueDate: form.nextDueDate,
        paymentDate: form.paymentDate,
      };

      await axiosInstance.post(`/loan-payments/${loan.loanId}/pay`, payload);

      alert("✅ Payment successful");
      onClose();
      onSubmit?.();
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
                  <input
                    type="number"
                    name="remainingBalance"
                    value={form.remainingBalance}
                    disabled
                  />
                </td>
              </tr>

              <tr>
                <td>
                  <label>Next Due Date:</label>
                </td>
                <td>
                  <input
                    type="date"
                    name="nextDueDate"
                    value={form.nextDueDate}
                    disabled
                  />
                </td>
              </tr>

              <tr>
                <td colSpan="2">
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
                <td colSpan="2" style={{ textAlign: "center" }}>
                  <button type="submit" className="save-btn">
                    Save
                  </button>
                  <button
                    type="button"
                    onClick={onClose}
                    className="cancel-btn"
                  >
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

// import React, { useState, useEffect } from "react";
// import "../styles/loan.css";

// const LoanPaymentForm = ({ loan, onClose, onSubmit }) => {
//   const [form, setForm] = useState({
//     monthlyPayment: loan.monthlyPayment || 0,
//     extraPayment: 0,
//     paymentDate: new Date().toISOString().split("T")[0],
//     selectedType: "monthly",
//   });

//   useEffect(() => {
//     if (loan) {
//       setForm((prev) => ({
//         ...prev,
//         monthlyPayment: loan.monthlyPayment || 0,
//       }));
//     }
//   }, [loan]);

//   const handleChange = (e) => {
//     const { name, value, type, checked } = e.target;

//     if (name === "selectedType") {
//       setForm((prev) => ({
//         ...prev,
//         selectedType: value,
//       }));
//     } else {
//       setForm((prev) => ({
//         ...prev,
//         [name]: type === "checkbox" ? checked : value,
//       }));
//     }
//   };

//   const handleSubmit = (e) => {
//     e.preventDefault();
//     const paymentData = {
//       loanId: loan.loanId,
//       paymentAmount:
//         form.selectedType === "monthly"
//           ? parseFloat(form.monthlyPayment)
//           : parseFloat(form.extraPayment),
//       isExtra: form.selectedType === "extra",
//       paymentDate: form.paymentDate,
//     };
//     onSubmit(paymentData);
//   };

//   return (
//     <div className="modal-overlay">
//       <div className="modal">
//         <h2>Make a Payment</h2>
//         <form onSubmit={handleSubmit}>
//           <table className="form-table">
//             <tbody>
//               <tr>
//                 <td>
//                   <label>Monthly Payment:</label>
//                 </td>
//                 <td>
//                   <input
//                     type="radio"
//                     name="selectedType"
//                     value="monthly"
//                     checked={form.selectedType === "monthly"}
//                     onChange={handleChange}
//                   />
//                 </td>
//                 <td>
//                   <input
//                     type="number"
//                     name="monthlyPayment"
//                     value={form.monthlyPayment}
//                     onChange={handleChange}
//                     disabled={form.selectedType !== "monthly"}
//                     required={form.selectedType === "monthly"}
//                   />
//                 </td>
//               </tr>

//               <tr>
//                 <td>
//                   <label>Extra Payment:</label>
//                 </td>
//                 <td>
//                   <input
//                     type="radio"
//                     name="selectedType"
//                     value="extra"
//                     checked={form.selectedType === "extra"}
//                     onChange={handleChange}
//                   />
//                 </td>
//                 <td>
//                   <input
//                     type="number"
//                     name="extraPayment"
//                     value={form.extraPayment}
//                     onChange={handleChange}
//                     disabled={form.selectedType !== "extra"}
//                     required={form.selectedType === "extra"}
//                   />
//                 </td>
//               </tr>

//               <tr>
//                 <td>
//                   <label>Payment Date:</label>
//                 </td>
//                 <td colSpan="2">
//                   <input
//                     type="date"
//                     name="paymentDate"
//                     value={form.paymentDate}
//                     onChange={handleChange}
//                     required
//                   />
//                 </td>
//               </tr>

//               <tr>
//                 <td colSpan="3" style={{ textAlign: "center" }}>
//                   <button type="submit" className="save-btn">
//                     Save
//                   </button>
//                   <button
//                     type="button"
//                     onClick={onClose}
//                     className="cancel-btn"
//                   >
//                     Cancel
//                   </button>
//                 </td>
//               </tr>
//             </tbody>
//           </table>
//         </form>
//       </div>
//     </div>
//   );
// };

// export default LoanPaymentForm;