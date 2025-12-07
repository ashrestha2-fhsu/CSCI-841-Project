

import React, { useEffect, useState } from "react";
import axiosInstance from "../services/axiosInstance";
import InvestmentFormModal from "../pages/investmentFormModal";
import InvestmentHistoryModal from "../pages/investmentHistory";
import "../styles/investment.css";

type InvestmentType = "STOCKS" | "CRYPTO" | "MUTUAL_FUNDS" | "REAL_ESTATE";

interface Investment {
  investmentId: number;
  assetName: string;
  investmentType: InvestmentType;
  totalAmountInvested: number;
  currentValue: number;
  purchaseDate: string;
  performance: number;
  assetSymbol?: string;
  quantity?: number;
  currency?: string;
  isDeleted: boolean;
}

const Investment: React.FC = () => {
  const [investments, setInvestments] = useState<Investment[]>([]);
  const [userId, setUserId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [editInvestment, setEditInvestment] = useState<Investment | null>(null);
  const [formMode, setFormMode] = useState<'edit' | 'add' | 'reinvest'>('add');
  const [showHistory, setShowHistory] = useState(false);
  const [selectedInvestmentId, setSelectedInvestmentId] = useState<number | null>(null);

  const fetchInvestments = async () => {
    if (!userId) return;
    try {
      const res = await axiosInstance.get(`/investments/user/${userId}`);
      setInvestments(res.data);
    } catch (err) {
      console.error("Error fetching investments:", err);
    }
  };

  useEffect(() => {
    const fetchUserProfile = async () => {
      try {
        const res = await axiosInstance.get("/users/profile");
        setUserId(res.data.userId);
      } catch (err) {
        console.error("Error fetching user profile:", err);
      }
    };
    fetchUserProfile();
  }, []);

  useEffect(() => {
    if (userId) fetchInvestments();
  }, [userId]);

  const handleDelete = async (id: number) => {
    if (!window.confirm("Are you sure you want to delete this investment?")) return;
    try {
      await axiosInstance.delete(`/investments/${id}`);
      fetchInvestments();
    } catch (err) {
      console.error("Delete failed:", err);
    }
  };

  const handleRestore = async (id: number) => {
    try {
      await axiosInstance.put(`/investments/${id}/restore`);
      fetchInvestments();
    } catch (err) {
      console.error("Restore failed:", err);
    }
  };

  const handleViewHistory = (investmentId: number) => {
    setSelectedInvestmentId(investmentId);
    setShowHistory(true);
  };

  const handleReinvest = (inv: Investment) => {
    setEditInvestment(inv);
    setFormMode('reinvest');
    setShowForm(true);
  };

  return (
    <div className="investment-container">
      <h2 className="investment-title">
        <i className="fas fa-chart-line"></i> My Investments
      </h2>

      <button
        className="investment-add-btn"
        onClick={() => {
          setEditInvestment(null);
          setFormMode('add');
          setShowForm(true);
        }}
      >
        ➕ Add Investment
      </button>

      <table className="investment-table">
        <thead>
          <tr>
            <th>Asset</th>
            <th>Type</th>
            <th>Symbol</th>
            <th>Quantity</th>
            <th>Currency</th>
            <th>Total Invested</th>
            <th>Current</th>
            <th>Performance</th>
            <th>Purchase Date</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {investments.map((inv) => (
            <tr key={inv.investmentId} className={inv.isDeleted ? "deleted-row" : ""}>
              <td>{inv.assetName}</td>
              <td>{inv.investmentType}</td>
              <td>{inv.assetSymbol}</td>
              <td>{inv.quantity}</td>
              <td>{inv.currency}</td>
              <td>${inv.totalAmountInvested.toFixed(2)}</td>
              <td>${inv.currentValue.toFixed(2)}</td>
              <td>{inv.performance.toFixed(2)}%</td>
              <td>{inv.purchaseDate.split("T")[0]}</td>
              <td>
                {!inv.isDeleted ? (
                  <>
                    <button
                      className="edit-btn"
                      onClick={() => {
                        setEditInvestment(inv);
                        setFormMode('edit');
                        setShowForm(true);
                      }}
                    >
                      Edit
                    </button>
                    <button
                      className="reinvest-btn"
                      onClick={() => handleReinvest(inv)}
                    >
                      Reinvest
                    </button>
                    <button
                      className="delete-btn"
                      onClick={() => handleDelete(inv.investmentId)}
                    >
                      Delete
                    </button>
                    <button
                      className="view-btn"
                      onClick={() => handleViewHistory(inv.investmentId)}
                    >
                      View
                    </button>
                  </>
                ) : (
                  <button
                    className="restore-btn"
                    onClick={() => handleRestore(inv.investmentId)}
                  >
                    Restore
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {showForm && (
        <InvestmentFormModal
          initialData={editInvestment}
          mode={formMode}
          onClose={() => {
            setShowForm(false);
            fetchInvestments();
          }}
          onSuccess={fetchInvestments}
        />
      )}

      {showHistory && selectedInvestmentId && (
        <InvestmentHistoryModal
          investmentId={selectedInvestmentId}
          onClose={() => {
            setShowHistory(false);
            setSelectedInvestmentId(null);
          }}
        />
      )}
    </div>
  );
};

export default Investment;



// import React, { useEffect, useState } from "react";
// import axiosInstance from "../services/axiosInstance";
// import InvestmentFormModal from "../pages/investmentFormModal";
// import InvestmentHistoryModal from "../pages/investmentHistory";


// // import InvestmentHistoryModal from "../pages/investmentHistoryModal";
// import "../styles/investment.css";

// type InvestmentType = "STOCKS" | "CRYPTO" | "MUTUAL_FUNDS" | "REAL_ESTATE";

// interface Investment {
//   investmentId: number;
//   assetName: string;
//   investmentType: InvestmentType;
//   totalAmountInvested: number;
//   currentValue: number;
//   purchaseDate: string;
//   performance: number;
//   assetSymbol?: string;
//   quantity?: number;
//   currency?: string;
//   isDeleted: boolean;
// }

// const Investment: React.FC = () => {
//   const [investments, setInvestments] = useState<Investment[]>([]);
//   const [userId, setUserId] = useState<number | null>(null);
//   const [showForm, setShowForm] = useState(false);
//   const [editInvestment, setEditInvestment] = useState<Investment | null>(null);
//   const [showHistory, setShowHistory] = useState(false);
//   const [selectedInvestmentId, setSelectedInvestmentId] = useState<number | null>(null);

//   const fetchInvestments = async () => {
//     if (!userId) return;
//     try {
//       const res = await axiosInstance.get(`/investments/user/${userId}`);
//       setInvestments(res.data);
//     } catch (err) {
//       console.error("Error fetching investments:", err);
//     }
//   };

//   useEffect(() => {
//     const fetchUserProfile = async () => {
//       try {
//         const res = await axiosInstance.get("/users/profile");
//         setUserId(res.data.userId);
//       } catch (err) {
//         console.error("Error fetching user profile:", err);
//       }
//     };
//     fetchUserProfile();
//   }, []);

//   useEffect(() => {
//     if (userId) fetchInvestments();
//   }, [userId]);

//   const handleDelete = async (id: number) => {
//     if (!window.confirm("Are you sure you want to delete this investment?")) return;
//     try {
//       await axiosInstance.delete(`/investments/${id}`);
//       fetchInvestments();
//     } catch (err) {
//       console.error("Delete failed:", err);
//     }
//   };

//   const handleRestore = async (id: number) => {
//     try {
//       await axiosInstance.put(`/investments/${id}/restore`);
//       fetchInvestments();
//     } catch (err) {
//       console.error("Restore failed:", err);
//     }
//   };

//   const handleViewHistory = (investmentId: number) => {
//     setSelectedInvestmentId(investmentId);
//     setShowHistory(true);
//   };

//   return (
//     <div className="investment-container">
//       <h2 className="investment-title">
//         <i className="fas fa-chart-line"></i> My Investments
//       </h2>

//       <button
//         className="investment-add-btn"
//         onClick={() => {
//           setEditInvestment(null);
//           setShowForm(true);
//         }}
//       >
//         ➕ Add Investment
//       </button>

//       <table className="investment-table">
//         <thead>
//           <tr>
//             <th>Asset</th>
//             <th>Type</th>
//             <th>Symbol</th>
//             <th>Quantity</th>
//             <th>Currency</th>
//             <th>Invested</th>
//             <th>Current</th>
//             <th>Performance</th>
//             <th>Purchase Date</th>
//             <th>Action</th>
//           </tr>
//         </thead>
//         <tbody>
//           {investments.map((inv) => (
//             <tr key={inv.investmentId} className={inv.isDeleted ? "deleted-row" : ""}>
//               <td>{inv.assetName}</td>
//               <td>{inv.investmentType}</td>
//               <td>{inv.assetSymbol}</td>
//               <td>{inv.quantity}</td>
//               <td>{inv.currency}</td>
//               <td>${inv.totalAmountInvested.toFixed(2)}</td>
//               <td>${inv.currentValue.toFixed(2)}</td>
//               <td>{inv.performance.toFixed(2)}%</td>
//               <td>{inv.purchaseDate.split("T")[0]}</td>
//               <td>
//                 {!inv.isDeleted ? (
//                   <>
//                     <button
//                       className="edit-btn"
//                       onClick={() => {
//                         setEditInvestment(inv);
//                         setShowForm(true);
//                       }}
//                     >
//                       Edit
//                     </button>
//                     <button
//                       className="delete-btn"
//                       onClick={() => handleDelete(inv.investmentId)}
//                     >
//                       Delete
//                     </button>
//                     <button
//                       className="view-btn"
//                       onClick={() => handleViewHistory(inv.investmentId)}
//                     >
//                       View
//                     </button>
//                   </>
//                 ) : (
//                   <button
//                     className="restore-btn"
//                     onClick={() => handleRestore(inv.investmentId)}
//                   >
//                     Restore
//                   </button>
//                 )}
//               </td>
//             </tr>
//           ))}
//         </tbody>
//       </table>

//       {showForm && (
//         <InvestmentFormModal
//           initialData={editInvestment}
//           onClose={() => {
//             setShowForm(false);
//             fetchInvestments();
//           }}
//           onSuccess={fetchInvestments}
//         />
//       )}

//       {showHistory && selectedInvestmentId && (
//         <InvestmentHistoryModal
//           investmentId={selectedInvestmentId}
//           onClose={() => {
//             setShowHistory(false);
//             setSelectedInvestmentId(null);
//           }}
//         />
//       )}
//     </div>
//   );
// };

// export default Investment;

