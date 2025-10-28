
// src/components/LoanPayment.tsx
import React, { useEffect, useState } from "react";
import axiosInstance from "../services/axiosInstance";
import "../styles/loan.css";

interface Loan {
  loanId: number;
  lenderName: string;
}

interface Payment {
  paymentId: number;
  paymentAmount?: number | string;
  extraPayment?: number | string;
  principalPaid?: number | string;
  interestPaid?: number | string;
  totalAmountPaid?: number | string;
  remainingBalance?: number | string;
  lastPaymentDate?: string;  // ISO
  nextDueDate?: string;      // ISO
  paymentDate?: string;      // ISO
}

interface LoanPaymentProps {
  loan?: Loan | null;
  onClose?: () => void; // ← make optional
}

const money = (v?: number | string) =>
  v === undefined || v === null ? "$0.00" : `$${Number(v).toFixed(2)}`;

const day = (iso?: string) => (iso ? iso.split("T")[0] : "");

const LoanPayment: React.FC<LoanPaymentProps> = ({ loan, onClose }) => {
  const [payments, setPayments] = useState<Payment[]>([]);
  const handleClose = onClose ?? (() => window.history.back()); // ← safe default

  useEffect(() => {
    if (loan?.loanId) {
      fetchPayments();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [loan?.loanId]);

  const fetchPayments = async (): Promise<void> => {
    if (!loan?.loanId) return;
    try {
      const res = await axiosInstance.get<Payment[]>(`/loan-payments/${loan.loanId}`);
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
        <button onClick={handleClose} className="close-btns">✖</button>

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
                  <td>{money(p.paymentAmount)}</td>
                  <td>{money(p.extraPayment)}</td>
                  <td>{money(p.principalPaid)}</td>
                  <td>{money(p.interestPaid)}</td>
                  <td>{money(p.totalAmountPaid)}</td>
                  <td>{money(p.remainingBalance)}</td>
                  <td>{day(p.lastPaymentDate)}</td>
                  <td>{day(p.nextDueDate)}</td>
                  <td>{day(p.paymentDate)}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={9}>No payments found for this loan.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default LoanPayment;



// // src/components/LoanPayment.tsx
// import React, { useEffect, useState } from "react";
// import axiosInstance from "../services/axiosInstance";
// import "../styles/loan.css";

// interface Loan {
//   loanId: number;
//   lenderName: string;
// }

// interface Payment {
//   paymentId: number;
//   paymentAmount?: number | string;
//   extraPayment?: number | string;
//   principalPaid?: number | string;
//   interestPaid?: number | string;
//   totalAmountPaid?: number | string;
//   remainingBalance?: number | string;
//   lastPaymentDate?: string;  // ISO
//   nextDueDate?: string;      // ISO
//   paymentDate?: string;      // ISO
// }

// interface LoanPaymentProps {
//   loan?: Loan | null;
//   onClose: () => void;
// }

// const money = (v?: number | string) =>
//   v === undefined || v === null ? "$0.00" : `$${Number(v).toFixed(2)}`;

// const day = (iso?: string) => (iso ? iso.split("T")[0] : "");

// const LoanPayment: React.FC<LoanPaymentProps> = ({ loan, onClose }) => {
//   const [payments, setPayments] = useState<Payment[]>([]);

//   useEffect(() => {
//     if (loan?.loanId) {
//       fetchPayments();
//     }
//     // eslint-disable-next-line react-hooks/exhaustive-deps
//   }, [loan?.loanId]);

//   const fetchPayments = async (): Promise<void> => {
//     try {
//       const res = await axiosInstance.get<Payment[]>(
//         `/loan-payments/${loan!.loanId}`
//       );
//       if (Array.isArray(res.data)) {
//         setPayments(res.data);
//       } else {
//         console.warn("⚠️ Unexpected payment data:", res.data);
//         setPayments([]);
//       }
//     } catch (err) {
//       console.error("Error fetching payments:", err);
//       setPayments([]);
//     }
//   };

//   return (
//     <div className="modal-overlay">
//       <div className="modal loan-payment-modal">
//         <h2>Payments for Loan - {loan?.lenderName || "Loading..."}</h2>
//         <button onClick={onClose} className="close-btns">
//           ✖
//         </button>

//         <table className="loan-table">
//           <thead>
//             <tr>
//               <th>Payment</th>
//               <th>Extra</th>
//               <th>Principal</th>
//               <th>Interest</th>
//               <th>Total Paid</th>
//               <th>Remaining</th>
//               <th>Last Paid</th>
//               <th>Next Due</th>
//               <th>Payment Date</th>
//             </tr>
//           </thead>
//           <tbody>
//             {Array.isArray(payments) && payments.length > 0 ? (
//               payments.map((p) => (
//                 <tr key={p.paymentId}>
//                   <td>{money(p.paymentAmount)}</td>
//                   <td>{money(p.extraPayment)}</td>
//                   <td>{money(p.principalPaid)}</td>
//                   <td>{money(p.interestPaid)}</td>
//                   <td>{money(p.totalAmountPaid)}</td>
//                   <td>{money(p.remainingBalance)}</td>
//                   <td>{day(p.lastPaymentDate)}</td>
//                   <td>{day(p.nextDueDate)}</td>
//                   <td>{day(p.paymentDate)}</td>
//                 </tr>
//               ))
//             ) : (
//               <tr>
//                 <td colSpan={9}>No payments found for this loan.</td>
//               </tr>
//             )}
//           </tbody>
//         </table>
//       </div>
//     </div>
//   );
// };

// export default LoanPayment;
