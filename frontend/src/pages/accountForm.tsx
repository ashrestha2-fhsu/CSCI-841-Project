
// src/components/AccountForm.tsx
import { useState, useEffect } from "react";
import type { ChangeEvent, FormEvent } from "react";
import "../styles/accounts.css"; // or a separate modal-specific CSS if needed

const CURRENCIES = ["USD", "EUR", "GBP", "NGN"] as const;
const ACCOUNT_TYPES = [
  "Select",
  "CHECKING_ACCOUNT",
  "SAVINGS_ACCOUNT",
  "BANK",
  "CASH",
  "CREDIT_CARD",
  "WALLET",
  "STOCK",
  "OTHER",
] as const;

interface AccountFormData {
  name: string;
  type: string;
  balance: string;
  currency: string;
  institutionName: string;
  accountNumber: string;
  interestRate: string;
}

type AccountFormProps = {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: AccountFormData) => void;
  initialData?: Partial<AccountFormData>;
};

const AccountForm: React.FC<AccountFormProps> = ({
  isOpen,
  onClose,
  onSubmit,
  initialData,
}) => {
  const [formData, setFormData] = useState<AccountFormData>({
    name: "",
    type: "Select",
    balance: "",
    currency: "USD",
    institutionName: "",
    accountNumber: "",
    interestRate: "",
  });

  useEffect(() => {
    if (isOpen) {
      if (initialData) {
        setFormData({
          name: initialData.name ?? "",
          type: initialData.type ?? "BANK",
          balance: initialData.balance ?? "",
          currency: initialData.currency ?? "USD",
          institutionName: initialData.institutionName ?? "",
          accountNumber: initialData.accountNumber ?? "",
          interestRate: initialData.interestRate ?? "",
        });
      } else {
        // Reset form on Add mode
        setFormData({
          name: "",
          type: "Select",
          balance: "",
          currency: "USD",
          institutionName: "",
          accountNumber: "",
          interestRate: "",
        });
      }
    }
  }, [initialData, isOpen]);

  const handleChange = (
    e: ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    onSubmit(formData);
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal">
        <h2>{initialData ? "Edit Account" : "Add Account"}</h2>
        <form onSubmit={handleSubmit}>
          <table className="form-table">
            <tbody>
              <tr>
                <td>
                  <label>Account Name:</label>
                </td>
                <td>
                  <input
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    required
                  />
                </td>
                <td>
                  <label>Account Type:</label>
                </td>
                <td>
                  <select
                    name="type"
                    value={formData.type}
                    onChange={handleChange}
                  >
                    {ACCOUNT_TYPES.map((type) => (
                      <option key={type} value={type}>
                        {type}
                      </option>
                    ))}
                  </select>
                </td>
              </tr>

              <tr>
                <td>
                  <label>Balance:</label>
                </td>
                <td>
                  <input
                    type="number"
                    name="balance"
                    value={formData.balance}
                    onChange={handleChange}
                    required
                  />
                </td>
                <td>
                  <label>Currency:</label>
                </td>
                <td>
                  <select
                    name="currency"
                    value={formData.currency}
                    onChange={handleChange}
                  >
                    {CURRENCIES.map((curr) => (
                      <option key={curr}>{curr}</option>
                    ))}
                  </select>
                </td>
              </tr>

              <tr>
                <td>
                  <label>Account Number:</label>
                </td>
                <td>
                  <input
                    name="accountNumber"
                    value={formData.accountNumber}
                    onChange={handleChange}
                  />
                </td>
                <td>
                  <label>Interest Rate:</label>
                </td>
                <td>
                  <div style={{ display: "flex", alignItems: "center" }}>
                    <input
                      type="number"
                      name="interestRate"
                      value={formData.interestRate}
                      onChange={handleChange}
                      step="0.01"
                      style={{ flex: 1, marginRight: "5px" }}
                    />
                    <span>%</span>
                  </div>
                </td>
              </tr>

              <tr>
                <td>
                  <label>Institution Name:</label>
                </td>
                <td colSpan={3}>
                  <input
                    name="institutionName"
                    value={formData.institutionName}
                    onChange={handleChange}
                  />
                </td>
              </tr>

              <tr>
                <td colSpan={4} className="form-buttons-row">
                  <div className="form-buttons">
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
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </form>
      </div>
    </div>
  );
};

export default AccountForm;
