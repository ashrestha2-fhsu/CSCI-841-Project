

import React, { useEffect, useState } from "react";
import axiosInstance from "../services/axiosInstance";
import "../styles/investment.css";

interface InvestmentHistory {
  historyId: number;
  investmentId: number;
  currentValue: number;
  performance: number;
  recordedAt: string;
  returnsGenerated: number;
  totalAmountInvested: number;
  amountInvested: number;
}

interface Props {
  investmentId: number;
  onClose: () => void;
}

const InvestmentHistoryModal: React.FC<Props> = ({ investmentId, onClose }) => {
  const [history, setHistory] = useState<InvestmentHistory[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const res = await axiosInstance.get(`/investment_history/${investmentId}`);
        setHistory(res.data);
      } catch (err) {
        console.error("Error fetching history:", err);
        alert("❌ Could not load history.");
      } finally {
        setLoading(false);
      }
    };
    fetchHistory();
  }, [investmentId]);

  return (
    <div className="modal-overlay">
      <div className="modal">
        <h3>Investment History</h3>
        <button className="btn btn-cancel" onClick={onClose}>Close</button>

        {loading ? (
          <p>Loading...</p>
        ) : (
          <table className="investment-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Amount Reinvested</th>
                <th>Total Invested</th>
                <th>Current Value</th>
                <th>Performance (%)</th>
                <th>Returns</th>
              </tr>
            </thead>
            <tbody>
              {history.map((record) => (
                <tr key={record.historyId}>
                  <td>{record.recordedAt.split("T")[0]}</td>
                  <td>${record.amountInvested.toFixed(2)}</td>
                  <td>${record.totalAmountInvested.toFixed(2)}</td>
                  <td>${record.currentValue.toFixed(2)}</td>
                  <td>{record.performance.toFixed(2)}%</td>
                  <td>${record.returnsGenerated.toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default InvestmentHistoryModal;
