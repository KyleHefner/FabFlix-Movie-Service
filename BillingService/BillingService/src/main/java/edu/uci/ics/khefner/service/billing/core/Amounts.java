package edu.uci.ics.khefner.service.billing.core;

public class Amounts {
    private String total;
    private String currency;

    public Amounts(String total, String currency) {
        this.total = total;
        this.currency = currency;
    }

    public Amounts(Amounts amount){
        this.total = amount.getTotal();
        this.currency = amount.getCurrency();
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
