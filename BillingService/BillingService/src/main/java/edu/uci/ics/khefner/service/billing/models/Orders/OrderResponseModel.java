package edu.uci.ics.khefner.service.billing.models.Orders;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.khefner.service.billing.core.Order;
import edu.uci.ics.khefner.service.billing.core.Transactions;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OrderResponseModel {

    private int resultCode;
    private String message;
    List<Transactions> transactions;
    String redirectURL;
    String token;

    public OrderResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
        this.transactions = new ArrayList<Transactions>();
        this.token = null;
        this.redirectURL = null;
    }

    public OrderResponseModel(int resultCode, String message, List<Transactions> items) {
        this.resultCode = resultCode;
        this.message = message;
        this.transactions = new ArrayList<Transactions>(items);
        this.token = null;
        this.redirectURL = null;

    }

    public OrderResponseModel(int resultCode, String message, String token, String redirectURL) {
        this.resultCode = resultCode;
        this.message = message;
        this.transactions = new ArrayList<Transactions>();
        this.token = token;
        this.redirectURL = redirectURL;

    }


    public void addTransaction(Transactions transaction){
        this.transactions.add(transaction);
    }

    public void addOrderToTransaction(String transactionId, Order order){
        for(Transactions trans: transactions){
            if(trans.getTransactionId().equals(transactionId)){
                trans.addOrder(order);
            }
        }

    }


    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Transactions> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transactions> transactions) {
        this.transactions = new ArrayList<Transactions>(transactions);
    }

    public String getRedirectURL() { return redirectURL; }

    public void setRedirectURL(String redirectURL) { this.redirectURL = redirectURL; }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }
}
