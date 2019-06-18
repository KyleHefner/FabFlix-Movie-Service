package edu.uci.ics.khefner.service.billing.core;

import java.util.Date;

public class Order {
    private String email;
    private String movieId;
    private int quantity;
    private float unit_price;
    private float discount;
    private String saleDate;

    public Order(String email, String movieId, int quantity, float unit_price, float discount,String saleDate) {
        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;
        this.saleDate = saleDate;
        this.discount = discount;
        this.unit_price = unit_price;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public float getUnit_price() { return unit_price; }

    public void setUnit_price(float unit_price) { this.unit_price = unit_price; }

    public float getDiscount() { return discount; }

    public void setDiscount(float discount) { this.discount = discount; }
}
