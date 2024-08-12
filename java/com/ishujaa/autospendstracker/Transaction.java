package com.ishujaa.autospendstracker;


public class Transaction {
    private int accountId, txnId;
    private double amount;
    private String note, date;

    public Transaction(int accountId, int txnId, double amount, String note, String date){
        this.accountId = accountId;
        this.txnId = txnId;
        this.amount = amount;
        this.note = note;
        this.date = date;
    }
    public Transaction(){}

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setTxnId(int txnId) {
        this.txnId = txnId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTxnId() {
        return txnId;
    }

    public int getAccountId() {
        return accountId;
    }

    public double getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }
}
