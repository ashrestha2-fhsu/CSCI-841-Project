

import React, { useEffect, useState } from "react";
import axiosInstance from "../services/axiosInstance";

interface Investment {
  investmentId?: number;
  assetName: string;
  investmentType: string;
  totalAmountInvested: number | string;
  amountInvested?: number | string; // used during reinvestment
  currentValue: number | string;
  purchaseDate: string;
  assetSymbol?: string;
  quantity?: number | string;
  currency?: string;
}

type FormMode = "add" | "edit" | "reinvest";

interface InvestmentFormProps {
  initialData?: Investment | null;
  mode?: FormMode;
  onClose: () => void;
  onSuccess: () => void;
}

const assetSymbols = ["AAPL", "BTC", "VGRO", "ETH", "TSLA", "GOOG"];

const InvestmentFormModal: React.FC<InvestmentFormProps> = ({
  initialData,
  mode = "add",
  onClose,
  onSuccess,
}) => {
  const [form, setForm] = useState<Investment>({
    assetName: "",
    investmentType: "STOCKS",
    totalAmountInvested: "",
    amountInvested: "",
    currentValue: "",
    purchaseDate: new Date().toISOString().split("T")[0],
    assetSymbol: "",
    quantity: "",
    currency: "USD",
  });

  useEffect(() => {
    if (initialData) {
      setForm({
        ...initialData,
        purchaseDate:
          mode === "reinvest"
            ? new Date().toISOString().split("T")[0]
            : initialData.purchaseDate?.split("T")[0],
        amountInvested: "",
        quantity: "",
        totalAmountInvested: initialData.totalAmountInvested || "",
        currentValue: initialData.currentValue || "",
        assetSymbol: initialData.assetSymbol || "",
        currency: initialData.currency || "USD",
      });
    }
  }, [initialData, mode]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const profile = await axiosInstance.get("/users/profile");
      const userId = profile.data.userId;

      if (mode === "reinvest" && initialData?.investmentId) {
        await axiosInstance.post(
          `/investments/${initialData.investmentId}/reinvest`,
          {
            amountInvested: form.amountInvested,
            reinvestedQuantity: form.quantity,
            userId,
          }
        );
        alert("✅ Reinvestment successful!");
      } else if (mode === "edit" && initialData?.investmentId) {
        await axiosInstance.put(
          `/investments/${initialData.investmentId}`,
          { ...form, userId }
        );
        alert("✅ Investment updated successfully");
      } else {
        await axiosInstance.post("/investments", { ...form, userId });
        alert("✅ Investment added successfully");
      }

      onSuccess();
      onClose();
    } catch (err) {
      console.error("Error submitting investment:", err);
      alert("❌ Failed to submit investment. Please try again.");
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <h2>
          {mode === "reinvest"
            ? "Reinvest"
            : mode === "edit"
            ? "Edit Investment"
            : "Add Investment"}
        </h2>
        <form onSubmit={handleSubmit} className="investment-form-grid">
          {[
            { label: "Asset Name", name: "assetName", type: "text" },
            {
              label: "Asset Symbol",
              name: "assetSymbol",
              type: "select",
              options: assetSymbols,
            },
            {
              label: "Type",
              name: "investmentType",
              type: "select",
              options: ["STOCKS", "CRYPTO", "MUTUAL_FUNDS", "REAL_ESTATE"],
            },
            { label: "Quantity", name: "quantity", type: "number" },
            { label: "Currency", name: "currency", type: "text" },
            {
              label: "Amount Invested",
              name:
                mode === "reinvest" ? "amountInvested" : "totalAmountInvested",
              type: "number",
            },
            {
              label: "Current Value",
              name: "currentValue",
              type: "number",
            },
            { label: "Purchase Date", name: "purchaseDate", type: "date" },
          ].map((field) => (
            <div key={field.name} className="form-group">
              <label>{field.label}:</label>
              {field.type === "select" ? (
                <select
                  name={field.name}
                  value={form[field.name as keyof Investment] as string}
                  onChange={handleChange}
                  required
                >
                  <option value="">-- Select --</option>
                  {field.options?.map((option) => (
                    <option key={option} value={option}>
                      {option}
                    </option>
                  ))}
                </select>
              ) : (
                <input
                  type={field.type}
                  name={field.name}
                  value={form[field.name as keyof Investment] as string}
                  onChange={handleChange}
                  required={["assetName", "amountInvested", "totalAmountInvested", "purchaseDate"].includes(
                    field.name
                  )}
                  disabled={mode === "reinvest" && field.name === "currentValue"}
                />
              )}
            </div>
          ))}

          <div className="form-actions" style={{ gridColumn: "1 / -1" }}>
            <button type="submit" className="btn btn-save">
              {mode === "reinvest"
                ? "Reinvest"
                : mode === "edit"
                ? "Update"
                : "Save"}
            </button>
            <button type="button" className="btn btn-cancel" onClick={onClose}>
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default InvestmentFormModal;


// import React, { useEffect, useState } from "react";
// import axiosInstance from "../services/axiosInstance";

// interface Investment {
//   investmentId?: number;
//   assetName: string;
//   investmentType: string;
//   totalAmountInvested: number | string;
//   amountInvested?: number | string; // used during reinvestment
//   currentValue: number | string;
//   purchaseDate: string;
//   assetSymbol?: string;
//   quantity?: number | string;
//   currency?: string;
// }

// type FormMode = "add" | "edit" | "reinvest";

// interface InvestmentFormProps {
//   initialData?: Investment | null;
//   mode?: FormMode;
//   onClose: () => void;
//   onSuccess: () => void;
// }

// const assetSymbols = ["AAPL", "BTC", "VGRO", "ETH", "TSLA", "GOOG"];

// const InvestmentFormModal: React.FC<InvestmentFormProps> = ({
//   initialData,
//   mode = "add",
//   onClose,
//   onSuccess,
// }) => {
//   const [form, setForm] = useState<Investment>({
//     assetName: "",
//     investmentType: "STOCKS",
//     totalAmountInvested: "",
//     amountInvested: "",
//     currentValue: "",
//     purchaseDate: new Date().toISOString().split("T")[0],
//     assetSymbol: "",
//     quantity: "",
//     currency: "USD",
//   });

//   useEffect(() => {
//     if (initialData) {
//       setForm({
//         ...initialData,
//         purchaseDate:
//           mode === "reinvest"
//             ? new Date().toISOString().split("T")[0]
//             : initialData.purchaseDate?.split("T")[0],
//         amountInvested: "",
//         quantity: "",
//         totalAmountInvested: initialData.totalAmountInvested || "",
//         currentValue: initialData.currentValue || "",
//         assetSymbol: initialData.assetSymbol || "",
//         currency: initialData.currency || "USD",
//       });
//     }
//   }, [initialData, mode]);

//   const handleChange = (
//     e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
//   ) => {
//     const { name, value } = e.target;
//     setForm((prev) => ({ ...prev, [name]: value }));
//   };

//   const handleSubmit = async (e: React.FormEvent) => {
//     e.preventDefault();
//     try {
//       const profile = await axiosInstance.get("/users/profile");
//       const userId = profile.data.userId;

//       if (mode === "reinvest" && initialData?.investmentId) {
//         await axiosInstance.post(
//           `/investments/${initialData.investmentId}/reinvest`,
//           {
//             amountInvested: form.amountInvested,
//             reinvestedQuantity: form.quantity, // ✅ CORRECTED key name
//             userId,
//           }
//         );
//         alert("✅ Reinvestment successful!");
//       } else if (mode === "edit" && initialData?.investmentId) {
//         await axiosInstance.put(
//           `/investments/${initialData.investmentId}`,
//           { ...form, userId }
//         );
//         alert("✅ Investment updated successfully");
//       } else {
//         await axiosInstance.post("/investments", { ...form, userId });
//         alert("✅ Investment added successfully");
//       }

//       onSuccess();
//       onClose();
//     } catch (err) {
//       console.error("Error submitting investment:", err);
//       alert("❌ Failed to submit investment. Please try again.");
//     }
//   };

//   return (
//     <div className="modal-overlay">
//       <div className="modal">
//         <h2>
//           {mode === "reinvest"
//             ? "Reinvest"
//             : mode === "edit"
//             ? "Edit Investment"
//             : "Add Investment"}
//         </h2>
//         <form onSubmit={handleSubmit} className="investment-form-grid">
//           {[
//             { label: "Asset Name", name: "assetName", type: "text" },
//             {
//               label: "Asset Symbol",
//               name: "assetSymbol",
//               type: "select",
//               options: assetSymbols,
//             },
//             {
//               label: "Type",
//               name: "investmentType",
//               type: "select",
//               options: ["STOCKS", "CRYPTO", "MUTUAL_FUNDS", "REAL_ESTATE"],
//             },
//             { label: "Quantity", name: "quantity", type: "number" },
//             { label: "Currency", name: "currency", type: "text" },
//             {
//               label: "Amount Invested",
//               name: mode === "reinvest" ? "amountInvested" : "totalAmountInvested",
//               type: "number",
//             },
//             ...(mode !== "reinvest"
//               ? [
//                   {
//                     label: "Current Value",
//                     name: "currentValue",
//                     type: "number",
//                   },
//                 ]
//               : []),
//             { label: "Purchase Date", name: "purchaseDate", type: "date" },
//           ].map((field) => (
//             <div key={field.name} className="form-group">
//               <label>{field.label}:</label>
//               {field.type === "select" ? (
//                 <select
//                   name={field.name}
//                   value={form[field.name as keyof Investment] as string}
//                   onChange={handleChange}
//                   required
//                 >
//                   <option value="">-- Select --</option>
//                   {field.options?.map((option) => (
//                     <option key={option} value={option}>
//                       {option}
//                     </option>
//                   ))}
//                 </select>
//               ) : (
//                 <input
//                   type={field.type}
//                   name={field.name}
//                   value={form[field.name as keyof Investment] as string}
//                   onChange={handleChange}
//                   required={["assetName", "amountInvested", "totalAmountInvested", "purchaseDate"].includes(
//                     field.name
//                   )}
//                   disabled={
//                     mode === "reinvest" && field.name === "currentValue"
//                   }
//                 />
//               )}
//             </div>
//           ))}

//           <div className="form-actions" style={{ gridColumn: "1 / -1" }}>
//             <button type="submit" className="btn btn-save">
//               {mode === "reinvest"
//                 ? "Reinvest"
//                 : mode === "edit"
//                 ? "Update"
//                 : "Save"}
//             </button>
//             <button type="button" className="btn btn-cancel" onClick={onClose}>
//               Cancel
//             </button>
//           </div>
//         </form>
//       </div>
//     </div>
//   );
// };

// export default InvestmentFormModal;

