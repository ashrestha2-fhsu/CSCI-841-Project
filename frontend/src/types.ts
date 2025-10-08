// src/types.ts
export interface Loan {
  loanId: string;
  lenderName: string;
  amountBorrowed: string; // or number
  numberOfYears: number;
  interestRate: string; // or number
  monthlyPayment: string; // or number
  outstandingBalance: string; // or number
  dueDate: string;
  status: "ACTIVE" | "PAID_OFF" | "DEFAULTED";
}

export interface Payment {
  paymentId: string;
  paymentAmount: number;
  extraPayment: number;
  principalPaid: number;
  interestPaid: number;
  totalAmountPaid: number;
  remainingBalance: number;
  lastPaymentDate?: string;
  nextDueDate?: string;
  paymentDate?: string;
}
