package edu.uci.ics.khefner.service.billing.core;

import java.util.ArrayList;
import java.util.List;


public class Transactions {
    private String transactionId;
    private String state;
    private Amounts amount;
    private TransactionFee transaction_fee;
    private String create_time;
    private String update_time;
    private List<Order> items;


    public Transactions(String transactionId, String state,  Amounts amount, TransactionFee transaction_fee, String create_time, String update_time) {
        this.transactionId = transactionId;
        this.state = state;
        this.amount = new Amounts(amount);
        this.transaction_fee = new TransactionFee(transaction_fee);
        this.create_time = create_time;
        this.update_time = update_time;
        this.items = new ArrayList<Order>();
    }

    public void addOrder(Order order){
        this.items.add(order);
    }


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public Amounts getAmount() {
        return amount;
    }

    public void setAmounts(Amounts amount) {
        this.amount = new Amounts(amount);
    }

    public TransactionFee getTransaction_fee() {
        return transaction_fee;
    }

    public void setTransaction_fee(TransactionFee transaction_fee) {
        this.transaction_fee = new TransactionFee(transaction_fee);
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public List<Order> getItems() {
        return items;
    }

    public void setItems(List<Order> items) {
        this.items = new ArrayList<>(items);
    }
}
