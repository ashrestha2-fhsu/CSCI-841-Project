// src/components/LoanPaymentForm.tsx
import { useState, useEffect } from "react";
import axiosInstance from "../services/axiosInstance";
import "../styles/loan.css";

type SelectedType = "monthly" | "extra";

interface Loan {
  loanId: number;
  monthlyPayment?: number;
  outstandingBalance?: number;
  dueDate?: string; // YYYY-MM-DD
}

interface LoanPaymentFormProps {
  loan: Loan;
  onClose: () => void;
  onSubmit?: () => void;
}

interface ProfileResponse {
  userId: number;
}

interface Account {
  accountId: number;
  name: string;
  balance?: number;
  deleted?: boolean;
}

// Match your backend enum exactly
const PAYMENT_METHODS = [
  "CASH",
  "DEBIT_CARD",
  "CREDIT_CARD",
  "MOBILE_MONEY",
  "INTERNAL_ACCOUNT",
  "PAYPAL",
  "OTHER",
] as const;

const pretty = (v: string) =>
  v.replace(/_/g, " ").toLowerCase().replace(/\b\w/g, (c) => c.toUpperCase());

interface FormState {
  monthlyPayment: number | string;
  extraPayment: number | string;
  paymentDate: string; // YYYY-MM-DD
  selectedType: SelectedType;
  userId: number | null;
  remainingBalance: number | string;
  nextDueDate: string; // YYYY-MM-DD
  // new fields
  paymentMethod: string;
  accountId: string; // required when INTERNAL_ACCOUNT
  externalReference: string;
}

const LoanPaymentForm: React.FC<LoanPaymentFormProps> = ({ loan, onClose, onSubmit }) => {
  const [form, setForm] = useState<FormState>({
    monthlyPayment: loan?.monthlyPayment ?? 0,
    extraPayment: 0,
    paymentDate: new Date().toISOString().split("T")[0],
    selectedType: "monthly",
    userId: null,
    remainingBalance: loan?.outstandingBalance ?? 0,
    nextDueDate: loan?.dueDate ?? "",
    paymentMethod: "INTERNAL_ACCOUNT",
    accountId: "",
    externalReference: "",
  });

  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loadingAccounts, setLoadingAccounts] = useState(false);

  useEffect(() => {
    if (loan) {
      setForm((prev) => ({
        ...prev,
        monthlyPayment: loan.monthlyPayment ?? 0,
        remainingBalance: loan.outstandingBalance ?? 0,
        nextDueDate: loan.dueDate ?? "",
      }));
    }
    fetchUser();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [loan]);

  useEffect(() => {
    if (form.userId) fetchAccounts(form.userId);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [form.userId]);

  // ---- APIs (use your existing controllers) ----
  const fetchUser = async (): Promise<void> => {
    try {
      const profile = await axiosInstance.get<ProfileResponse>("/users/profile");
      setForm((prev) => ({ ...prev, userId: profile.data.userId }));
    } catch (err) {
      console.error("Error fetching user:", err);
    }
  };

  const fetchAccounts = async (userId: number) => {
    setLoadingAccounts(true);
    try {
      const res = await axiosInstance.get<Account[]>(`/accounts/user/${userId}`);
      setAccounts((res.data || []).filter((a) => !a.deleted));
    } catch (err) {
      console.error("Error fetching accounts:", err);
      setAccounts([]);
    } finally {
      setLoadingAccounts(false);
    }
  };

  // ---- Handlers ----
  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ): void => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleRadioChange = (e: React.ChangeEvent<HTMLInputElement>): void => {
    const value = e.target.value as SelectedType;
    setForm((prev) => ({ ...prev, selectedType: value }));
  };

  const handleSubmit = async (e: React.FormEvent): Promise<void> => {
    e.preventDefault();

    const isExtra = form.selectedType === "extra";
    const amount = isExtra
      ? parseFloat(String(form.extraPayment))
      : parseFloat(String(form.monthlyPayment));

    if (isNaN(amount) || amount <= 0) {
      alert("Please enter a valid payment amount.");
      return;
    }

    if (form.paymentMethod === "INTERNAL_ACCOUNT" && !form.accountId) {
      alert("Please choose the account to pay from.");
      return;
    }

    // Controller expects @RequestParam → send as query params (no JSON body)
    const params = {
      paymentAmount: isExtra ? 0 : amount,
      extraPayment: isExtra ? amount : 0,
      paymentMethod: form.paymentMethod,
      accountId:
        form.paymentMethod === "INTERNAL_ACCOUNT" ? Number(form.accountId) : null,
      externalReference: form.externalReference || null,
      paymentDate: form.paymentDate, // YYYY-MM-DD
    };

    try {
      await axiosInstance.post(
        `/loan-payments/${loan.loanId}/pay`,
        null,
        { params }
      );

      alert("✅ Payment successful");
      onClose();
      onSubmit?.();
    } catch (err) {
      console.error("Payment failed:", err);
      alert("❌ Payment failed. Check required fields or try again.");
    }
  };

  // ---- UI ----
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

              {/* Payment Method */}
              <tr>
                <td><label>Payment Method:</label></td>
                <td>
                  <select
                    name="paymentMethod"
                    value={form.paymentMethod}
                    onChange={handleChange}
                  >
                    {PAYMENT_METHODS.map((m) => (
                      <option key={m} value={m}>
                        {pretty(m)}
                      </option>
                    ))}
                  </select>
                </td>
              </tr>

              {/* Pay-from Account (only for INTERNAL_ACCOUNT) */}
              {form.paymentMethod === "INTERNAL_ACCOUNT" && (
                <tr>
                  <td><label>Pay From Account:</label></td>
                  <td>
                    <select
                      name="accountId"
                      value={form.accountId}
                      onChange={handleChange}
                      required
                      disabled={loadingAccounts}
                    >
                      <option value="">
                        {loadingAccounts ? "Loading accounts..." : "-- Select account --"}
                      </option>
                      {accounts.map((a) => (
                        <option key={a.accountId} value={String(a.accountId)}>
                          {a.name}
                          {a.balance !== undefined
                            ? ` • $${Number(a.balance).toFixed(2)}`
                            : ""}
                        </option>
                      ))}
                    </select>
                  </td>
                </tr>
              )}

              {/* Optional external reference */}
              <tr>
                <td><label>External Reference:</label></td>
                <td>
                  <input
                    type="text"
                    name="externalReference"
                    value={form.externalReference}
                    onChange={handleChange}
                    placeholder="e.g., bank txn id / momo ref / receipt"
                  />
                </td>
              </tr>

              <tr>
                <td><label>Remaining Balance:</label></td>
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
                <td><label>Next Due Date:</label></td>
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
                  <button type="submit" className="save-btn">Save</button>
                  <button type="button" onClick={onClose} className="cancel-btn">Cancel</button>
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




// // src/components/LoanPaymentForm.tsx
// import React, { useState, useEffect } from "react";
// import axiosInstance from "../services/axiosInstance";
// import "../styles/loan.css";

// type SelectedType = "monthly" | "extra";

// interface Loan {
//   loanId: number;
//   monthlyPayment?: number;
//   outstandingBalance?: number;
//   dueDate?: string; // YYYY-MM-DD
// }

// interface LoanPaymentFormProps {
//   loan: Loan;
//   onClose: () => void;
//   onSubmit?: () => void;
// }

// interface ProfileResponse {
//   userId: number;
// }

// interface FormState {
//   monthlyPayment: number | string;
//   extraPayment: number | string;
//   paymentDate: string; // YYYY-MM-DD
//   selectedType: SelectedType;
//   userId: number | null;
//   remainingBalance: number | string;
//   nextDueDate: string; // YYYY-MM-DD
// }

// const LoanPaymentForm: React.FC<LoanPaymentFormProps> = ({ loan, onClose, onSubmit }) => {
//   const [form, setForm] = useState<FormState>({
//     monthlyPayment: loan?.monthlyPayment ?? 0,
//     extraPayment: 0,
//     paymentDate: new Date().toISOString().split("T")[0],
//     selectedType: "monthly",
//     userId: null,
//     remainingBalance: loan?.outstandingBalance ?? 0,
//     nextDueDate: loan?.dueDate ?? "",
//   });

//   useEffect(() => {
//     if (loan) {
//       setForm((prev) => ({
//         ...prev,
//         monthlyPayment: loan.monthlyPayment ?? 0,
//         remainingBalance: loan.outstandingBalance ?? 0,
//         nextDueDate: loan.dueDate ?? "",
//       }));
//     }
//     fetchUser();
//     // eslint-disable-next-line react-hooks/exhaustive-deps
//   }, [loan]);

//   const fetchUser = async (): Promise<void> => {
//     try {
//       const profile = await axiosInstance.get<ProfileResponse>("/users/profile");
//       setForm((prev) => ({ ...prev, userId: profile.data.userId }));
//     } catch (err) {
//       console.error("Error fetching user:", err);
//     }
//   };

//   const handleChange = (
//     e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
//   ): void => {
//     const { name, value } = e.target;
//     setForm((prev) => ({
//       ...prev,
//       [name]: value,
//     }));
//   };

//   const handleRadioChange = (e: React.ChangeEvent<HTMLInputElement>): void => {
//     const value = e.target.value as SelectedType;
//     setForm((prev) => ({
//       ...prev,
//       selectedType: value,
//     }));
//   };

//   const handleSubmit = async (e: React.FormEvent): Promise<void> => {
//     e.preventDefault();
//     const isExtra = form.selectedType === "extra";
//     const amount = isExtra
//       ? parseFloat(String(form.extraPayment))
//       : parseFloat(String(form.monthlyPayment));

//     if (isNaN(amount) || amount <= 0) {
//       alert("Please enter a valid payment amount.");
//       return;
//     }

//     try {
//       const payload = {
//         loanId: loan.loanId,
//         paymentAmount: isExtra ? 0 : amount,
//         extraPayment: isExtra ? amount : 0,
//         remainingBalance: parseFloat(String(form.remainingBalance)),
//         nextDueDate: form.nextDueDate,
//         paymentDate: form.paymentDate,
//       };

//       await axiosInstance.post(`/loan-payments/${loan.loanId}/pay`, payload);

//       alert("✅ Payment successful");
//       onClose();
//       onSubmit?.();
//     } catch (err) {
//       console.error("Payment failed:", err);
//       alert("❌ Payment failed. Check required fields or try again.");
//     }
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
//                   <label>
//                     Monthly Payment:
//                     <input
//                       type="radio"
//                       name="selectedType"
//                       value="monthly"
//                       checked={form.selectedType === "monthly"}
//                       onChange={handleRadioChange}
//                     />
//                   </label>
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
//                   <label>
//                     Extra Payment:
//                     <input
//                       type="radio"
//                       name="selectedType"
//                       value="extra"
//                       checked={form.selectedType === "extra"}
//                       onChange={handleRadioChange}
//                     />
//                   </label>
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
//                   <label>Remaining Balance:</label>
//                 </td>
//                 <td>
//                   <input
//                     type="number"
//                     name="remainingBalance"
//                     value={form.remainingBalance}
//                     disabled
//                   />
//                 </td>
//               </tr>

//               <tr>
//                 <td>
//                   <label>Next Due Date:</label>
//                 </td>
//                 <td>
//                   <input
//                     type="date"
//                     name="nextDueDate"
//                     value={form.nextDueDate}
//                     disabled
//                   />
//                 </td>
//               </tr>

//               <tr>
//                 <td colSpan={2}>
//                   <label>Payment Date:</label>
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
//                 <td colSpan={2} style={{ textAlign: "center" }}>
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
