package edu.uci.ics.khefner.service.billing.models.Customers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.billing.core.Customer;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CustomerResponseModel {

    @JsonProperty(value = "resultCode")
    int resultCode;
    @JsonProperty(value = "message")
    String message;
    Customer customer;


    public CustomerResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
        this.customer = null;

    }

    public CustomerResponseModel(int resultCode, String message, Customer customer){
        this.resultCode = resultCode;
        this.message = message;
        this.customer = new Customer(customer.getEmail(), customer.getFirstName(), customer.getLastName(), customer.getCcId(), customer.getAddress());

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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
