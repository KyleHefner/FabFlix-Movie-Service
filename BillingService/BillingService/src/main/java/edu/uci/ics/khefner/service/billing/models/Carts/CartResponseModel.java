package edu.uci.ics.khefner.service.billing.models.Carts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.billing.core.CartItem;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CartResponseModel {
    private int resultCode;
    private String message;
    private List<CartItem> items;

    @JsonCreator
    public CartResponseModel(@JsonProperty("resultCode") int resultCode, @JsonProperty("message") String message, @JsonProperty("items") List<CartItem> items) {
        this.resultCode = resultCode;
        this.message = message;
        this.items = new ArrayList<CartItem>(items);
    }

    @JsonCreator
    public CartResponseModel(@JsonProperty("resultCode") int resultCode, @JsonProperty("message") String message) {
        this.resultCode = resultCode;
        this.message = message;
        this.items = new ArrayList<CartItem>();
    }


    public void addToItemList(CartItem item){
        this.items.add(item);
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

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = new ArrayList<CartItem>(items);
    }


}
