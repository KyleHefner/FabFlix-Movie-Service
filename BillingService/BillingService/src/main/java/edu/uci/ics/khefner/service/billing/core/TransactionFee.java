package edu.uci.ics.khefner.service.billing.core;

public class TransactionFee {

    private String value;
    private String currency;

    public TransactionFee(String value, String currency) {
        this.value = value;
        this.currency = currency;
    }

    public TransactionFee(TransactionFee TFee){
        this.value = TFee.getValue();
        this.currency = TFee.getCurrency();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
